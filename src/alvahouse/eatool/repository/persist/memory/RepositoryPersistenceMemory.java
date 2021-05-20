/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import alvahouse.eatool.repository.persist.MetaModelPersistence;
import alvahouse.eatool.repository.persist.ModelPersistence;
import alvahouse.eatool.repository.persist.RepositoryPersistence;
import alvahouse.eatool.repository.persist.ScriptPersistence;

/**
 * Persistence class to manage the repository in memory.
 * @author bruce_porteous
 *
 */
public class RepositoryPersistenceMemory implements RepositoryPersistence {

	ModelPersistence modelPersistence = new ModelPersistenceMemory();
	MetaModelPersistence metaModelPersistence = new MetaModelPersistenceMemory();
	ScriptPersistence scriptPersistence = new ScriptPersistenceMemory();
	
	/**
	 * 
	 */
	public RepositoryPersistenceMemory() {
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

}
