/**
 * 
 */
package alvahouse.eatool.repository.persist;

import alvahouse.eatool.repository.dto.RepositoryPropertiesDto;

/**
 * @author bruce_porteous
 *
 */
public interface RepositoryPropertiesPersistence {

	/**
	 * Gets the properties for the repository
	 * @return the properties.
	 */
	public RepositoryPropertiesDto get() throws Exception;
	
	/**
	 * Sets the parameters for the repository.
	 * @param repositoryProperties
	 */
	public void set(RepositoryPropertiesDto repositoryProperties) throws Exception;

}
