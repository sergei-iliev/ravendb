package com.luee.wally.admin.repository;

import java.util.Collection;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;

public class SuspiciousEmailDomainRepository extends AbstractRepository {
	private final Logger logger = Logger.getLogger(SuspiciousEmailDomainRepository.class.getName());
 
	
}
