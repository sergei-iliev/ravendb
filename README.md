# RavenDB Hospital tutorial
RavenDB is an open-source NoSQL document store database. It is fully transactional,multi-platform and high availability distributed data store which support clients for a varity of programming languages including Java.
The following sample Hospital Management app is built upon the dynamic document based structure that RavenDB represents.
It uses RavenDB Java client to communicate with the document store.


* RavenDB community edition install
* Domain Entity descrption
* Session and Unit of Work pattern
* CRUD operations
* Paging on large record sets
* BLOB handling - attachements
* Queries

## RavenDB community edition install
Installing RavenDB is pretty straight forward:
1. Download the zip bundle from https://ravendb.net/download and unzip in a local drive folder
2. Register a community edition free licence from https://ravendb.net/buy
3. In powershell start either .\run.ps1 (console mode app) or .\setup-as-service.ps1 (service mode app) and follow the install instractions.
4. Once installed RavenDB Studio will show up in web browser, open "About" tab and register your license
5. Create your first noSQL database.

As noSQL database RavenDB is based on following properties
* Stores data in JSON-like documents that can have various structures
* Uses dynamic schemas, which means that we can create records without predefining anything
* The structure of a record can be changed simply by adding new fields or deleting existing ones
* Dynamically generated indexes to fascilitated fast data retrieval
* Map/Reduce to process large sets of documents
* On top of this RavenDB is easy to administer and deploy

## Entities, tables, collections, and documents
When it comes to persisting data a Java programmer tends to annotate Java POJO with @Entity so that the underlying JPA framework would treat the class as a domain object mapped to a row in a database.
RavenDB doesn’t use tables. Instead, it creates objects as documents, and multiple documents are known as a collection. 
In RavenDB, a domain object is mapped to a single document. In this regard there is no need of special class treatment other then having a default no args constructor. The sample model consists of 4 basic entitities, one of which is embedded as an array to demonstrate the power of grouping and fetching queries in RavenDB.

![UML Diagram](/screenshots/uml.png)
1. Patient - stored as a separate collection
```java
public class Patient {
        private String id;
	private String firstName,lastName;
	private Date birthDate;
	private Gender gender;
	
	private String email;
	private Address address;
	private List<Visit> visits;
  
  }
```
JSON representation of Patient document at RavenDB side
```JSON
{
    "firstName": "Megi",
    "lastName": "Devasko",
    "birthDate": "2016-11-30T22:00:00.0000000Z",
    "gender": "FEMALE",
    "email": "sss@box.com",
    "address": null,
    "visits": [
        {
            "date": "2019-02-26T22:00:00.0000000Z",
            "doctorId": "doctors/1-A",
            "type": "HOUSE",
            "visitSummery": "just a mainor pain",
            "conditionId": "conditions/1-A",
            "doctorName": "Sergiz Ovesian"
        },
        {
            "date": "2019-01-31T22:00:00.0000000Z",
            "doctorId": "doctors/2-A",
            "type": "EMERGENCYROOM",
            "visitSummery": "never worry",
            "conditionId": "conditions/2-A",
            "doctorName": "Megalo Karimov"
        }
    ],
    "@metadata": {
        "@collection": "Patients",
        "@flags": "HasAttachments",
        "Raven-Java-Type": "net.ravendb.demo.model.Patient"
    }
}
```
2. Visist - stored as an array in Patient collection  
```java
public class Visit{
	private Date date;
	private String doctorId;	
	private Type type;
	private String visitSummery;
	private String conditionId;
	private String doctorName;

}	
```
JSON representation of Visit document at RavenDB side is an array of documents in Patient document(look above)

