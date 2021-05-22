/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import alvahouse.eatool.repository.persist.ImagePersistence;
import alvahouse.eatool.repository.persist.MetaModelPersistence;
import alvahouse.eatool.repository.persist.ModelPersistence;
import alvahouse.eatool.repository.persist.RepositoryPersistence;
import alvahouse.eatool.repository.persist.RepositoryPropertiesPersistence;
import alvahouse.eatool.repository.persist.ScriptPersistence;

/**
 * Persistence class to manage the repository in memory.
 * @author bruce_porteous
 *
 */
public class RepositoryPersistenceMemory implements RepositoryPersistence {

	RepositoryPropertiesPersistence repositoryPropertiesPersistence = new RepositoryPropertiesPersistenceMemory();
	ModelPersistence modelPersistence = new ModelPersistenceMemory();
	MetaModelPersistence metaModelPersistence = new MetaModelPersistenceMemory();
	ScriptPersistence scriptPersistence = new ScriptPersistenceMemory();
	ImagePersistence imagePersistence = new ImagePersistenceMemory();
	/**
	 * 
	 */
	public RepositoryPersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getRepositoryPropertiesPersistence()
	 */
	@Override
	public RepositoryPropertiesPersistence getRepositoryPropertiesPersistence() {
		return repositoryPropertiesPersistence;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getModelPersistence()
	 */
	@Override
	public ModelPersistence getModelPersistence() {
		return modelPersistence;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getMetaModelPersistence()
	 */
	@Override
	public MetaModelPersistence getMetaModelPersistence() {
		return metaModelPersistence;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getScriptPersistence()
	 */
	@Override
	public ScriptPersistence getScriptPersistence() {
		return scriptPersistence;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getImagePersistence()
	 */
	@Override
	public ImagePersistence getImagePersistence() {
		return imagePersistence;
	}

}
