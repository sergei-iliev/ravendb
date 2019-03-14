package net.ravendb.demo.db;

import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.IDocumentStore;

public final class RavenDBDocumentStore {
	
	private static IDocumentStore store;

    static {    
        store = new DocumentStore(new String[]{ "http://127.0.0.1:18080","http://127.0.0.1:18081","http://127.0.0.1:18082"}, "Hospital");
        store.initialize();
    }

    public static IDocumentStore getStore() {    	
        return store;
    }
}