3. Condition - list of available conditions
```java
public class Condition {
	private String id;
	private Type severity;
	private String prescription;
	private String description;

}
```
JSON representation of Condition document at RavenDB side
```JSON
{
    "severity": "SEVIER",
    "prescription": "stay at home fro a week",
    "description": "headache",
    "@metadata": {
        "@collection": "Conditions",
        "Raven-Java-Type": "net.ravendb.demo.model.Condition"
    }
}
```
4. Doctor - stored in a separate collection
```java
public class Doctor{
    	private String id;
    	private String name;
    	private String department;
   	private int age; 
 }
 ```
 JSON representation of Doctor document at RavenDB side
 ```JSON
 {
    "name": "Sergiz Ovesian",
    "department": "LV",
    "age": 45,
    "@metadata": {
        "@collection": "Doctors",
        "Raven-Java-Type": "net.ravendb.demo.model.Doctor"
    }
}
 ```
Each POJO has a property name "id" which will triger the usage RavenDB algorithm of autogenarating Ids. 
The convention is that entities get the identifiers in the following format collection/number-tag so the programmer is not concerned with the uniqueness of each document in a collection.

## RavenDB connector
The focal point is the RavenDB Java connector, which is added as a dependency to pom.xml. 
```
<dependency>
  <groupId>net.ravendb</groupId>
  <artifactId>ravendb</artifactId>
  <version>4.0.5</version>
</dependency>
```
It provides  the main API object document store, which sets up connection with the Server and downloads various configuration metadata.
The DocumentStore is capable of working with multiple databases and for proper operation it is recommend having only one instance of it per application.
```java
public enum RavenDBDocumentStore {
INSTANCE;
	
	private static IDocumentStore store;

    static {    
        store = new DocumentStore(
	new String[]{ "http://127.0.0.1:18080" /*,"http://127.0.0.1:18081","http://127.0.0.1:18082"*/}, 
	"Hospital");
        store.initialize();
    }

    public IDocumentStore getStore() {    	
        return store;
    }
}
```
## Session and Unit of Work pattern
For any operation we want to perform on RavenDB, we start by obtaining a new Session object from the document store. The Session object will contain everything we need to perform any operation necessary. Much like JPA's Hibernate implementation, the RavenDB Session also   
implements the Unit of Work pattern which has several implications in the context of a single session:
* Batching requests to save expensive remote calls.
* Single document (identified by its ID) always resolves to the same instance.
* Change tracking for all the entities that it has either loaded or stored.
In contrast to a DocumentStore,  Session is a lightweight object and can be created more frequently. For example, in web applications, a common (and recommended) pattern is to create a session per request.

## CRUD operations
Patient entity is given as an example only. 

![Patient CRUD](/screenshots/p_edit.png)

Create operation inserts a new document. Each document contains a unique ID that identifies it, data and adjacent metadata, both stored in JSON format. The metadata contains information describing the document, e.g. the last modification date (@last-modified property) or the collection (@collection property) assignment. As alreay mentioned we will use the default algoritm for letting RavenDB generate unique ID for our entities by specifing a property named "id" in each entity. 

```java
public void create(Patient patient) {
		 try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			 
			   session.store(patient);
				 
	           if(patient.getAttachment()!=null){	        	 
			     	session.advanced().attachments().store(patient,patient.getAttachment().getName(),patient.getAttachment().getInputStream(),patient.getAttachment().getMimeType());
	           }
	           session.saveChanges();
	           
	     }	  		
}
```
Update operation is worth noting - it handles optimistic conqurrency control and throws ConcurrecyException provided that another use has already changed the record. The method also handles attachement as a 1:1 relationship with each patient. 

```java
	public void update(Patient patient)throws ConcurrencyException{

		 try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			   
			   //enable oca			   
			   session.advanced().setUseOptimisticConcurrency(true);			   			   
			   session.store(patient);
			   
	           //delete previous attachments	           
			   AttachmentName[] names=session.advanced().attachments().getNames(patient);
			   if(names.length>0){				
				  session.advanced().attachments().delete(patient,names[0].getName());				
			   }
				 
	           if(patient.getAttachment()!=null){	        	  
			     session.advanced().attachments().store(patient,patient.getAttachment().getName(),patient.getAttachment().getInputStream(),patient.getAttachment().getMimeType());			     
	           }
	           
	           session.saveChanges();
	           
	     }
	}
```

