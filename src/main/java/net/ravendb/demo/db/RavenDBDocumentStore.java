package net.ravendb.demo.db;

import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.IDocumentStore;

public enum RavenDBDocumentStore {
INSTANCE;
	
	private static IDocumentStore store;

    static {
        store = new DocumentStore("http://127.0.0.1:18080", "Hospital");
    }

    public IDocumentStore getStore() {
    	store.initialize();
        return store;
    }
}
