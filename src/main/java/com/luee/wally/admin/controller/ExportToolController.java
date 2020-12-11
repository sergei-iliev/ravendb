package com.luee.wally.admin.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.luee.wally.admin.repository.CloudStorageRepository;
import com.luee.wally.api.route.Controller;

@Deprecated
public class ExportToolController implements Controller{
	private final Logger logger = Logger.getLogger(ExportToolController.class.getName());
	/*
	 * Export demo data from 
	 */
	public void runExportTool(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	 
		ThreadManager.createBackgroundThread(new Runnable() {
			@Override
			public void run() {

				try {
					logger.log(Level.WARNING,
							"*************************Export Tool Task in the background started ********************");
					Map<Key,List<String>> result=ExportToolController.this.read();
			  		  try(Writer writer=new StringWriter()){
				  		   for(Map.Entry<Key, List<String>> entry:result.entrySet()){	
				  			   
				  			   writer.append(KeyFactory.keyToString(entry.getKey())); 
				  			   writer.append("|");
				  			   for(String v:entry.getValue()){
				  				 writer.append("\""+v+"\",");   
				  			   }				  			    
				  			   writer.append("\r\n");
				  		   }
				  		   
						    CloudStorageRepository cloudStorageRepository=new CloudStorageRepository();
						    cloudStorageRepository.save(writer,"fb_air_export_tool/test");
				  	  }
					
					logger.log(Level.WARNING,
							"*************************Background export Tool task finished*****************");
				} catch (Exception e) {
					logger.log(Level.SEVERE, "affs export service:", e);
				}
			}
		}).start();
	  	

	}
	
	
	private Map<Key,List<String>> read(){
		Map<Key,List<String>> res=new HashMap<>();
		
		String date="2020-11-29";
        this.read(date,res);
        
        logger.log(Level.WARNING,"----------com.relaxingbraintraining.wordcup------------found "+res.values().stream().flatMap(v->v.stream()).count());
		return res;
	}
	
	private void read(String date,Map<Key,List<String>> output){
		List<Entity> entities=this.getUserDailyRevenueByDate(date);
		logger.log(Level.WARNING,date+" found "+entities.size());
        for(Entity entity:entities){
        	Key key=(Key)entity.getProperty("aff_key");
            EmbeddedEntity ee=(EmbeddedEntity)entity.getProperty("app_cpm");
            List<String> icmps=(List<String>)ee.getProperty("com.relaxingbraintraining.wordcup");
            
            if(icmps!=null&&icmps.size()>0){
            	logger.log(Level.WARNING,key+" package found "+icmps.size());
            	List<String> list=output.get(key);
            	if(list==null){
            		list=new ArrayList<String>();
            		output.put(key,list);
            	}
            	list.addAll(icmps);            	
            }
        }		
	}
	public List<Entity> getUserDailyRevenueByDate(String date) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();		

		Filter filter = new FilterPredicate("rev_check_date",FilterOperator.EQUAL,date);

		Query q = new Query("user_daily_revenue_fb");
		
		q.setFilter(filter);
		PreparedQuery pq = ds.prepare(q);
		
		return pq.asQueryResultList(FetchOptions.Builder.withDefaults());
		
		
	}
	
}
