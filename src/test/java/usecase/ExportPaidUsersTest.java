package usecase;

import java.io.Closeable;
import java.io.StringWriter;
import java.io.Writer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.repository.CloudStorageRepository;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.api.service.AffsSearchService;
import com.luee.wally.api.service.CampaignSearchService;
import com.luee.wally.api.service.PaymentService;
import com.luee.wally.api.service.impex.ExportService;
import com.luee.wally.command.AffsSearchForm;
import com.luee.wally.command.AffsSearchResult;
import com.luee.wally.command.CampaignSearchForm;
import com.luee.wally.command.ExportPaidUsersForm;
import com.luee.wally.command.PaymentEligibleUserForm;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.entity.PaidUserExternal;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.json.JSONUtils;
import com.luee.wally.utils.TestDatabase;
import com.luee.wally.utils.Utilities;

public class ExportPaidUsersTest {
	private Closeable session;

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	@Before
	public void initialize() {
		helper.setUp();

	}

	@After
	public void release() throws Exception {

		helper.tearDown();
	}

	@Test
	public void exportPaidUsersTest() throws Exception {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		TestDatabase.INSTANCE.generateDB();			
		
		ExportPaidUsersForm form = new ExportPaidUsersForm();
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusYears(3);		
		form.setStartDate(Date.from(yesterday.toInstant()));
		form.setEndDate(new Date());
		
		String formatedStartDate = Utilities.formatedDate(form.getStartDate(), "yyyy_MM_dd");
		String formatedEndDate = Utilities.formatedDate(form.getEndDate(), "yyyy_MM_dd");
		
		String prefix = "2020111111";
		int count = 0;
		String invoiceNumber;
		
		Collection<PaidUser> paidUsers = null;
		Collection<PaidUserExternal> paidUserExternals = null;

		Collection<Pair<PaidUser, RedeemingRequests>> paidUserPairs = new ArrayList<>();
		Collection<Pair<PaidUserExternal, RedeemingRequests>> paidUserExternalPairs = new ArrayList<>();
		
		ExportService exportService = new ExportService();
		PaidUsersRepository paidUsersRepository = new PaidUsersRepository();
		
		paidUsers = exportService.findPaidUsersByDate(form.getStartDate(), form.getEndDate());
		for (PaidUser user : paidUsers) {
			Collection<Entity> entities = paidUsersRepository.findEntities("redeeming_requests_new",
					"user_guid", user.getUserGuid());
			if (entities.size() > 1) {
				throw new ServletException("Too many entities for user_guid: " + user.getUserGuid());
			}

			if (entities.size() == 0) {
				System.out.println( "No user entry found for - " + user.getUserGuid());
				continue;
			}

			RedeemingRequests redeemingRequest = RedeemingRequests.valueOf(entities.iterator().next());
			invoiceNumber = prefix + String.valueOf(count++);
			user.setInvoiceNumber(invoiceNumber);
			paidUserPairs.add(new ImmutablePair<>(user, redeemingRequest));
			// create pdf in cloud store
			
			exportService.createPDFInCloudStore(redeemingRequest, user,
					createCloudStoragePath("user_credit_notes_2020_with_id/PaidUsers2020_",formatedStartDate,formatedEndDate), invoiceNumber);
		}
		// create CSV
		//saveCSVFile(paidUserPairs);		
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
	
}