package usecase;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.DB;
import com.luee.wally.admin.controller.ImportController;
import com.luee.wally.admin.controller.PaymentController;
import com.luee.wally.admin.repository.InvoiceRepository;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.service.GiftCardService;
import com.luee.wally.api.service.InvoiceService;
import com.luee.wally.api.service.MailService;
import com.luee.wally.api.service.PayPalService;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.command.Email;
import com.luee.wally.command.PdfAttachment;
import com.luee.wally.command.invoice.PayoutResult;
import com.luee.wally.constants.Constants;
import com.luee.wally.csv.PaidUsers2018;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.utils.TestDatabase;
import com.luee.wally.utils.Utilities;
import com.tangocard.raas.RaasClient;
import com.tangocard.raas.exceptions.RaasGenericException;
import com.tangocard.raas.models.AccountModel;
import com.tangocard.raas.models.BillingAddressModel;
import com.tangocard.raas.models.CatalogModel;
import com.tangocard.raas.models.CreateAccountInput;
import com.tangocard.raas.models.CreateAccountRequestModel;
import com.tangocard.raas.models.CreateCreditCardRequestModel;
import com.tangocard.raas.models.CreateCustomerRequestModel;
import com.tangocard.raas.models.CreateOrderRequestModel;
import com.tangocard.raas.models.CreditCardModel;
import com.tangocard.raas.models.CustomerModel;
import com.tangocard.raas.models.NameEmailModel;
import com.tangocard.raas.models.NewCreditCardModel;
import com.tangocard.raas.models.OrderModel;
import com.tangocard.raas.models.SystemStatusResponseModel;

public class PayPalTest {
	private final static Logger logger = Logger.getLogger(PayPalTest.class.getName());
	
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	
	
	@Before
	public void initialize() {
		helper.setUp();
		Utilities.domain="demo.test";
		TestDatabase.INSTANCE.generateDB(); 
	}

	@After
	public void release() throws Exception {
		helper.tearDown();
	}
	
	@Test
	public void payPalTest() throws Throwable {
		
		PaymentRepository paymentRepository=new PaymentRepository();
		Entity entity=paymentRepository.getRedeemingRequestsByUserGuid("ffff2675-a072-4b6b-ab66-cb599a29147d");
		//****comes from server*********
		String key=KeyFactory.keyToString(entity.getKey());
		Entity user=paymentRepository.getRedeemingRequestsByKey(key);
		RedeemingRequests redeemingRequests=RedeemingRequests.valueOf(user);
		
        PayPalService payPalService = new PayPalService();
        InvoiceService invoiceService = new InvoiceService();

        MailService mailService = new MailService();
        InvoiceRepository invoiceRepository=new InvoiceRepository();
        try {
        	
        PayoutResult payoutResult = payPalService.payout(redeemingRequests);
        
        String invoiceNumber=Long.toString(invoiceRepository.createInvoiceNumber());
        paymentRepository.savePayPalPayment(redeemingRequests,invoiceNumber,payoutResult.getPayoutBatchId());

		
        PdfAttachment attachment=new PdfAttachment();
        attachment.readFromStream(invoiceService.createInvoice(payoutResult,user,invoiceNumber));   

        mailService.sendInvoice(Constants.toInvoiceMail,attachment);

        
        }
        catch(Exception ex) {        	
        	logger.log(Level.SEVERE, "payment", ex);
        	StringWriter sw = new StringWriter();
        	PrintWriter pw = new PrintWriter(sw);
        	ex.printStackTrace(pw);
        	String sStackTrace = sw.toString();
			Email email=new Email();
			email.setSubject("Error alert!");
			email.setContent((Objects.toString(ex.getMessage(), ""))+"/n/n"+sStackTrace);
			email.setFrom(Constants.fromMail);
			email.setTo(Constants.toInvoiceMail);
			mailService.sendMail(email);
        }		
		
	}

}
