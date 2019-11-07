package usecase;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.DB;
import com.luee.wally.admin.controller.ImportController;
import com.luee.wally.api.ConnectionMgr;
import com.luee.wally.api.service.impex.ImportService;
import com.luee.wally.constants.Constants;
import com.luee.wally.csv.PaidUsers2018;
import com.luee.wally.json.ExchangeRateVO;
import com.luee.wally.paypal.InvoiceService;
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

public class GiftCardTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	private ObjectMapper objectMapper=new ObjectMapper();
	
	@Before
	public void initialize() {
		helper.setUp();

	}

	@After
	public void release() throws Exception {

		helper.tearDown();
	}
	
	@Test
	public void createUserGiftCardTest() throws Throwable {
	   RaasClient raasClient=new  RaasClient(Constants.PLATFORM_IDENTIFIER,Constants.PLATFORM_KEY);
//	   CatalogModel catalogModel = raasClient.getCatalog().getCatalog();
//       System.out.println(catalogModel.getCatalogName());
//       catalogModel.getBrands().forEach(a->{
//    	   System.out.println(a.getBrandName()+","+a.getImageUrls());
//    	   a.getItems().forEach(i->{
//    		   System.out.println("---"+i.getUtid());  
//    	   });
//    	  
//       });
//     //create customers
//       String customerIdentifier = Constants.CUSTOMER_NAME;
//       
//       CreateCustomerRequestModel createCustomerRequestModel = new CreateCustomerRequestModel();
//       createCustomerRequestModel.setCustomerIdentifier(customerIdentifier);
//       createCustomerRequestModel.setDisplayName(customerIdentifier);
//       
//       try{
//    	   CustomerModel c=raasClient.getCustomers().getCustomer(customerIdentifier);
//    	   System.out.println("BackedSoft customer: " + json(c));
//       }catch(RaasGenericException e){
//    	   System.out.println(e.getHttpCode());
//       }
//       try{
//    	   CustomerModel createdCustomerModel = raasClient.getCustomers().createCustomer(createCustomerRequestModel);
//    	   System.out.println("Created Customer: " + json(createdCustomerModel));
//       }catch(RaasGenericException e){
//    	   System.out.println(e.getHttpCode());
//       }
//       
//       //create account
//       String accountIdentifier = Constants.ACCOUNT_NAME;
//       
//       CreateAccountInput createAccountInput=new CreateAccountInput();
//       
//       CreateAccountRequestModel createAccountRequestModel=new CreateAccountRequestModel();
//       createAccountRequestModel.setAccountIdentifier(accountIdentifier);
//       createAccountRequestModel.setDisplayName(accountIdentifier);
//       createAccountRequestModel.setContactEmail(Constants.ACCOUNT_EMAIL);
//       
//       
//       createAccountInput.setBody(createAccountRequestModel);
//       createAccountInput.setCustomerIdentifier(customerIdentifier);
//       
//       try{
//    	   
//    	  AccountModel accountModel = raasClient.getAccounts().createAccount(createAccountInput);
//          System.out.println("Created Account: " + json(accountModel));
//       }catch(RaasGenericException e){
//    	   System.out.println(e.getHttpCode());
//       }
//       //fund account
//       CreateCreditCardRequestModel createCreditCardRequestModel=new CreateCreditCardRequestModel();
//       createCreditCardRequestModel.setCustomerIdentifier(customerIdentifier);
//       createCreditCardRequestModel.setAccountIdentifier(accountIdentifier);
//       createCreditCardRequestModel.setLabel("VISA");
//       createCreditCardRequestModel.setIpAddress("192.168.1.1");
//       BillingAddressModel billingAddressModel=new BillingAddressModel();
//       billingAddressModel.setCity("Sofia");
//       billingAddressModel.setFirstName("Sergey");
//       billingAddressModel.setLastName("Iliev");
//       billingAddressModel.setAddressLine1("asfasf asdf asd 88");
//       billingAddressModel.setCountry("BG");
//       billingAddressModel.setState("ALABAMA");
//       billingAddressModel.setEmailAddress("mega@yahoo.com");
//       billingAddressModel.setPostalCode("4009");       
//       createCreditCardRequestModel.setBillingAddress(billingAddressModel);
//       
//       NewCreditCardModel newCreditCardModel=new NewCreditCardModel();
//       newCreditCardModel.setExpiration("2020-01");
//       newCreditCardModel.setNumber("08f8ffff-4467-4795-9638-9fbd308564d2");
//       newCreditCardModel.setVerificationNumber("1111");
//       
//       createCreditCardRequestModel.setCreditCard(newCreditCardModel);
//       try{
//       CreditCardModel createdOrderModel= raasClient.getFund().createRegisterCreditCard(createCreditCardRequestModel);
//       System.out.println("Credit card: " + json(createdOrderModel));
//       }catch(RaasGenericException e){
//    	   System.out.println(IOUtils.toString(e.getHttpContext().getResponse().getRawBody(),Charset.defaultCharset()));
//       }
       
       
       String externalRefId = UUID.randomUUID().toString();
       
       NameEmailModel recipientNameEmailModel = new NameEmailModel();
       recipientNameEmailModel.setFirstName("Gil");
       recipientNameEmailModel.setLastName("Iliev");
       //recipientNameEmailModel.setEmail("sergei.iliev@gmail.com");
       recipientNameEmailModel.setEmail("gil.mincberg@gmail.com");
       
       CreateOrderRequestModel createOrderRequestModel = new CreateOrderRequestModel();
       createOrderRequestModel.setExternalRefID(externalRefId);
       createOrderRequestModel.setCustomerIdentifier(Constants.CUSTOMER_NAME);
       createOrderRequestModel.setAccountIdentifier(Constants.ACCOUNT_NAME);
       createOrderRequestModel.setRecipient(recipientNameEmailModel);
       createOrderRequestModel.setSendEmail(true);
       createOrderRequestModel.setUtid("U666425"); // Amazon.com Variable item
       createOrderRequestModel.setAmount(1.00);
	   createOrderRequestModel.setMessage(String.format(Constants.EMAIL_TEMPLATE_MESSAGE,"BackeSoft"));
	   createOrderRequestModel.setEmailSubject(String.format(Constants.EMAIL_TEMPLATE_SUBJECT,"BackedSoft"));
       
	   NameEmailModel senderNameEmailModel = new NameEmailModel();
	   senderNameEmailModel.setFirstName("Sergey");
	   senderNameEmailModel.setLastName("Iliev");
	   senderNameEmailModel.setEmail(Constants.ACCOUNT_EMAIL);
	   createOrderRequestModel.setSender(senderNameEmailModel);
	    
       try{
       OrderModel createdOrderModel = raasClient.getOrders().createOrder(createOrderRequestModel);
       System.out.println("Created Order: " + json(createdOrderModel));
       }catch(RaasGenericException e){
    	   System.out.println(IOUtils.toString(e.getHttpContext().getResponse().getRawBody(),Charset.defaultCharset()));
       }
	}
	
	private String json(Object object) throws JsonProcessingException {
        
		return objectMapper.writeValueAsString(object);
    }
	
}
