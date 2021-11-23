package com.luee.wally.api.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.luee.wally.admin.repository.ApplicationSettingsRepository;
import com.luee.wally.utils.Utilities;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

public class SlackMessagingService extends AbstractService{
	private final Logger logger = Logger.getLogger(SlackMessagingService.class.getName());
	private ApplicationSettingsService applicationSettingsService=new ApplicationSettingsService();
//	https://api.slack.com/apps/A02M3TU3M0T/oauth?success=1
//		
//	curl -XPOST https://slack.com/api/chat.postMessage \
//		  -H 'Content-Type: application/x-www-form-urlencoded' \
//		  -H 'Authorization: Bearer xoxb-2703218318967-2725265480435-UAuA2x4TWoUhZQFvOw5PDkUa' \
//		  -d 'channel=%23softbaked&text=%3Awave%3A%20Hi%20from%20a%20bot%20written%20in%20Java%21'
		  
	public void sendMessage(String message)throws IOException{
		
		 String token=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.SLACK_BOT_TOKEN);
		 String channelId=applicationSettingsService.getApplicationSettingCached(ApplicationSettingsRepository.SLACK_BOT_CHANNEL_NAME);
		    
		    
		    
		    
		if(channelId.startsWith("#")){
			channelId="#"+channelId;
		}
		Slack slack = Slack.getInstance();
		try{
		ChatPostMessageResponse response = slack.methods(token).chatPostMessage(ChatPostMessageRequest.builder()
				  .channel(channelId)
				  .text(message)
				  .build());
		
		if (!response.isOk()) {		    		  		    			
			logger.severe(response.getError());
			logger.severe(response.getErrors().toString());		    
		}
		} catch (SlackApiException requestFailure) {
			logger.log(Level.SEVERE,"Slack request error",requestFailure);    
		}
	}
	
	public void sendMessage(List<String> discrepencyList,Map<String,BigDecimal> map,Map<String,BigDecimal> localMap,String subject) throws IOException{
		StringBuffer sb=new StringBuffer(subject);		 
		 sb.append("\n");
		 
		 map.entrySet().forEach(e->{
			 if(discrepencyList.contains(e.getKey())){
				 sb.append(e.getKey());
				 sb.append(" - ");
				 sb.append(Utilities.formatPrice(e.getValue().negate()));
				 sb.append("\n");
			 }
		 });
		 
		 sb.append("\n");
		 sb.append("Payments from our server:");
		 sb.append("\n");
		 
		 localMap.entrySet().forEach(e->{
			 if(discrepencyList.contains(e.getKey())){
				 sb.append(e.getKey());
				 sb.append(" - ");
				 sb.append(Utilities.formatPrice(e.getValue().negate()));
				 sb.append("\n");
			 }
		 });
	
		 this.sendMessage(sb.toString());
	}
}
