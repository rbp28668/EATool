/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import alvahouse.eatool.repository.RepositoryProperties;
import alvahouse.eatool.repository.dto.RepositoryPropertiesDto;
import alvahouse.eatool.repository.persist.RepositoryPropertiesPersistence;

/**
 * Memory "persistence" class for repository properties.  Somewhat simpler than
 * others as there's only one set of repository properties.
 * @author bruce_porteous
 *
 */
public class RepositoryPropertiesPersistenceMemory implements RepositoryPropertiesPersistence {

	private RepositoryPropertiesDto props = new RepositoryPropertiesDto();
	/**
	 * 
	 */
	public RepositoryPropertiesPersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPropertiesPersistence#get()
	 */
	@Override
	public RepositoryPropertiesDto get() throws Exception{
		return props;
	}
	
	@Override
	public void set(RepositoryPropertiesDto repositoryProperties) throws Exception{
		props = repositoryProperties;
		
	}
}
