/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import alvahouse.eatool.repository.RepositoryProperties;
import alvahouse.eatool.repository.persist.RepositoryPropertiesPersistence;

/**
 * Memory "persistence" class for repository properties.  Somewhat simpler than
 * others as there's only one set of repository properties.
 * @author bruce_porteous
 *
 */
public class RepositoryPropertiesPersistenceMemory implements RepositoryPropertiesPersistence {

	private RepositoryProperties props = new RepositoryProperties();
	/**
	 * 
	 */
	public RepositoryPropertiesPersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPropertiesPersistence#get()
	 */
	@Override
	public RepositoryProperties get() throws Exception{
		return (RepositoryProperties) props.clone();
	}
	
	@Override
	public void set(RepositoryProperties repositoryProperties) throws Exception{
		props = (RepositoryProperties) repositoryProperties.clone();
		
	}
}
