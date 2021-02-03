package usecase;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.luee.wally.api.service.GiftCardService;
import com.luee.wally.api.service.PaymentOrderTransactionsService;
import com.luee.wally.api.tangocard.client.AccountsApi;
import com.luee.wally.api.tangocard.client.OrdersApi;
import com.luee.wally.api.tangocard.client.model.AccountView;
import com.luee.wally.api.tangocard.client.model.OrderListView;
import com.luee.wally.command.Email;
import com.luee.wally.command.order.OrderTransactionResult;
import com.luee.wally.constants.Constants;
import com.luee.wally.utils.Utilities;
import com.tangocard.raas.Configuration;
import com.tangocard.raas.RaasClient;
import com.tangocard.raas.exceptions.RaasGenericException;
import com.tangocard.raas.models.AccountModel;
import com.tangocard.raas.models.BrandModel;
import com.tangocard.raas.models.CatalogModel;
import com.tangocard.raas.models.CreateCustomerRequestModel;
import com.tangocard.raas.models.CustomerModel;
import com.tangocard.raas.models.GetOrdersInput;
import com.tangocard.raas.models.GetOrdersResponseModel;
import com.tangocard.raas.models.ItemModel;

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
	public void getCatalogTest() throws Throwable {
		   Configuration.environment=Configuration.environment.PRODUCTION;
		   RaasClient raasClient=new  RaasClient(Constants.PROD_PLATFORM_IDENTIFIER,Constants.PROD_PLATFORM_KEY);
		   CatalogModel catalogModel = raasClient.getCatalog().getCatalog();
		   System.out.println(json(catalogModel));
		   for(BrandModel brandModel:catalogModel.getBrands()){
			   System.out.println(brandModel.getBrandName());
			   for(ItemModel itemModel:brandModel.getItems()){
				   System.out.println(json(itemModel));
			     
			   }
		   }

	}
	@Test
	public void getGiftCardAcountBalanceTest() throws Throwable {
		   //Configuration.environment=Configuration.environment.PRODUCTION;
		   //GiftCardService giftCardService=new GiftCardService();
		   //BigDecimal balance=giftCardService.getGiftCardAccountBalance(Constants.PROD_PLATFORM_IDENTIFIER, Constants.PROD_PLATFORM_KEY, "A88393817");
		   AccountsApi accountsApi=new AccountsApi(Constants.PROD_PLATFORM_IDENTIFIER, Constants.PROD_PLATFORM_KEY);
		   AccountView accountView= accountsApi.getAccount("A88393817");
		   System.out.println(accountView.getCurrentBalance());	
		   
	}
	@Test
	public void getAcountsTest() throws Throwable {
		   Configuration.environment=Configuration.environment.PRODUCTION;
		   RaasClient raasClient=new  RaasClient(Constants.PROD_PLATFORM_IDENTIFIER,Constants.PROD_PLATFORM_KEY);
		   List<AccountModel> accoutModels = raasClient.getAccounts().getAllAccounts();
		   for(AccountModel accountModel:accoutModels){
			   double balance=accountModel.getCurrentBalance();
			   String accountIdentifier=accountModel.getAccountIdentifier();
			   if(Double.compare(Constants.TANGO_CARD_ACCOUNT_BALANCE_THRESHOLD, balance)>0){
				   //send email
					Email email = new Email();
					email.setSubject("Tango card account balance threshold!");
					email.setContent("Tango Card account "+accountIdentifier+" balance is "+balance);

			   }
		   }


	}
	@Test
	public void getCustomersTest() throws Throwable {
		   Configuration.environment=Configuration.environment.PRODUCTION;
		   RaasClient raasClient=new  RaasClient(Constants.PROD_PLATFORM_IDENTIFIER,Constants.PROD_PLATFORM_KEY);

		   List<CustomerModel> customers = raasClient.getCustomers().getAllCustomers();
		  for(CustomerModel customer:customers){
	          System.out.println(customer.getCustomerIdentifier());
	            
	          System.out.println(raasClient.getAccounts().getAccountsByCustomer(customer.getCustomerIdentifier()).get(0).getAccountIdentifier());
		   };
 
	}
	
	private static final String customerDisplayName="JustPlay GmbH";
	private static final String customerIdentifier="justplaygmbh";
	@Test
	public void createGiftCardAccountTest() throws Throwable {
		   Configuration.environment=Configuration.environment.PRODUCTION;
		   RaasClient raasClient=new  RaasClient(Constants.PROD_PLATFORM_IDENTIFIER,Constants.PROD_PLATFORM_KEY);
		   List<CustomerModel> customers= raasClient.getCustomers().getAllCustomers();
		   for(CustomerModel customer:customers){
			   System.out.println(customer.getCustomerIdentifier()+"::"+customer.getDisplayName()+"::"+customer.getStatus());
		   }
		   
	       CreateCustomerRequestModel createCustomerRequestModel = new CreateCustomerRequestModel();
	       createCustomerRequestModel.setCustomerIdentifier(customerIdentifier);
	       createCustomerRequestModel.setDisplayName(customerDisplayName);
	       CustomerModel customer=null;
	       try{
	    	   customer=raasClient.getCustomers().getCustomer(customerIdentifier);	    	   	    	   
	       }catch(RaasGenericException e){	    	   	    	   
	    	   //in case it exists
	           customer=null;
	       }
	       
	       if(customer==null){
    	     try{
	    	   customer = raasClient.getCustomers().createCustomer(createCustomerRequestModel);	       
	         }catch(RaasGenericException e){
	    	   System.out.println(e.getHttpPhrase());
	         }
	       }
	       
	       System.out.println(customer);
    	   
		   
	}
	@Test
	public void getGiftCardOrdersTest() throws Exception {
		ZonedDateTime now=ZonedDateTime.now();
		ZonedDateTime yesterday=now.minusDays(1);
		   
		ZonedDateTime yesterdayStart=yesterday.truncatedTo(ChronoUnit.DAYS);
		ZonedDateTime yesterdayEnd=yesterdayStart.plusHours(24);
		
		Map<String,String> configMap=new HashMap<>();
		configMap.put(Constants.PLATFORM_IDENTIFIER,Constants.PROD_PLATFORM_IDENTIFIER);
		configMap.put(Constants.PLATFORM_KEY,Constants.PROD_PLATFORM_KEY);
		
		PaymentOrderTransactionsService paymentOrderTransactionsService=new PaymentOrderTransactionsService();
		Collection<OrderTransactionResult> orderTransactionResults=paymentOrderTransactionsService.getGiftCardOrderTransactions(Instant.from(yesterdayStart).toString(),Instant.from(yesterdayEnd).toString(),configMap);
	    //group by
		Map<String,BigDecimal> map= paymentOrderTransactionsService.getOrderTransactionsGroupBy(orderTransactionResults);
		//get the sum in usd
		String formattedDate = Utilities.formatedDate(yesterday, "yyyy-MM-dd");
		BigDecimal usdSum=paymentOrderTransactionsService.calculateTotal(map, formattedDate, "USD");
		
		//get the sum in eur		
		BigDecimal eurSum=paymentOrderTransactionsService.calculateTotal(map, formattedDate, "EUR");
		 
        System.out.println(map);
        System.out.println(usdSum);
        System.out.println(eurSum);
        
        	   	
	}
	/*
	 * OLD RAAS SDK CLIENT
	 *  G71971146::SoftBakedAppsGmbH-920::active
		A88393817
		G43915119::JustPlayGmbH::active
		A60855040
	 */
	@Test
	public void getGiftCardTransactionHistoryTest() throws Throwable {
		   
		   Configuration.environment=Configuration.environment.PRODUCTION;
		   RaasClient raasClient=new  RaasClient(Constants.PROD_PLATFORM_IDENTIFIER,Constants.PROD_PLATFORM_KEY);
		   List<CustomerModel> customers= raasClient.getCustomers().getAllCustomers();
		   for(CustomerModel customer:customers){
			   System.out.println(customer.getCustomerIdentifier()+"::"+customer.getDisplayName()+"::"+customer.getStatus());
			   customer.getAccounts().forEach(a->{
				   System.out.println(a.getAccountIdentifier());
			   });
		   }
		   
//		   DateTime now=DateTime.now();
//		   DateTime yesterday=now.minusDays(1);
//			   
//		   DateTime yesterdayStart=yesterday.withTimeAtStartOfDay();
//		   DateTime yesterdayEnd=yesterdayStart.plusHours(24);
//			
//		   GetOrdersInput getOrdersInput = new GetOrdersInput();
//		    
//		   getOrdersInput.setStartDate(DateTime.now());
//		   //getOrdersInput.setEndDate(yesterdayEnd);
//		   getOrdersInput.setPage(4);
//		   
//		   getOrdersInput.setElementsPerBlock(100);
//	       GetOrdersResponseModel ordersResponseModel = raasClient.getOrders().getOrders(getOrdersInput);
//	       System.out.println(ordersResponseModel.getPage().getTotalCount());
//	       System.out.println(ordersResponseModel.getPage().getResultCount());
//	       System.out.println(ordersResponseModel.getPage().getNumber());
//	       
//	       ordersResponseModel.getOrders().forEach(o->{
//	    	   
//	    	   if(o.getStatus().equals("COMPLETE"))
//	    	    System.out.println(o.getCreatedAt()+"::"+ o.getAmountCharged().getValue()+"::"+o.getAmountCharged().getExchangeRate()+"::"+o.getAmountCharged().getTotal()+"::"+o.getAmountCharged().getCurrencyCode()+"::"+o.getStatus());  
//	       });
	       
		
		
	       //System.out.println("Get Orders: " + json(getOrdersResponseModel));
	}
	/*
	@Test
	public void createUserGiftCardTest() throws Throwable {
	   RaasClient raasClient=new  RaasClient(Constants.PLATFORM_IDENTIFIER,Constants.PLATFORM_KEY);
	   CatalogModel catalogModel = raasClient.getCatalog().getCatalog();
       System.out.println(catalogModel.getCatalogName());
       catalogModel.getBrands().forEach(a->{
    	   System.out.println(a.getBrandName()+","+a.getImageUrls());
    	   a.getItems().forEach(i->{
    		   System.out.println("---"+i.getUtid());  
    	   });
    	  
       });
       
       
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
       
       
       String externalRefId = "U100001";//UUID.randomUUID().toString();
       
       NameEmailModel recipientNameEmailModel = new NameEmailModel();
       recipientNameEmailModel.setFirstName("Gil");
       recipientNameEmailModel.setLastName("Iliev");
       recipientNameEmailModel.setEmail("sergei.iliev@gmail.com");
       //recipientNameEmailModel.setEmail("gil.mincberg@gmail.com");
       
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
	*/
	private String json(Object object) throws JsonProcessingException {
        
		return objectMapper.writeValueAsString(object);
    }
	
}
