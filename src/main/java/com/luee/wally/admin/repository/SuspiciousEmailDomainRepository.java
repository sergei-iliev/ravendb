package com.luee.wally.admin.repository;

import java.util.Collection;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;

public class SuspiciousEmailDomainRepository extends AbstractRepository {
	private final Logger logger = Logger.getLogger(SuspiciousEmailDomainRepository.class.getName());
 
	public Collection<Entity> findSuspiciousDomainByEmail(String email){
		String domain=email.substring(email.indexOf("@") + 1);
		return findEntities("suspicious_email_domains","domain", domain);		
	}
}
