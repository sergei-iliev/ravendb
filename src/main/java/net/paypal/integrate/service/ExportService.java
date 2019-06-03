package net.paypal.integrate.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.Timestamp;

import net.paypal.integrate.api.GenerateCSV;
import net.paypal.integrate.entity.RedeemingRequests;
import net.paypal.integrate.repository.ImpexRepository;

@Service
public class ExportService {
	private final Logger logger = Logger.getLogger(ExportService.class.getName());
	
	@Autowired
	private ImpexRepository impexRepository;
	
	public Collection<RedeemingRequests> find(boolean paid,String countryCode,String packageName,boolean confirmedEmail,String type,String amount,Timestamp date){
		return impexRepository.find( paid, countryCode, packageName, confirmedEmail, type, amount, date);
	}
	
	public void convertToCSV(Writer write,Collection<RedeemingRequests> list)throws IOException{
		Collection<String> line=new ArrayList<String>();
		
		for(RedeemingRequests item:list){		    
			//item
			line.add(item.getAmount());
			line.add(item.getEmail());
			line.add(item.getMessage());
			line.add(item.getFrom());
			line.add(String.valueOf(item.getUserGuid()));
			GenerateCSV.INSTANCE.writeLine(write, line);
			line.clear();
		}
	}
	public void generateData(){
		for(int i=0;i<10;i++){
			RedeemingRequests r=new RedeemingRequests();
			r.setDate(Timestamp.now());
			r.setPaid((i%2)==0);
			if(r.isPaid())
				r.setCountryCode("US");
			else
				r.setCountryCode("BG");	
			r.setEmail("br@home.bg");
			r.setFullAddress("Levski town, Baba Tonka 7");
			r.setFullName("Sergei Rachev Iliev");
			if(i%2==0){
				r.setAmount("20.23");
				r.setUserGuid("634095b2-c739-4789-8de9-02aeffa503dd");
			}else if(i%3==0){
				r.setAmount("25.23");
				r.setUserGuid("1b85072b-3a54-4bfe-b49e-25d0022a223d");
			}else{
				r.setAmount("36.23");				
			    r.setUserGuid("dcb12c58-b747-469e-9510-f4f0c2ea0517");
			}
			
						
			r.setType("Amazon");
			r.setConfirmedEmail(true);
			impexRepository.save(r);
			
		}
	}
}
