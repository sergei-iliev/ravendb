package com.luee.wally.admin.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.luee.wally.admin.repository.CloudStorageRepository;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.api.route.Controller;
import com.luee.wally.api.service.impex.ExportService;
import com.luee.wally.command.ExportPaidUsersForm;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.entity.PaidUserExternal;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.utils.Utilities;

public class ExportController implements Controller {

	private static final Logger logger = Logger.getLogger(ExportController.class.getName());

	public void index(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.getRequestDispatcher("/jsp/export_paid_users.jsp").forward(req, resp);
	}

	public void exportPaidUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ExportPaidUsersForm form = ExportPaidUsersForm.parse(req);
		if(form.getStartDate()==null||form.getEndDate()==null||form.getInvoiceBase()==null){
			req.setAttribute("error", "Form fields are mandatory");
			req.setAttribute("webform",form);
			req.getRequestDispatcher("/jsp/export_paid_users.jsp").forward(req, resp);
			return;
		}
		
		ThreadManager.createBackgroundThread(new Runnable() {
			@Override
			public void run() {
				try {
					logger.log(Level.WARNING,
							"*************************Export paid user in the background started ********************");

					String prefix = form.getInvoiceBase();					
					int count = 0;
					String invoiceNumber;
			
					String formatedStartDate = Utilities.formatedDate(form.getStartDate(), "yyyy_MM_dd");
					String formatedEndDate = Utilities.formatedDate(form.getEndDate(), "yyyy_MM_dd");
					
					String internalFolder="user_credit_notes_2020_with_id/"+createCloudStoragePath(formatedStartDate,formatedEndDate,"internal");
					String externalFolder="user_credit_notes_2020_with_id/"+createCloudStoragePath(formatedStartDate,formatedEndDate,"external");
					
					ExportService exportService = new ExportService();
					PaidUsersRepository paidUsersRepository = new PaidUsersRepository();
					

					Collection<PaidUser> paidUsers = null;
					Collection<PaidUserExternal> paidUserExternals = null;

					Collection<Pair<PaidUser, RedeemingRequests>> paidUserPairs = new ArrayList<>();
					

					if (form.isExternal()) {
						paidUserExternals = exportService.findPaidUsersExternalByDate(form.getStartDate(),
								form.getEndDate());
						for (PaidUserExternal user : paidUserExternals) {							
							invoiceNumber = prefix + String.valueOf(count++);
							user.setInvoiceNumber(invoiceNumber);
					
							// create pdf in cloud store
							exportService.createPDFInCloudStore(user,externalFolder,"invoices","PaidUser_"+invoiceNumber, invoiceNumber);									         
						}
						// create CSV
						_saveCSVFile(paidUserExternals,externalFolder,"credit_notes.csv");
					} else {
						paidUsers = exportService.findPaidUsersByDate(form.getStartDate(), form.getEndDate());
						for (PaidUser user : paidUsers) {
							invoiceNumber = prefix + String.valueOf(count++);
							user.setInvoiceNumber(invoiceNumber);
							
							if(user.getRedeemingRequestKey()!=null){
								Entity entity = paidUsersRepository.findEntityByKey(KeyFactory.stringToKey(user.getRedeemingRequestKey()));

								if (entity == null) {
									logger.log(Level.SEVERE, "No user entry found for - " + user.getRedeemingRequestKey());
									continue;
								}

								RedeemingRequests redeemingRequest = RedeemingRequests.valueOf(entity);
								paidUserPairs.add(new ImmutablePair<>(user, redeemingRequest));
								// create pdf in cloud store
								exportService.createPDFInCloudStore(redeemingRequest, user,internalFolder,"invoices","PaidUser_"+invoiceNumber, invoiceNumber);								
							}else{
								paidUserPairs.add(new ImmutablePair<>(user, null));
								// create pdf in cloud store
								exportService.createPDFInCloudStore(null, user,internalFolder,"invoices","PaidUser_"+invoiceNumber, invoiceNumber);																
							}
						}
						// create CSV
						saveCSVFile(paidUserPairs,internalFolder,"credit_notes.csv");
					}

				} catch (Exception e) {
					logger.log(Level.SEVERE, "export paid user  service:", e);
				}
			}
		}).start();
		
		req.setAttribute("success", "User revenue job successfully posted.");
		req.setAttribute("webform",form);
		req.getRequestDispatcher("/jsp/export_paid_users.jsp").forward(req, resp);
	}
	private String createCloudStoragePath(String startDate,String endDate,String sufix){
		StringBuilder sb=new StringBuilder();		
		sb.append(startDate);
		sb.append("_");
		sb.append(endDate);
		sb.append("_");
		sb.append(sufix);
		return sb.toString();
	}
	private void saveCSVFile(Collection<Pair<PaidUser, RedeemingRequests>> entities,String folder,String name) throws IOException {
		ExportService exportService = new ExportService();

		try (Writer writer = new StringWriter()) {
			exportService.createCSVFile(writer, entities);

			CloudStorageRepository cloudStorageRepository = new CloudStorageRepository();
			cloudStorageRepository.save(writer, folder+"/"+name);
		}
	}

	private void _saveCSVFile(Collection<PaidUserExternal> entities,String folder,String name) throws IOException {
		ExportService exportService = new ExportService();

		try (Writer writer = new StringWriter()) {
			exportService._createCSVFile(writer, entities);

			CloudStorageRepository cloudStorageRepository = new CloudStorageRepository();
			cloudStorageRepository.save(writer, folder+"/"+name);
		}
	}
}