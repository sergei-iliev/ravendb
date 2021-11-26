package com.luee.wally.admin.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.Entity;
import com.luee.wally.DB;
import com.luee.wally.admin.repository.CloudStorageRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.constants.Constants;
import com.luee.wally.csv.PaidUsers2018;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.utils.Utilities;

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
					String invoiceNumber;
					
					//needed for CSV list generation
					Collection<Pair<PaidUsers2018, Entity>> entities=new ArrayList<Pair<PaidUsers2018,Entity>>();
					
					ImportService importService = new ImportService();

					Collection<PaidUsers2018> users2019EUR = importService
							.importCSVFile2019(Constants.IMPORT_CSV_FILE_2019_eur_amount, true);

					Collection<PaidUsers2018> users2019Currency = importService
							.importCSVFile2019(Constants.IMPORT_CSV_FILE_2019_currency_amount, false);

					for (PaidUsers2018 user : users2019EUR) {
						
						Entity redeemingRequest = importService.getRedeemingRequestFromGuid(user.getUserGuid(),user.toDate());
						if (redeemingRequest != null) {
							invoiceNumber=prefix + String.valueOf(count++);
							user.setInvoiceNumber(invoiceNumber);
							entities.add(new ImmutablePair<>(user, redeemingRequest)); 
							// create pdf in cloud store
							importService.createPDFInCloudStore(redeemingRequest, user,"user_credit_notes_2019_with_id/PaidUsers2019_",
									invoiceNumber);
						} else {
							logger.log(Level.SEVERE, "No user entry found for - " + user.getUserGuid());
						}
					}

					for (PaidUsers2018 user : users2019Currency) {						
						if (!user.getCurrencyCode().equals("EUR")) {
							// convert to EUR
							resetUserCurrency(user);
						}
						Entity redeemingRequest = importService.getRedeemingRequestFromGuid(user.getUserGuid(),user.toDate());
						if (redeemingRequest != null) {
							invoiceNumber=prefix + String.valueOf(count++);
							user.setInvoiceNumber(invoiceNumber);
							entities.add(new ImmutablePair<>(user, redeemingRequest));
							// create pdf in cloud store
							importService.createPDFInCloudStore(redeemingRequest, user,"user_credit_notes_2019_with_id/PaidUsers2019_",
									invoiceNumber);
						} else {
							logger.log(Level.SEVERE, "No user entry found for - " + user.getUserGuid());
						}
					}

					//save CSV file
					saveCSVFile(entities);
					
					logger.log(Level.WARNING, "*************************Background task finished*****************");

				} catch (Exception e) {
					logger.log(Level.SEVERE, "affs search service:", e);
				}

			}
		}).start();

		req.setAttribute("success", "Job 2019 user revenue successfully posted.");
		req.getRequestDispatcher("/jsp/user_revenue_2019.jsp").forward(req, resp);
	}
    private void saveCSVFile(Collection<Pair<PaidUsers2018, Entity>> entities)throws IOException{
       ImportService importService = new ImportService();
    	
  	   try(Writer writer=new StringWriter()){
		    importService.createCSVFile(writer,entities);
		  
		    CloudStorageRepository cloudStorageRepository=new CloudStorageRepository();
		    cloudStorageRepository.save(writer,"user_revenue/users_revenue_2019");
	   }    	
    }
	private void resetUserCurrency(PaidUsers2018 user) throws Exception {
		//keep original data
		user.setUserCurrencyCode(user.getCurrencyCode());
		user.setUserPayedAmount(user.getPayedAmount());
		
		ImportService importService = new ImportService();
		ExchangeRateVO exchangeRateVO = importService.getExchangeRates(user.getFormatedDate("YYYY-MM-dd"), "EUR");
		Double rate = exchangeRateVO.getRates().get(user.getCurrencyCode());
		BigDecimal currentValue = new BigDecimal(user.getPayedAmount());
		BigDecimal rateValue = BigDecimal.valueOf(rate);
		BigDecimal paidAmount = currentValue.divide(rateValue,2, BigDecimal.ROUND_HALF_EVEN);

		
		user.setPayedAmount(paidAmount.toString());
		user.setCurrencyCode("EUR");
	}

}