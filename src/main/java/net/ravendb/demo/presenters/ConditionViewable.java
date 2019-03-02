package net.ravendb.demo.presenters;

import java.util.Collection;
import java.util.Date;

import net.ravendb.demo.model.Condition;
import net.ravendb.demo.model.Patient;

public interface ConditionViewable {

	public interface ConditionViewListener{
		public Condition getConditionById(String id);
		public Patient getPatientById(String id) ;
	    public void save(Condition condition);
	    public void delete(Condition condition);
	    
	    Collection<Condition> getConditionsList(int offset,int limit,String term);
	    int getConditionsCount(String term);
	    
		  void openSession();
		  void releaseSession();
	}
	
	
}
