package com.luee.wally.admin.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.Entity;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.csv.PaidUsers2018;
import com.luee.wally.db.DB;
import com.luee.wally.json.ExchangeRateVO;

public class ImportController implements Controller {
	private static final Logger logger = Logger.getLogger(ImportController.class.getName());

	public void importUserRevenue2019(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// Queue queue = QueueFactory.getDefaultQueue();
		// queue.add(TaskOptions.Builder.withUrl("/administration/import/user/revenue/2019/background").method(Method.POST));

		ThreadManager.createBackgroundThread(new Runnable() {
			@Override
			public void run() {

				try {
					logger.log(Level.WARNING,
							"*************************Task in the background started ********************");
					String prefix = "2019111111";
					int count = 0;

					ImportService importService = new ImportService();

					Collection<PaidUsers2018> users2019EUR = importService
							.importCSVFile2019(ImportService.IMPORT_CSV_FILE_2019_eur_amount, true);

					Collection<PaidUsers2018> users2019Currency = importService
							.importCSVFile2019(ImportService.IMPORT_CSV_FILE_2019_currency_amount, false);

					for (PaidUsers2018 user : users2019EUR) {
						Entity redeemingRequest = DB.getRedeemingRequestFromGuid(user.getUserGuid());
						if (redeemingRequest != null) {
							// create pdf in cloud store
							importService.createPDFInCloudStore(redeemingRequest, user,"user_credit_notes_2019_with_id/PaidUsers2019_",
									prefix + String.valueOf(count++));
						} else {
							logger.log(Level.SEVERE, "No user entry found for - " + user.getUserGuid());
						}
					}

					for (PaidUsers2018 user : users2019Currency) {						
						if (!user.getCurrencyCode().equals("EUR")) {
							// convert to EUR
							resetUserCurrency(user);
						}
						Entity redeemingRequest = DB.getRedeemingRequestFromGuid(user.getUserGuid());
						if (redeemingRequest != null) {
							// create pdf in cloud store
							importService.createPDFInCloudStore(redeemingRequest, user,"user_credit_notes_2019_with_id/PaidUsers2019_",
									prefix + String.valueOf(count++));
						} else {
							logger.log(Level.SEVERE, "No user entry found for - " + user.getUserGuid());
						}
					}

					logger.log(Level.WARNING, "*************************Background task finished*****************");

				} catch (Exception e) {
					logger.log(Level.SEVERE, "affs search service:", e);
				}

			}
		}).start();

		req.setAttribute("success", "Job 2019 user revenue successfully posted.");
		req.getRequestDispatcher("/jsp/user_revenue_2019.jsp").forward(req, resp);
	}
/*
	public void importUserRevenue2019InBackground(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String prefix = "2019111111";
		int count = 0;

		ImportService importService = new ImportService();

		try {
			Collection<PaidUsers2018> users2019EUR = importService
					.importCSVFile2019(ImportService.IMPORT_CSV_FILE_2019_eur_amount, true);

			Collection<PaidUsers2018> users2019Currency = importService
					.importCSVFile2019(ImportService.IMPORT_CSV_FILE_2019_currency_amount, false);

			for (PaidUsers2018 user : users2019EUR) {
				Entity redeemingRequest = DB.getRedeemingRequestFromGuid(user.getUserGuid());
				if (redeemingRequest != null) {
					// create pdf in cloud store
					importService.createPDFInCloudStore(redeemingRequest, user, prefix + String.valueOf(count++));
				} else {
					logger.log(Level.SEVERE, "No user entry found for - " + user.getUserGuid());
				}
			}

			for (PaidUsers2018 user : users2019Currency) {
				if (user.getCurrencyCode() != "EUR") {
					// convert to EUR
					resetUserCurrency(user);
				}
				Entity redeemingRequest = DB.getRedeemingRequestFromGuid(user.getUserGuid());
				if (redeemingRequest != null) {
					// create pdf in cloud store
					importService.createPDFInCloudStore(redeemingRequest, user, prefix + String.valueOf(count++));
				} else {
					logger.log(Level.SEVERE, "No user entry found for - " + user.getUserGuid());
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "import csv:", e);
		}
	}
*/
	private void resetUserCurrency(PaidUsers2018 user) throws Exception {
		ImportService importService = new ImportService();
		ExchangeRateVO exchangeRateVO = importService.getExchangeRates(user.getFormatedDate("YYYY-MM-dd"), "EUR");
		Double rate = exchangeRateVO.getRates().get(user.getCurrencyCode());
System.out.println(user.getCurrencyCode());
System.out.println(rate);
		BigDecimal currentValue = new BigDecimal(user.getPayedAmount());
		BigDecimal rateValue = BigDecimal.valueOf(rate);
		BigDecimal paidAmount = currentValue.multiply(rateValue);

		user.setPayedAmount(paidAmount.setScale(4, BigDecimal.ROUND_HALF_EVEN).toString());
		user.setCurrencyCode("EUR");
	}

}
