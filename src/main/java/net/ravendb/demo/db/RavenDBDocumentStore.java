package net.ravendb.demo.db;

import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.IDocumentStore;

public enum RavenDBDocumentStore {
INSTANCE;
	
	private static IDocumentStore store;

    static {    
        store = new DocumentStore(new String[]{ "http://127.0.0.1:18080"}, "Hospital");
        store.initialize();
    }

    public IDocumentStore getStore() {    	
        return store;
    }
}
