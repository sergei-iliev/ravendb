package usecase;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.junit.Test;

import com.luee.wally.admin.controller.ImportController;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.csv.PaidUsers2018;
import com.luee.wally.json.ExchangeRateVO;

public class UserRevenueTest {

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
	
}
