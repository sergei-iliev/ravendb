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
import com.luee.wally.api.service.PaidUsersService;
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
		if(form.getStartDate()==null||form.getEndDate()==null){
			req.setAttribute("error", "Start and End dates are mandatory");
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

					String prefix = "2020111111";
					int count = 0;
					String invoiceNumber;
			
					String formatedStartDate = Utilities.formatedDate(form.getStartDate(), "yyyy_MM_dd");
					String formatedEndDate = Utilities.formatedDate(form.getEndDate(), "yyyy_MM_dd");
					
					ExportService exportService = new ExportService();
					PaidUsersRepository paidUsersRepository = new PaidUsersRepository();
					PaidUsersService paidUsersService=new PaidUsersService();

					Collection<PaidUser> paidUsers = null;
					Collection<PaidUserExternal> paidUserExternals = null;

					Collection<Pair<PaidUser, RedeemingRequests>> paidUserPairs = new ArrayList<>();
					Collection<Pair<PaidUserExternal, RedeemingRequests>> paidUserExternalPairs = new ArrayList<>();

					if (form.isExternal()) {
						paidUserExternals = exportService.findPaidUsersExternalByDate(form.getStartDate(),
								form.getEndDate());
						for (PaidUserExternal user : paidUserExternals) {
							Collection<Entity> entities = paidUsersRepository.findEntities("paid_users_external",
									"redeeming_request_id", user.getRedeemingRequestId());
							if (entities.size() > 1) {
								throw new ServletException(
										"Too many entities for redeeming_request_id: " + user.getRedeemingRequestId());
							}

							if (entities.size() == 0) {
								logger.log(Level.SEVERE, "No user entry found for redeeming_request_id - "
										+ user.getRedeemingRequestId());
								continue;
							}
							RedeemingRequests redeemingRequest = RedeemingRequests.valueOf(entities.iterator().next());
							invoiceNumber = prefix + String.valueOf(count++);
							user.setInvoiceNumber(invoiceNumber);
							paidUserExternalPairs.add(new ImmutablePair<>(user, redeemingRequest));
							// create pdf in cloud store
							exportService.createPDFInCloudStore(redeemingRequest, user,
									createCloudStoragePath("user_credit_notes_2020_with_id/PaidUsersExternal2020_",formatedStartDate,formatedEndDate), invoiceNumber);
						}
						// create CSV
						_saveCSVFile(paidUserExternalPairs,createCloudStoragePath("user_revenue_external/users_revenue_2020_",formatedStartDate,formatedEndDate));
					} else {
						paidUsers = exportService.findPaidUsersByDate(form.getStartDate(), form.getEndDate());
						for (PaidUser user : paidUsers) {
							
							Entity entity = paidUsersRepository.findEntityByKey(KeyFactory.stringToKey(user.getRedeemingRequestKey()));

							if (entity == null) {
								logger.log(Level.SEVERE, "No user entry found for - " + user.getRedeemingRequestKey());
								continue;
							}

							RedeemingRequests redeemingRequest = RedeemingRequests.valueOf(entity);
							invoiceNumber = prefix + String.valueOf(count++);
							user.setInvoiceNumber(invoiceNumber);
							paidUserPairs.add(new ImmutablePair<>(user, redeemingRequest));
							// create pdf in cloud store
							exportService.createPDFInCloudStore(redeemingRequest, user,
									createCloudStoragePath("user_credit_notes_2020_with_id/PaidUsers2020_",formatedStartDate,formatedEndDate), invoiceNumber);
						}
						// create CSV
						saveCSVFile(paidUserPairs,createCloudStoragePath("user_revenue/users_revenue_2020_",formatedStartDate,formatedEndDate));
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
	private String createCloudStoragePath(String prefix,String startDate,String endDate){
		StringBuilder sb=new StringBuilder();
		sb.append(prefix);
		sb.append(startDate);
		sb.append("_");
		sb.append(endDate);
		sb.append("_");
		return sb.toString();
	}
	private void saveCSVFile(Collection<Pair<PaidUser, RedeemingRequests>> entities,String cloudStoragePath) throws IOException {
		ExportService exportService = new ExportService();

		try (Writer writer = new StringWriter()) {
			exportService.createCSVFile(writer, entities);

			CloudStorageRepository cloudStorageRepository = new CloudStorageRepository();
			//cloudStorageRepository.save(writer, "user_revenue/users_revenue_2020");
			cloudStorageRepository.save(writer, cloudStoragePath);
		}
	}

	private void _saveCSVFile(Collection<Pair<PaidUserExternal, RedeemingRequests>> entities,String cloudStoragePath) throws IOException {
		ExportService exportService = new ExportService();

		try (Writer writer = new StringWriter()) {
			exportService._createCSVFile(writer, entities);

			CloudStorageRepository cloudStorageRepository = new CloudStorageRepository();
			cloudStorageRepository.save(writer, "user_revenue_external/users_revenue_2020");
		}
	}
}
