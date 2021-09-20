/**
 * 
 */
package alvahouse.eatool.repository.persist.couchdb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
 * @author bruce_porteous
 *
 */
public class RepositoryPersistenceCouchDb implements RepositoryPersistence {

	private final CouchDB couch;
	
	
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

		// Close down write access on the new database.
	    StringBuilder textBuilder = new StringBuilder();
		try(InputStream is = RepositoryPersistenceCouchDb.class.getResourceAsStream("validation.js")) {
		    try (Reader reader = new BufferedReader(new InputStreamReader
		      (is, Charset.forName(StandardCharsets.UTF_8.name())))) {
		        int c = 0;
		        while ((c = reader.read()) != -1) {
		            textBuilder.append((char) c);
		        }
		    }
		}
		
		DesignDocument.create("_auth")
		.updateValidation(textBuilder.toString())
		.save(couch, database);

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
	public RepositoryPersistenceCouchDb(CouchDB couch, String database) throws Exception {
		this.couch = couch;
		
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

	@Override
	public void disconnect() throws Exception{
		couch.clearCredentials();
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
