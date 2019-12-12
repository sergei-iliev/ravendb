package usecase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.admin.repository.InvoiceRepository;
import com.luee.wally.admin.repository.PaymentRepository;
import com.luee.wally.api.service.InvoiceService;
import com.luee.wally.api.service.MailService;
import com.luee.wally.api.service.PayPalService;
import com.luee.wally.command.Email;
import com.luee.wally.command.PdfAttachment;
import com.luee.wally.command.invoice.PayoutResult;
import com.luee.wally.entity.RedeemingRequests;
import com.luee.wally.utils.TestDatabase;
import com.luee.wally.utils.Utilities;


public class PayPalTest {
	private final static Logger logger = Logger.getLogger(PayPalTest.class.getName());

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	@Before
	public void initialize() {
		helper.setUp();
		Utilities.domain = "demo.test";
		TestDatabase.INSTANCE.generateDB();
	}

	@After
	public void release() throws Exception {
		helper.tearDown();
	}

	


	@Test
	public void payPalTest() throws Throwable {

		PaymentRepository paymentRepository = new PaymentRepository();
		Entity entity = paymentRepository.getRedeemingRequestsByUserGuid("ffff2675-a072-4b6b-ab66-cb599a29147d");
		// ****comes from server*********
		String key = KeyFactory.keyToString(entity.getKey());
		Entity user = paymentRepository.getRedeemingRequestsByKey(key);
		RedeemingRequests redeemingRequests = RedeemingRequests.valueOf(user);

		PayPalService payPalService = new PayPalService();
		InvoiceService invoiceService = new InvoiceService();

		MailService mailService = new MailService();
		InvoiceRepository invoiceRepository = new InvoiceRepository();
		try {

			PayoutResult payoutResult = payPalService.payout(redeemingRequests, "USD");

			String invoiceNumber = Long.toString(invoiceRepository.createInvoiceNumber());
			paymentRepository.savePayPalPayment(redeemingRequests, "USD", BigDecimal.ZERO, invoiceNumber,
					payoutResult.getPayoutBatchId());

			PdfAttachment attachment = new PdfAttachment();
			attachment.readFromStream(invoiceService.createInvoice(payoutResult, user, invoiceNumber));

			// mailService.sendInvoice(Constants.toInvoiceMail,attachment);

		} catch (Exception ex) {
			logger.log(Level.SEVERE, "payment", ex);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			String sStackTrace = sw.toString();
			Email email = new Email();
			email.setSubject("Error alert!");
			email.setContent((Objects.toString(ex.getMessage(), "")) + "/n/n" + sStackTrace);
			// email.setFrom(Constants.fromMail);
			// email.setTo(Constants.toInvoiceMail);
			// mailService.sendMail(email);
		}

	}

}
