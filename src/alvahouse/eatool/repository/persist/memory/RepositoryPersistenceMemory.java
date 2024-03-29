/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import alvahouse.eatool.repository.persist.DiagramPersistence;
import alvahouse.eatool.repository.persist.DiagramTypePersistence;
import alvahouse.eatool.repository.persist.EventMapPersistence;
import alvahouse.eatool.repository.persist.ExportMappingPersistence;
import alvahouse.eatool.repository.persist.HTMLPagePersistence;
import alvahouse.eatool.repository.persist.ImagePersistence;
import alvahouse.eatool.repository.persist.ImportMappingPersistence;
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
	EventMapPersistence eventMapPersistence = new EventMapPersistenceMemory();
	EventMapPersistence eventMapModelPersistence = new EventMapPersistenceMemory();
	EventMapPersistence eventMapMetaModelPersistence = new EventMapPersistenceMemory();
	
	ModelPersistence modelPersistence = new ModelPersistenceMemory();
	MetaModelPersistence metaModelPersistence = new MetaModelPersistenceMemory();
	ScriptPersistence scriptPersistence = new ScriptPersistenceMemory();
	ImagePersistence imagePersistence = new ImagePersistenceMemory();
	ImportMappingPersistence importMappingPersistence = new ImportMappingPersistenceMemory();
	ExportMappingPersistence exportMappingPersistence = new ExportMappingPersistenceMemory();
	HTMLPagePersistence htmlPagePersistence = new HTMLPagePersistenceMemory();
	DiagramPersistence diagramPersistence = new DiagramPersistenceMemory();
	DiagramPersistence diagramMetaModelPersistence = new DiagramPersistenceMemory();
	DiagramTypePersistence diagramTypePersistence = new DiagramTypePersistenceMemory();
	
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

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getRepositoryEventMapPersistence()
	 */
	@Override
	public EventMapPersistence getRepositoryEventMapPersistence() {
		return eventMapPersistence;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getModelViewerEventMapPersistence()
	 */
	@Override
	public EventMapPersistence getModelViewerEventMapPersistence() {
		return eventMapModelPersistence;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getMetaModelViewerEventMapPersistence()
	 */
	@Override
	public EventMapPersistence getMetaModelViewerEventMapPersistence() {
		return eventMapMetaModelPersistence;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getImportMappingPersistence()
	 */
	@Override
	public ImportMappingPersistence getImportMappingPersistence() {
		return importMappingPersistence;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getExportMappingPersistence()
	 */
	@Override
	public ExportMappingPersistence getExportMappingPersistence() {
		return exportMappingPersistence;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getHTMLPagePeristence()
	 */
	@Override
	public HTMLPagePersistence getHTMLPagePeristence() {
		return htmlPagePersistence;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getDiagramPersistence()
	 */
	@Override
	public DiagramPersistence getDiagramPersistence() {
		return diagramPersistence;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getMetaModelDiagramPersistence()
	 */
	@Override
	public DiagramPersistence getMetaModelDiagramPersistence() {
		return diagramMetaModelPersistence;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getDiagramTypePersistence()
	 */
	@Override
	public DiagramTypePersistence getDiagramTypePersistence() {
		return diagramTypePersistence;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#disconnect()
	 */
	@Override
	public void disconnect() throws Exception {
		// Nop for in-memory repository.
	}

}
