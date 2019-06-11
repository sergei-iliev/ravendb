package net.paypal.integrate;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import net.paypal.integrate.entity.Affs;
import net.paypal.integrate.entity.Counter;
import net.paypal.integrate.entity.PayPalPayment;
import net.paypal.integrate.entity.PayPalUser;
import net.paypal.integrate.entity.RedeemingRequests;
import net.paypal.integrate.entity.UserDailyRevenue;

public class ObjectifyServletContextListener implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//cloud_datastore_emulator.cmd start --host=localhost --port=8884 --store_on_disk=True --consistency=0.9 "C:\Users\Sergey Iliev\AppData\Roaming\gcloud\emulators\datastore"
		//gcloud beta emulators datastore start --host-port=localhost:<yourpreferredport>		
/*DEBUG*/		
        ObjectifyService.init(new ObjectifyFactory(
                DatastoreOptions.newBuilder().setHost("http://localhost:8884")
                    .setProjectId("sapient-office-232912")
                    .build().getService(),
                new AppEngineMemcacheClientService()
            ));
        
/*PRODUCTION*/		
//		 ObjectifyService.init(new ObjectifyFactory(
//		            DatastoreOptions.getDefaultInstance().getService(),
//		            new AppEngineMemcacheClientService()
//		        ));

       	// ObjectifyService.init();
        
        
		 ObjectifyService.register(PayPalUser.class);
		 ObjectifyService.register(PayPalPayment.class);
		 ObjectifyService.register(Counter.class);
		 ObjectifyService.register(RedeemingRequests.class);
		 ObjectifyService.register(UserDailyRevenue.class);
		 ObjectifyService.register(Affs.class);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}
