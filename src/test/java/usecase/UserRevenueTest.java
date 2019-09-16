package usecase;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import com.luee.wally.admin.controller.ImportController;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.service.impex.ImportService;

public class UserRevenueTest {

	@Test
	public void getExchangeRatesTest() throws Exception {
		String json=ConnectionMgr.INSTANCE.getJSON("https://api.exchangeratesapi.io/latest?base=EUR");
		
		//by date
		json=ConnectionMgr.INSTANCE.getJSON("https://api.exchangeratesapi.io/2018-03-26?base=EUR");
		
		//System.out.println(json);
	}


	@Test
	public void import2019Test() throws Exception {
		ImportController importController=new ImportController();		
		importController.importUserRevenue2019InBackground(null,null);
		
	}
	
}
