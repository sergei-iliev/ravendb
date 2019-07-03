package usecase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import net.paypal.integrate.command.Attachment;




public class DBTestCase{
	private Closeable session;
	
	  private final LocalServiceTestHelper helper =
		      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	 

	
	@Before
	public void initialize(){
		helper.setUp();

	}
	@After
	public void release()throws Exception{

		helper.tearDown();
	}
	


	@Test
	public void createDBTest() throws Exception{
		 DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		 Entity affs=new Entity("affs");
		 affs.setProperty("total",1.0d);		 
		 ds.put(affs);
		
		
		 Assert.assertEquals(1, ds.prepare(new Query("affs")).countEntities(FetchOptions.Builder.withLimit(10)));
		  
		 EmbeddedEntity embeddedEntity=new EmbeddedEntity();
		 Set<String> s=new HashSet<>();
		 s.add("2007-12-12");
		 s.add("2013-11-11");
		 embeddedEntity.setProperty("package.1.x",s);
		 
		 s=new HashSet<>();
		 int m=1900;
		 for(int i=0;i<400;i++){
		     m++; 
			 s.add(m+"-03-17");
		 }	 
		 embeddedEntity.setProperty("package.2.x",s);
		 
		 Entity user_history=new Entity("user_history");
		 user_history.setProperty("affsKey", affs.getKey());
		 user_history.setProperty("history",embeddedEntity);

		 user_history.setProperty("guid","123456789");
		 
		 ds.put(user_history);
		 
		List<Entity> e= ds.prepare(new Query("user_history").addFilter("guid", FilterOperator.EQUAL, "123456789")).asList(FetchOptions.Builder.withDefaults());
		EmbeddedEntity em= (EmbeddedEntity)e.get(0).getProperty("history");
		List<String> dates=(List<String>)em.getProperty("package.2.x");
		System.out.println(dates.contains("2281-03-17"));;
		 
	}
	/*
	 * Export from last 9 months all guid's from table affs and save in Cloud Store 
	 */
	@Test
	public void createAffsGuidDBTest() throws Exception{
		 DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		 
		 for(int i=0;i<5002;i++){
		  String uniqueID = UUID.randomUUID().toString();			 
		  Entity affs=new Entity("affs");
		  affs.setIndexedProperty("gaid",uniqueID);
		  Thread.currentThread().sleep(1);
		  affs.setProperty("created",new Date());
		  System.out.println(i);
		  ds.put(affs);		 
		 }

		 //read huge result set in batches
		 byte[] end="\r\n".getBytes();
		 int counter=0;
		 Cursor cursor=null;
		 QueryResultList<Entity> results;
		 ByteArrayOutputStream os=new ByteArrayOutputStream();
	     do{
	     FetchOptions fetchOptions;	 
	     if(cursor!=null){	 
	        fetchOptions = FetchOptions.Builder.withLimit(100).startCursor(cursor);
	     }else{
	        fetchOptions = FetchOptions.Builder.withLimit(100);	 
	     }
    	 Query query = new Query("affs");
	     PreparedQuery preparedQuery = ds.prepare(query);
	     results = preparedQuery.asQueryResultList(fetchOptions);
	     
	     for(Entity e:results){
	    	 if(e.getProperty("gaid")!=null) {
	    	   os.write(((String)e.getProperty("gaid")).getBytes());	 
	           os.write(end);
	    	 }
	     }
	     counter++;
	     if(counter==50){	
	    	//save in  				
			Attachment attachment=new Attachment();
			attachment.setContentType("text/plain");
			attachment.setFileName("Last_"+counter+".txt");
			attachment.setBuffer(os.toByteArray());
			saveBuffer(attachment);
			
	    	counter=0; 
	    	os.close();
	    	os=new ByteArrayOutputStream();
	     }
		 cursor=results.getCursor();
	     }while(results.size()>0);
			  
	     
		 //save in byte array
		 Attachment attachment=new Attachment();
		 attachment.setContentType("text/plain");
		 attachment.setFileName("Last_"+counter+".txt");
		 attachment.setBuffer(os.toByteArray());
		 
		 saveBuffer(attachment);
	}
	
	private void saveBuffer(Attachment attachment)throws IOException{
		 try (FileOutputStream fos = new FileOutputStream(attachment.getFileName())) {
			   fos.write(attachment.getBuffer());			   
	     }
	}
	
	@Test
	public void merge2filesGuidDBTest() throws Exception{
		Set<String> uniques1;
		try( 
			 InputStream inputFS = new FileInputStream(new File("D:\\Last9MonthsGaidList_15000.txt"));
			 BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));){
			
			 uniques1=br.lines().collect(Collectors.toSet());
		}
		
		System.out.println(uniques1.size());
		Set<String> uniques2;
		try( 
				 InputStream inputFS = new FileInputStream(new File("D:\\Last9MonthsGaidList_final.txt"));
				 BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));){
				
				 uniques2=br.lines().collect(Collectors.toSet());
			}	
		System.out.println(uniques2.size());
		
		uniques1.addAll(uniques2);
		System.out.println(uniques1.size());
        
		//write to a total file
 		try( 
				 OutputStream inputFS = new FileOutputStream(new File("D:\\Last9MonthsGaidList.txt"));
 				 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(inputFS));){
				
 			     for(String line:uniques1){
 			    	 bw.write(line);
 			    	 bw.write("\r\n");
 			     }
				 bw.flush();
			}	
		
	}
		
}