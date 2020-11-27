package com.luee.wally.api.service.impex;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import com.luee.wally.csv.FBUserLevelRevenue;
import com.luee.wally.csv.UserLevelRevenue;

public class FBImportService extends ImportService{
	private final Logger logger = Logger.getLogger(FBImportService.class.getName());
	//[Date, Ad Unit ID, Ad Unit Name, Waterfall, Ad Format, Placement, Country, Device Type, IDFA, IDFV, User ID, Encrypted CPM]
	private static final String AD_UNIT_ID="Ad Unit ID";
	private static final String IDFA="IDFA";
	private static final String ENCRYPTED_CPM="Encrypted CPM";
	

	
	public Collection<FBUserLevelRevenue> importFBCSVText(String text)throws Exception{
		
		List<List<String>> list=readCSVText(text);		
		return convertToFBUserLevelRevenue(list);
	}
	public Collection<FBUserLevelRevenue> convertToFBUserLevelRevenue(List<List<String>> lines){
		Collection<FBUserLevelRevenue> result=new ArrayList<>();
		List<String> headers=new ArrayList<>();
		
		boolean isFirstLine=false;
		for(List<String> line:lines){
			if(!isFirstLine){				
				for(String item:line){
				  headers.add(item);	
				}
				
				isFirstLine=true;
				continue;
			}
			FBUserLevelRevenue user=new FBUserLevelRevenue();
			user.setAdUnitID(line.get(headers.indexOf(AD_UNIT_ID)));
			user.setIDFA(line.get(headers.indexOf(IDFA)).trim());
			user.setEncryptedCPM(line.get(headers.indexOf(ENCRYPTED_CPM)).trim());
			result.add(user);
		}
		
		return result;
	}
}
