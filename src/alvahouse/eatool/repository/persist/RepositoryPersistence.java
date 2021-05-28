/**
 * 
 */
package alvahouse.eatool.repository.persist;

/**
 * Overall interface for managing persistence in the model.  Rather than one big interface with a huge
 * number of add/update/delete methods this provides persistence classes for smaller areas of the model.
 * Generally this makes managing the persistence of a part of a model more manageable and localises
 * special cases. Also see "Interface Segregation Principle".
 * @author bruce_porteous
 *
 */
public interface RepositoryPersistence {

	/**
	 * @return
	 */
	RepositoryPropertiesPersistence getRepositoryPropertiesPersistence();
	
	EventMapPersistence getRepositoryEventMapPersistence();
	EventMapPersistence getModelViewerEventMapPersistence();
	EventMapPersistence getMetaModelViewerEventMapPersistence();

	/**
	 * @return
	 */
	MetaModelPersistence getMetaModelPersistence();

	/**
	 * @return
	 */
	ModelPersistence getModelPersistence();

	/**
	 * @return
	 */
	ScriptPersistence getScriptPersistence();
	
	/**
	 * @return
	 */
	ImagePersistence getImagePersistence();

}
