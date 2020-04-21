package com.luee.wally.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.luee.wally.admin.repository.EmailTemplateRepository;
import com.luee.wally.entity.EmailTemplate;

public class EmailTemplateService {
	private final Logger logger = Logger.getLogger(EmailTemplateService.class.getName());

	public Collection<EmailTemplate> getEmailTemplatesByContext(String type){
		Collection<EmailTemplate> result=new ArrayList<>();
		EmailTemplateRepository emailTemplateRepository=new EmailTemplateRepository();
		Collection<Entity> templates =emailTemplateRepository.getEmailTemplates(null,type);
		for(Entity entity:templates){			
			result.add(EmailTemplate.valueOf(entity));
		}
		return result;
	}
}