```java
	public void delete(Patient patient) {
		 try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
	           session.delete(patient.getId());
	           session.saveChanges();
	     }
		
	}
```

## Paging on large record sets
Paging through large data is one of the most common operations with RavenDB. A typical scenario is the need to display results in chunks in a lazy loading or pagable grid. The grid is configured to first obtain the total amount of records to shaow and then lazily as the user scrolls up and down to obtain records by batches of 50. For the patients grid, the corresponding attachements are also obtained and streamed into a convinient byte array to show in one of the grid columns. 

![Patient CRUD](/screenshots/p_paging.png)

```java
	public int getPatientsCount() {
		 try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {			 
			 return session.query(Patient.class).count();			 			 			 
		 }
	}
	public Collection<Patient> getPatientsList(int offset,int limit,boolean order) {
		   try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
				   Collection<Patient> list=null;

				   if(order){
				       IDocumentQuery<Patient>  query = session.query(Patient.class);
				       list= query.orderBy("birthDate").skip(offset).take(limit).toList();
				     }else{
				       IDocumentQuery<Patient> query = session.query(Patient.class);
				       list= query.skip(offset).take(limit).toList();	 
				     }
				   
				   
				   for(Patient patient:list){
						 AttachmentName[] names=session.advanced().attachments().getNames(patient);					
						 if(names.length>0){
							try(CloseableAttachmentResult result= session.advanced().attachments().get(patient,names[0].getName())){
							  	Attachment attachment=new Attachment();
							  	attachment.setName(names[0].getName());
							  	attachment.setMimeType(names[0].getContentType());
							  	byte[] bytes = IOUtils.toByteArray(result.getData());
								attachment.setBytes(bytes);
							    patient.setAttachment(attachment);
							}catch(IOException e){
								e.printStackTrace();
							}
							 
						 }
				   }
				   return list;
	       }		
	}
```

## BLOB handling - attachements
When binary data(images,documents,media) needs to be associated with the document, RavenDB provides the Attachement API.
Attachements are completely decopled from documents. They can be updated and changed separately from the document and do not 
participate in transactions. Following POJO represents attachments on client side.
```java
public class Attachment {

	String name;
	String mimeType;
	byte[] bytes; 
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public byte[] getBytes(){
		return bytes;
	}
	
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	public InputStream getInputStream(){
		return new ByteArrayInputStream(bytes);
	}
	public StreamResource getStreamResource(){
	  ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
	  return new StreamResource(name, () -> bis);
	}
	
}
```
In Patient entity, image is attached to the document using the session.Advanced.Attachments.Store method.
Attachments, just like documents, are a part of the session and will be only saved on the Server when DocumentSession.SaveChanges is executed.

```java
session.advanced().attachments().store(patient,patient.getAttachment().getName(),patient.getAttachment().getInputStream(),patient.getAttachment().getMimeType());			     	           	          
session.saveChanges();
```

This operation is used to get an attachment from a patient document.
```java
try(CloseableAttachmentResult result= session.advanced().attachments().get(patient,names[0].getName())){
	  	Attachment attachment=new Attachment();
	  	attachment.setName(names[0].getName());
	  	attachment.setMimeType(names[0].getContentType());
	  	byte[] bytes = IOUtils.toByteArray(result.getData());
		attachment.setBytes(bytes);
  	        patient.setAttachment(attachment);
}
```
## Queries
RavenDB uses indexes to retrieve data but they don't work the same way as relational database indexes work. The main difference is that RavenDB's indexes are schema-less documented oriented. RavenDB requires an index to solve a query;that is why indexe is needed. The great think is that a programmer is not required to manually create indexes - RavenDB can deduct and create required index dynamically, by analizing query at run time.
All query samples that follow are based on dynamic indexes, generated by RavenDB's search engine. 
The provided Patient type as the generic type parameter does not only define the type of returned results, but it also indicates that the queried collection will be Patients.
```java
		try (IDocumentSession session = RavenDBDocumentStore.INSTANCE.getStore().openSession()) {
			Patient patient = session.load(Patient.class, id);
			return patient;
		}
```


