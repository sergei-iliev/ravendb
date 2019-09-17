package usecase;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.controller.ImportController;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.csv.PaidUsers2018;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.paypal.InvoiceService;

public class UserRevenueTest {

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
	public void getExchangeRatesTest() throws Exception {
		ImportService importService=new ImportService();
		
		PaidUsers2018 user=new PaidUsers2018();
		user.setCurrencyCode("EUR");
		user.setDate("8/13/2019");
		
		ExchangeRateVO exchangeRateVO= importService.getExchangeRates(user.getFormatedDate("YYYY-MM-dd"), "EUR");
		System.out.println(exchangeRateVO.getRates().get(user.getCurrencyCode()));
	}


	@Test
	public void import2019Test() throws Exception {
	
		
		ImportController importController=new ImportController();		
		importController.importUserRevenue2019(null,null);
		
	}
	
	@Test
	public void createInvoice() throws Exception {
		InvoiceService invoiceService=new InvoiceService();
		Entity redeemingRequest=new Entity("affs");
		redeemingRequest.setProperty("date", new Date());
		Thread.currentThread().sleep(1000);
		redeemingRequest.setProperty("creation_date", new Date());
		redeemingRequest.setProperty("full_name","Sergey Iliev");
		
		redeemingRequest.setProperty("full_address","Baba Tonka 7");
		redeemingRequest.setProperty("country_code","5900");
		redeemingRequest.setProperty("email","1@1.com");
		redeemingRequest.setProperty("user_guid","01923456789");
		redeemingRequest.setProperty("type","PayPal");
		
		PaidUsers2018 paidUsers2018=new PaidUsers2018();
		paidUsers2018.setCurrencyCode("EUR");
		paidUsers2018.setPayedAmount("23.89");
		paidUsers2018.setDate("8/13/2019");
		
		paidUsers2018.setUserCurrencyCode("USD");
		paidUsers2018.setUserPayedAmount("25.00");
		
		InputStream in=invoiceService.createInvoice(redeemingRequest, paidUsers2018, "000001111");

		
		File targetFile = new File("D:\\demo.pdf");
		 
	    FileUtils.copyInputStreamToFile(in, targetFile);
	}
	
	
}
