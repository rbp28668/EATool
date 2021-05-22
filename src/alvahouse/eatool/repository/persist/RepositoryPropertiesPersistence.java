/**
 * 
 */
package alvahouse.eatool.repository.persist;

import alvahouse.eatool.repository.RepositoryProperties;

/**
 * @author bruce_porteous
 *
 */
public interface RepositoryPropertiesPersistence {

	/**
	 * Gets the properties for the repository
	 * @return the properties.
	 */
	public RepositoryProperties get() throws Exception;
	
	/**
	 * Sets the parameters for the repository.
	 * @param repositoryProperties
	 */
	public void set(RepositoryProperties repositoryProperties) throws Exception;

}
