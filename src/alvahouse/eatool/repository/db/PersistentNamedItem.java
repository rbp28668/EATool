/**
 * 
 */
package alvahouse.eatool.repository.db;


/**
 * @author bruce.porteous
 *
 */
public class PersistentNamedItem  extends PersistentKeyedItem{

		private String name;
		private String description;

	    public String getName(){
	    	return name;
	    }

	    public void setName(String name){
	    	assert(name != null);
	    	this.name = name;
	    }

	    public String getDescription(){
	    	return description;
	    }

	    public void setDescription(String desc){
	    	this.description = desc;
	    }

}
