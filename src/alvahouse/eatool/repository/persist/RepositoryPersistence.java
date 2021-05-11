/**
 * 
 */
package alvahouse.eatool.repository.persist;

/**
 * @author bruce_porteous
 *
 */
public interface RepositoryPersistence {

	/**
	 * @return
	 */
	ModelPersistence getModelPersistence();

	/**
	 * @return
	 */
	MetaModelPersistence getMetaModelPersistence();

}
