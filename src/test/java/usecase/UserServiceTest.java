package usecase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.admin.repository.UserRepository;
import com.luee.wally.api.service.UserService;



public class UserServiceTest {

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
	public void countUserGuidsMap() throws Exception {
		UserService userService=new UserService();
		UserRepository userRepository=new UserRepository();
		DatastoreService ds = userRepository.createDatastoreService(Consistency.EVENTUAL);
	    Collection<String> ids=new ArrayList<>();
		for (int i = 0; i < 100; i++) {

			Entity affs = new Entity("affs");
			affs.setProperty("total_ad_rev", 1.4d);
			affs.setProperty("date", new Date());
			affs.setProperty("email","jacob@enro.com");
			affs.setProperty("experiment", "preview_images");
			affs.setProperty("user_guid","affs_guid_"+i);
			if(ids.size()<37)
			ids.add("affs_guid_"+i);
			if (i == 2) {
				affs.setProperty("email", "Siliev@enro.com");
			}
			if (i == 3) {
				affs.setProperty("email", "siliev@enro.com");				
			}
			if (i == 4) {
				affs.setProperty("email", "siliev@Enro.com");
			}
			
			ds.put(affs);
		}
		System.out.println(userService.countUserGuids(new ArrayList<>(ids)).size());	
		System.out.println(userService.countUserGuids(new ArrayList<>(ids)));
		
	}
	
	@Test
	public void getRecordsByEmail() throws Exception {
		UserService userService=new UserService();
		PaidUsersRepository userRepository=new PaidUsersRepository();
		Collection<String> emails=(userService.convertToEmails("siliev@Enro.com"));
		
		DatastoreService ds = userRepository.createDatastoreService(Consistency.EVENTUAL);
		for (int i = 0; i < 6; i++) {

			Entity affs = new Entity("affs");
			affs.setProperty("total_ad_rev", 1.4d);
			affs.setProperty("date", new Date());
			affs.setProperty("email","jacob@enro.com");
			affs.setProperty("experiment", "preview_images");
			affs.setProperty("user_guid","affs_guid_"+i);
			if (i == 2) {
				affs.setProperty("email", "Siliev@enro.com");
			}
			if (i == 3) {
				affs.setProperty("email", "siliev@enro.com");
			}
			if (i == 4) {
				affs.setProperty("email", "siliev@Enro.com");
			}
			
			ds.put(affs);
		}


		
		for (int i = 0; i < 6; i++) {

			Entity affs = new Entity("redeeming_requests_new");
			affs.setProperty("mary","Hello mate");
			affs.setProperty("date", new Date());
			affs.setProperty("email","jacob@enro.com");
			affs.setProperty("paypal_account","jacob@enro.com");
			affs.setProperty("user_guid","redreq_guid_"+i);

			if (i == 2) {
				affs.setProperty("paypal_account", "Siliev@enro.com");
			}
			if (i == 3) {
				affs.setProperty("paypal_account", "siliev@enro.com");
			}
			if (i == 4) {
				affs.setProperty("paypal_account", "siliev@Enro.com");
			}
			
			ds.put(affs);
		}

		Collection<Entity> list=userRepository.getRecordsByEmails (emails,"redeeming_requests_new","paypal_account");		
		System.out.println(list);
	}



}
