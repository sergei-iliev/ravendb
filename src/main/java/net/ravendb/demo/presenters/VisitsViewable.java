package net.ravendb.demo.presenters;

import java.util.Collection;

import org.apache.commons.lang3.tuple.Pair;

import net.ravendb.demo.command.PatientVisit;

public interface VisitsViewable {

	public interface VisitsViewListener{
		Pair<Collection<PatientVisit>,Integer>	getVisistsList(int offset,int limit,boolean order);
		  
		Pair<Collection<PatientVisit>,Integer> searchVisitsList(int offset,int limit,String term,boolean order);
		  
		  void openSession();
		  void releaseSession();
	}
}
