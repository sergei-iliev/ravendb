package com.luee.wally.admin.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Pair;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.luee.wally.admin.repository.CloudStorageRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.AffsSearchService;
import com.luee.wally.api.service.FBAffsSearchService;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.command.AffsSearchForm;
import com.luee.wally.command.AffsSearchResult;
import com.luee.wally.command.FBAffsSearchResult;
import com.luee.wally.entity.Affs;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.utils.Utilities;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

public class FBAffsSearchController implements Controller {
	private final Logger logger = Logger.getLogger(FBAffsSearchController.class.getName());

	public void index(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("countries", this.getCountries());
		req.getRequestDispatcher("/jsp/fb_affs_search.jsp").forward(req, resp);
	}

	
	public void search(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final AffsSearchForm form = AffsSearchForm.parse(req);
		ThreadManager.createBackgroundThread(new Runnable() {
			@Override
			public void run() {

				try {
					logger.log(Level.WARNING,
							"*************************Task in the Facebook background started ********************");
					FBAffsSearchService fbAffsSearchService = new FBAffsSearchService();
					Collection<Pair<Collection<Key>, FBAffsSearchResult>> fbAffsSearchResults = fbAffsSearchService.processAffsSearch(form);
					for(Pair<Collection<Key>, FBAffsSearchResult> pair:fbAffsSearchResults){
						Collection<Key> affsKeys=pair.getKey();
						FBAffsSearchResult fbAffsSearchResult=pair.getValue();
						Map<String, Map<String, List<String>>> datePackageEcpmsMap=fbAffsSearchService.getECPMs(affsKeys);
						
						BigDecimal sum=fbAffsSearchService.calculateFBUserRevenue(datePackageEcpmsMap);
						fbAffsSearchResult.setTotalFBRev(sum);
					}
					
					Collection<FBAffsSearchResult> list=fbAffsSearchResults.stream().map(p->p.getValue()).collect(Collectors.toList());
					try (Writer writer = new StringWriter()) {
						fbAffsSearchService.createFile(writer, form,list);

						CloudStorageRepository cloudStorageRepository = new CloudStorageRepository();
						cloudStorageRepository.save(writer, "affs_ad_rev_search_fb/search" + formatDate(new Date()));
					}


					logger.log(Level.WARNING, "**********************Facebook task finished*****************");

				} catch (Exception e) {
					logger.log(Level.SEVERE, "Facebook affs search service:", e);
				}

			}
		}).start();		
		req.setAttribute("webform", form);
		req.setAttribute("countries", this.getCountries());
		req.setAttribute("success", "Job successfully posted.");
		req.getRequestDispatcher("/jsp/fb_affs_search.jsp").forward(req, resp);
	}
}
