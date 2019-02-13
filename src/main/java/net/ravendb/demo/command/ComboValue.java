
package net.ravendb.demo.command;

import java.util.Objects;


/**
 * Generic Value holder[Id,Name]
 */
public class ComboValue {
	
    private  String id;
    private  String name;

    public ComboValue(String id, String name) {

        this.id = id;
        this.name = name;
    }
    
    public static ComboValue  NULL =new ComboValue(null,""); 
       
    
    public String getId() {

        return id;
    }

    public String getName() {

        return name;
    }
    
    public void setId(String id) {
    
        this.id = id;
    }

    
    public void setName(String name) {
    
        this.name = name;
    }


    @Override
    public int hashCode() {
        return 31+(id==null?0:id.hashCode());
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ComboValue)) {
            return false;
        }
        
        ComboValue that = (ComboValue) obj;
        
        return Objects.equals(this.id, that.id);
    }

    @Override
    public String toString() {

        return this.name;
    }
}
