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
import java.util.stream.Collectors;

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
		
		Entity redeemingRequest=new Entity("redeeming_request_id");
		redeemingRequest.setIndexedProperty("name","Sergey");
		redeemingRequest.setIndexedProperty("email","first@gmail.com");
		ds.put(redeemingRequest);
		
		Entity redeemingRequest1=new Entity("redeeming_request_id");
		redeemingRequest1.setIndexedProperty("email","second@yahoo.com");
		redeemingRequest1.setIndexedProperty("name","Sergey");		
		ds.put(redeemingRequest1);

		
		Entity paidUser=new Entity("paid_user");
		paidUser.setProperty("user", "user1");
		paidUser.setProperty("redeeming_request_key", redeemingRequest.getKey());
		paidUser.setProperty("eur_currency",12.0);
		paidUser.setProperty("paid_user_success",true);
		paidUser.setProperty("email_sent_success",true);
		ds.put(paidUser);
		
		Entity paidUser1=new Entity("paid_user");
		paidUser1.setProperty("user", "user2");
		paidUser1.setProperty("eur_currency",12.0);
		paidUser1.setProperty("paid_user_success",true);
		paidUser1.setProperty("email_sent_success",true);
		
		paidUser1.setProperty("redeeming_request_key", KeyFactory.keyToString(redeemingRequest.getKey()));
		ds.put(paidUser1);
		
		PaidUsersRepository paidUsersRepository=new PaidUsersRepository();
		Collection<Entity> list=paidUsersRepository.findEntities("paid_user", null, null);
		Collection<PaidUser> paidUsers=list.stream().map(PaidUser::valueOf).collect(Collectors.toList());
		System.out.println(paidUsers);
		
		
		
		
		
		
		
	}


	
}