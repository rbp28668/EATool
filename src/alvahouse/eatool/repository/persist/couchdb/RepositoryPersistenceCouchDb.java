/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

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
import alvahouse.eatool.repository.persist.memory.DiagramPersistenceMemory;
import alvahouse.eatool.repository.persist.memory.DiagramTypePersistenceMemory;
import alvahouse.eatool.repository.persist.memory.EventMapPersistenceMemory;
import alvahouse.eatool.repository.persist.memory.ExportMappingPersistenceMemory;
import alvahouse.eatool.repository.persist.memory.HTMLPagePersistenceMemory;
import alvahouse.eatool.repository.persist.memory.ImagePersistenceMemory;
import alvahouse.eatool.repository.persist.memory.ImportMappingPersistenceMemory;
import alvahouse.eatool.repository.persist.memory.MetaModelPersistenceMemory;
import alvahouse.eatool.repository.persist.memory.ModelPersistenceMemory;
import alvahouse.eatool.repository.persist.memory.RepositoryPropertiesPersistenceMemory;
import alvahouse.eatool.repository.persist.memory.ScriptPersistenceMemory;

/**
 * @author bruce_porteous
 *
 */
public class RepositoryPersistenceCouchDb implements RepositoryPersistence {

	private final CouchDB couch = new CouchDB();
	
	
	private final RepositoryPropertiesPersistence repositoryPropertiesPersistence;
	private final EventMapPersistence eventMapPersistence;
	private final EventMapPersistence eventMapModelPersistence;
	private final EventMapPersistence eventMapMetaModelPersistence;
	private final ModelPersistence modelPersistence;
	private final MetaModelPersistence metaModelPersistence;
	private final ScriptPersistence scriptPersistence;
	private final ImagePersistence imagePersistence;
	private final ImportMappingPersistence importMappingPersistence;
	private final ExportMappingPersistence exportMappingPersistence;
	private final HTMLPagePersistence htmlPagePersistence;
	private final DiagramPersistence diagramPersistence;
	private final DiagramPersistence diagramMetaModelPersistence;
	private final DiagramTypePersistence diagramTypePersistence;

	
	
	public static void initialiseDatabase(CouchDB couch, String database) throws Exception {
		
		couch.database().create(database);
		
		MetaModelPersistenceCouchDb.initialiseDatabase(couch, database);
		ModelPersistenceCouchDb.initialiseDatabase(couch, database);
		ScriptPersistenceCouchDb.initialiseDatabase(couch, database);
		HTMLPagePersistenceCouchDb.initialiseDatabase(couch, database);
		ImagePersistenceCouchDb.initialiseDatabase(couch, database);
		DiagramPersistenceCouchDb.initialiseDatabase(couch, database);
		DiagramTypePersistenceCouchDb.initialiseDatabase(couch, database);
		ExportMappingPersistenceCouchDb.initialiseDatabase(couch, database);
		ImportMappingPersistenceCouchDb.initialiseDatabase(couch, database);
	}
	
	/**
	 * 
	 */
	public RepositoryPersistenceCouchDb(String database) throws Exception {
		repositoryPropertiesPersistence = new RepositoryPropertiesPersistenceCouchDb(couch,  database);
		eventMapPersistence = new EventMapPersistenceCouchDb(couch,  database, "events");
		eventMapModelPersistence = new EventMapPersistenceCouchDb(couch,  database, "modelEvents");
		eventMapMetaModelPersistence = new EventMapPersistenceCouchDb(couch,  database, "metaModelEvents");
		modelPersistence = new ModelPersistenceCouchDb(couch,  database);
		metaModelPersistence = new MetaModelPersistenceCouchDb(couch,  database);
		scriptPersistence = new ScriptPersistenceCouchDb(couch,  database);
		imagePersistence = new ImagePersistenceCouchDb(couch,  database);
		importMappingPersistence = new ImportMappingPersistenceCouchDb(couch,  database);
		exportMappingPersistence = new ExportMappingPersistenceCouchDb(couch,  database);
		htmlPagePersistence = new HTMLPagePersistenceCouchDb(couch,  database);
		diagramPersistence = new DiagramPersistenceCouchDb(couch,  database);
		diagramMetaModelPersistence = new DiagramPersistenceCouchDb(couch,  database);
		diagramTypePersistence = new DiagramTypePersistenceCouchDb(couch,  database);

	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getRepositoryPropertiesPersistence()
	 */
	@Override
	public RepositoryPropertiesPersistence getRepositoryPropertiesPersistence() {
		return repositoryPropertiesPersistence;
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
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getMetaModelPersistence()
	 */
	@Override
	public MetaModelPersistence getMetaModelPersistence() {
		return metaModelPersistence;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.RepositoryPersistence#getModelPersistence()
	 */
	@Override
	public ModelPersistence getModelPersistence() {
		return modelPersistence;
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

}
