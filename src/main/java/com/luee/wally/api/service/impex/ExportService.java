package com.luee.wally.api.service.impex;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.cloud.Timestamp;
import com.luee.wally.DB;
import com.luee.wally.admin.repository.PaidUsersRepository;
import com.luee.wally.api.service.AbstractService;
import com.luee.wally.entity.PaidUser;
import com.luee.wally.entity.PaidUserExternal;



public class ExportService extends AbstractService{
	private final Logger logger = Logger.getLogger(ExportService.class.getName());
	
	//private final static String message = "Congratulations! This is your reward from PlaySpot :-)";
	//private final static String  from = "PlaySpot"; 
	
	public Collection<PaidUserExternal> findPaidUsersExternalByDate(Date startDate,Date endDate){
		PaidUsersRepository paidUsersRepository=new PaidUsersRepository();		
		Collection<Entity> result=new ArrayList<>();
		
		String cursor=null;
	    do{		       
		       //read batch from paid users
		       cursor= paidUsersRepository.findPaidUsersExternal(cursor,result, startDate, endDate);
		       if(cursor==null){
		         break;
		       }		       		       	       
		    }while(cursor!=null);
	    Collection<PaidUserExternal> paidUsers=result.stream().map(PaidUserExternal::valueOf).collect(Collectors.toList());
		return paidUsers;
	}
	
	public Collection<PaidUser> findPaidUsersByDate(Date startDate,Date endDate){
		PaidUsersRepository paidUsersRepository=new PaidUsersRepository();		
		Collection<Entity> result=new ArrayList<>();
		
		String cursor=null;
	    do{		       
		       //read batch from paid users
		       cursor= paidUsersRepository.findPaidUsers(cursor,result,null, startDate, endDate);
		       if(cursor==null){
		         break;
		       }		       		       	       
		    }while(cursor!=null);
	    Collection<PaidUser> paidUsers=result.stream().map(PaidUser::valueOf).collect(Collectors.toList());
		return paidUsers;
	}
	
//	public Collection<Entity> find(boolean paid,String countryCode,String packageName,boolean confirmedEmail,String type,String amount,Date date){
//		return DB.getAmazonUsers( paid, countryCode, packageName, confirmedEmail, type, amount, date);
//	}
//	
//	
//	public void convertToCSV(Writer write,Collection<Entity> list)throws IOException{
//		Collection<String> line=new ArrayList<String>();
//		
//		line.add("Amount ($) *");
//		line.add("Name of Recipient  (50 character limit)");
//		line.add("Email Address *");
//		line.add("Message (1500 character limit)");
//		line.add("From *  (50 character limit)");
//		line.add("user_guid");
//		line.add("country_code");
//		GenerateCSV.INSTANCE.writeLine(write, line);
//		line.clear();
//
//
//
//		for(Entity item:list){		    
//			//item
//			line.add((String) item.getProperty("amount"));
//			
//			String s = ((String) item.getProperty("full_name")).replaceAll(",", " ");
//			
//			String[] values = s.split(",");
//
//			line.add(values[0]);
//			line.add((String) item.getProperty("email"));
//			line.add(message);
//			line.add(from);
//			line.add((String) item.getProperty("user_guid"));
//			line.add((String) item.getProperty("country_code"));
//			GenerateCSV.INSTANCE.writeLine(write, line);
//			line.clear();
//		}
//	}
//
//	public void convertToCSVGlobal(Writer write,Collection<Entity> list)throws IOException{
//		Collection<String> line=new ArrayList<String>();
//		
//		line.add("Amount");
//		line.add("Name of Recipient  (50 character limit)");
//		line.add("Email Address *");
//		line.add("user_guid");
//		line.add("country_code");
//		GenerateCSV.INSTANCE.writeLine(write, line);
//		line.clear();
//
//
//
//		for(Entity item:list){		    
//			//item
//			line.add((String) item.getProperty("amount"));
//			
//			String s = ((String) item.getProperty("full_name")).replaceAll(",", " ");
//			
//			String[] values = s.split(",");
//
//			line.add(values[0]);
//			line.add((String) item.getProperty("email"));
//			line.add((String) item.getProperty("user_guid"));
//			line.add((String) item.getProperty("country_code"));
//			GenerateCSV.INSTANCE.writeLine(write, line);
//			line.clear();
//		}
//	}

}
