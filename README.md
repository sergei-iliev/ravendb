# Sample Hospital Mangement web application
RavenDB is an open-source NoSQL document store database. It is fully transactional,multi-platform and high availability distributed data store which support clients for a varity of programming languages including Java.
The following sample Hospital Management app is built upon the dynamic document based structure that RavenDB represents.
It uses RavenDB Java client to communicate with the document store.


* RavenDB community edition install
* Domain Entity descrption
* CRUD operations
* Paging on large record sets
* BLOB handling - attachements
* Group by queries

## RavenDB community edition install
Installing RavenDB is pretty straight forward:
1. Download the zip bundle from https://ravendb.net/download and unzip in a local drive folder
2. Register a community edition free licence from https://ravendb.net/buy
3. In powershell start either .\run.ps1 (console mode app) or .\setup-as-service.ps1 (service mode app) and follow the install instractions.
4. Once installed RavenDB Studio will show up in web browser, open "About" tab and register your license
5. Create your first noSQL database.

## Entities, tables, collections, and documents
When it comes to persisting data a Java programmer tends to annotate Java POJO with @Entity so that the underlying JPA framework would treat the class as a domain object mapped to a row in a database.
RavenDB doesn’t use tables. Instead, it creates objects as documents, and multiple documents are known as a collection. 
In RavenDB, a domain object is mapped to a single document. In this regard there is no need of special class treatment other then having a default no args constructor. The sample model consists of 4 basic entitities, one of which is embedded as an array to demonstrate the power of grouping and fetching queries in RavenDB.

1. Patient - stored as a separate collection
```java
public class Patient implements Serializable{
    public enum Gender{
    	MALE,
    	FEMALE;
        
    	@JsonCreator
        public static Gender convert(String status){
            if(status==null){
                return Gender.MALE;
            }
            
            return Gender.valueOf(status);
        }
        
        @JsonValue
        public String getGender() {        
            return this.toString();
        }    	
    }
	
        private String id;
	private String firstName,lastName;
	private Date birthDate;
	private Gender gender;
	
	private String email;
	private Address address;
	private List<Visit> visits;
	@JsonIgnore
	private Attachment attachment;
  
  ......
  
  }
```  
2. Visist - stored as an array in Patient collection  
```java
public class Visit implements Serializable{
    public enum Type{
    	HOUSE,
    	EMERGENCYROOM,
    	HOSPITAL;
        
    	@JsonCreator
        public static Type convert(String type){
            if(type==null){
                return Type.HOUSE;
            }
            
            return Type.valueOf(type);
        }
        
        @JsonValue
        public String getType() {        
            return this.toString();
        }    	
    }
	
    	
	private Date date;
	private String doctorId;	
	private Type type;
	private String visitSummery;
	private String conditionId;
	@JsonIgnore
	private Doctor doctor;
	private String doctorName;
	@JsonIgnore
	private Condition condition;
.............
}	
```
3. Condition - list of available conditions
```java
public class Condition implements Serializable{
	public enum Type{
		SEVIER,MINOR,CHRONIC,NORMAL;
    	@JsonCreator
        public static Type convert(String status){
            if(status==null){
                return Type.NORMAL;
            }
            
            return Type.valueOf(status);
        }
        
        @JsonValue
        public String getType() {        
            return this.toString();
        }  
	}
	
	private String id;
	private Type severity;
	private String prescription;
	private String description;

..........
}
```
4. Doctor - stored in a separate collection
```java
public class Doctor implements Serializable{
    private String id;
    private String name;
    private String department;
    private int age;
 ........  
 }
 ```
Each POJO has a property name "id" which will triger the usage RavenDB algorithm of autogenarating Ids 
The convention is that entities get the identifiers in the following format collection/number-tag so the programmer is not concerned with the uniqueness of each document in a collection.
 
