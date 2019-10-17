package com.luee.wally.admin.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.luee.wally.entity.SearchFilterTemplate;

public class SearchFilterTemplateRepository extends AbstractRepository {
	private final Logger logger = Logger.getLogger(SearchFilterTemplateRepository.class.getName());

	public SearchFilterTemplate findSearchFilterTemplateById(String _key)throws EntityNotFoundException {
		Key key=KeyFactory.stringToKey(_key);
		DatastoreService ds = createDatastoreService(Consistency.STRONG);
		Entity entity=ds.get(key);		
		return SearchFilterTemplate.valueOf(entity);
	}

	public Collection<SearchFilterTemplate> findSearchFilterTemplates() {
		Collection<SearchFilterTemplate> list = new ArrayList<>();

		DatastoreService ds = createDatastoreService(Consistency.STRONG);
		Query query = new Query("search_filter_template");
		query.addSort("date", SortDirection.DESCENDING);

		PreparedQuery pq = ds.prepare(query);
		QueryResultList<Entity> entities = pq.asQueryResultList(FetchOptions.Builder.withDefaults());

		for (Entity entity : entities) {

			list.add(SearchFilterTemplate.valueOf(entity));
		}
		return list;

	}

	public void saveSearchFilterTemplates(SearchFilterTemplate searchFilterTemplate) {
		DatastoreService ds = createDatastoreService(Consistency.EVENTUAL);
		Entity entity = new Entity("search_filter_template");
		entity.setProperty("name", searchFilterTemplate.getName());
		entity.setProperty("date", searchFilterTemplate.getDate());
		entity.setProperty("form", searchFilterTemplate.getForm());

		ds.put(entity);
	}

}
