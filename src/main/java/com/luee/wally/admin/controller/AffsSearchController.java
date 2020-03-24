package com.luee.wally.admin.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.luee.wally.admin.repository.CloudStorageRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.AffsSearchService;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.command.AffsSearchForm;
import com.luee.wally.command.AffsSearchResult;
import com.luee.wally.entity.Affs;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.utils.Utilities;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

public class AffsSearchController implements Controller {
	private final Logger logger = Logger.getLogger(AffsSearchController.class.getName());

	public void index(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setAttribute("countries", this.getCountries());
		req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
	}

	public void exportGaid(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("countries", this.getCountries());
		req.getRequestDispatcher("/jsp/export_gaid.jsp").forward(req, resp);
	}

	public void runExportGaid(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	  	final AffsSearchForm form = AffsSearchForm.parse(req);
		ThreadManager.createBackgroundThread(new Runnable() {
			@Override
			public void run() {

				try {
					logger.log(Level.WARNING,
							"*************************Export Gaid Task in the background started ********************");
					AffsSearchService affsSearchService = new AffsSearchService();
					affsSearchService.exportGaid(form);
					logger.log(Level.WARNING,
							"*************************Background export Gaid task finished*****************");
				} catch (Exception e) {
					logger.log(Level.SEVERE, "affs export service:", e);
				}
			}
		}).start();
	  	
		req.setAttribute("webform", form);
		req.setAttribute("countries", this.getCountries());
		req.setAttribute("success", "Job successfully posted to the queue.");
		req.getRequestDispatcher("/jsp/export_gaid.jsp").forward(req, resp);
	}

	public void search(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final AffsSearchForm form = AffsSearchForm.parse(req);

		if (form.getSubmitType().equals("export")) {
			ThreadManager.createBackgroundThread(new Runnable() {
				@Override
				public void run() {

					try {
						logger.log(Level.WARNING,
								"*************************Export Task in the background started ********************");
						AffsSearchService affsSearchService = new AffsSearchService();
						Collection<Affs> affsExportResults = affsSearchService.processAffsExport(form);

						try (Writer writer = new StringWriter()) {
							affsSearchService.createExportFile(writer, form, affsExportResults);

							CloudStorageRepository cloudStorageRepository = new CloudStorageRepository();
							cloudStorageRepository.save(writer, "affs_ad_rev_export/export" + formatDate(new Date()));
						}
						logger.log(Level.WARNING,
								"*************************Background export task finished*****************");
					} catch (Exception e) {
						logger.log(Level.SEVERE, "affs export service:", e);
					}
				}
			}).start();
		} else {
			ThreadManager.createBackgroundThread(new Runnable() {
				@Override
				public void run() {

					try {
						logger.log(Level.WARNING,
								"*************************Task in the background started ********************");
						AffsSearchService affsSearchService = new AffsSearchService();
						Collection<AffsSearchResult> affsSearchResults = affsSearchService.processAffsSearch(form);

						// read USD rate
						ImportService importService = new ImportService();
						String formatedDate = Utilities.formatedDate(new Date(), "yyyy-MM-dd");
						ExchangeRateVO rate = importService.getExchangeRates(formatedDate, "EUR", "USD");
						BigDecimal rateValue = BigDecimal.valueOf(rate.getRates().get("USD"));
						for (AffsSearchResult affsSearchResult : affsSearchResults) {
							affsSearchResult.setRateValue(rateValue);
						}

						try (Writer writer = new StringWriter()) {
							affsSearchService.createFile(writer, form, affsSearchResults);

							CloudStorageRepository cloudStorageRepository = new CloudStorageRepository();
							cloudStorageRepository.save(writer, "affs_ad_rev_search/search" + formatDate(new Date()));
						}

						// for(AffsSearchResult result:affsSearchResults){
						// logger.log(Level.WARNING,"experiment="+result.getExperiment());
						// logger.log(Level.WARNING,"totalAdRev="+result.getTotalAdRev());
						// logger.log(Level.WARNING,"offerwallRev="+result.getOfferwallRev());
						//
						// logger.log(Level.WARNING,"Records
						// #="+result.getCount());
						// if(result.getCount()!=0){
						// BigDecimal
						// avrTotalAdRev=result.getTotalAdRev().divide(new
						// BigDecimal(result.getCount()),4,
						// BigDecimal.ROUND_HALF_EVEN);
						// logger.log(Level.WARNING,"avrTotalAdRev="+avrTotalAdRev);
						//
						// BigDecimal
						// avrOfferwallRev=result.getOfferwallRev().divide(new
						// BigDecimal(result.getCount()),4,
						// BigDecimal.ROUND_HALF_EVEN);
						// logger.log(Level.WARNING,"avrOfferwallRev="+avrOfferwallRev);
						// }
						// }

						logger.log(Level.WARNING, "*************************Background task finished*****************");

					} catch (Exception e) {
						logger.log(Level.SEVERE, "affs search service:", e);
					}

				}
			}).start();
		}
		req.setAttribute("webform", form);
		req.setAttribute("countries", this.getCountries());
		req.setAttribute("success", "Job successfully posted.");
		req.getRequestDispatcher("/jsp/index.jsp").forward(req, resp);
	}

}
