 /*
 * Repository.java
 *
 * Created on 12 January 2002, 21:41
 */

package alvahouse.eatool.repository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.transform.stream.StreamResult;

import org.apache.bsf.BSFException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import alvahouse.eatool.gui.graphical.standard.AllowableElements;
import alvahouse.eatool.gui.graphical.standard.metamodel.MetaModelDiagramType;
import alvahouse.eatool.gui.graphical.standard.metamodel.MetaModelDiagramTypes;
import alvahouse.eatool.gui.graphical.standard.model.ModelDiagramType;
import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.dto.RepositoryPropertiesDto;
import alvahouse.eatool.repository.dto.scripting.EventMapDto;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.exception.OutputException;
import alvahouse.eatool.repository.exception.RepositoryException;
import alvahouse.eatool.repository.graphical.DiagramFactory;
import alvahouse.eatool.repository.graphical.DiagramTypeFactory;
import alvahouse.eatool.repository.graphical.DiagramTypes;
import alvahouse.eatool.repository.graphical.Diagrams;
import alvahouse.eatool.repository.html.HTMLPageFactory;
import alvahouse.eatool.repository.html.HTMLPages;
import alvahouse.eatool.repository.images.ImageFactory;
import alvahouse.eatool.repository.images.Images;
import alvahouse.eatool.repository.mapping.EntityImportFactory;
import alvahouse.eatool.repository.mapping.ExportMapping;
import alvahouse.eatool.repository.mapping.ExportMappingFactory;
import alvahouse.eatool.repository.mapping.ExportMappings;
import alvahouse.eatool.repository.mapping.ImportMapping;
import alvahouse.eatool.repository.mapping.ImportMappingFactory;
import alvahouse.eatool.repository.mapping.ImportMappings;
import alvahouse.eatool.repository.mapping.RelationshipImportFactory;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.factory.DisplayHintFactory;
import alvahouse.eatool.repository.metamodel.factory.MetaEntityFactory;
import alvahouse.eatool.repository.metamodel.factory.MetaModelFactory;
import alvahouse.eatool.repository.metamodel.factory.MetaRelationshipFactory;
import alvahouse.eatool.repository.metamodel.types.ExtensibleTypes;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyType;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyTypes;
import alvahouse.eatool.repository.metamodel.types.TypeEvent;
import alvahouse.eatool.repository.metamodel.types.TypeEventListener;
import alvahouse.eatool.repository.metamodel.types.TypesFactory;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.model.factory.EntityFactory;
import alvahouse.eatool.repository.model.factory.ModelFactory;
import alvahouse.eatool.repository.model.factory.RelationshipFactory;
import alvahouse.eatool.repository.persist.EventMapPersistence;
import alvahouse.eatool.repository.persist.RepositoryPersistence;
import alvahouse.eatool.repository.persist.RepositoryPropertiesPersistence;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.repository.scripting.EventMapFactory;
import alvahouse.eatool.repository.scripting.ScriptFactory;
import alvahouse.eatool.repository.scripting.ScriptManager;
import alvahouse.eatool.repository.scripting.Scripts;
import alvahouse.eatool.repository.search.SearchEngine;
import alvahouse.eatool.scripting.proxy.RepositoryProxy;
import alvahouse.eatool.util.SettingsManager;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLLoader;
import alvahouse.eatool.util.XMLWriter;
import alvahouse.eatool.util.XMLWriterSAX;
import alvahouse.eatool.util.XSLTTransform;


/**
 * This is the root class of the repository.  All the data in the repository
 * is contained within the fields of this class.
 * @author  rbp28668
 */
public class RepositoryImpl implements TypeEventListener, Repository{

	private UUID key;
	
	private final RepositoryPersistence persistence; // = new RepositoryPersistenceMemory();
	
    private final ExtensibleTypes extensibleTypes;
	private final MetaPropertyTypes types;
    private final MetaModel metaModel;
    private final Model model;
    private final DiagramTypes diagramTypes;
    private final Diagrams diagrams;
    private final MetaModelDiagramTypes metaModelDiagramTypes;
    private final Diagrams metaModelDiagrams;
    private final ImportMappings importMappings;
    private final ExportMappings exportMappings;
    private final Scripts scripts;
    private final HTMLPages pages;
    private final Images images;

    // Events 
    private final String POST_LOAD_EVENT = "PostLoadEvent";
    private final String PRE_SAVE_EVENT = "PreSaveEvent";

    private final static String NAMESPACE = "http://alvagem.co.uk/eatool/repository";
    private final static String OLD_NAMESPACE = "http://rbp28668.co.uk/java/test/repository";

    /** The search engine */
    private SearchEngine search;

    /** Creates new, empty,  Repository */
    public RepositoryImpl(RepositoryPersistence persistence, SettingsManager config) throws RepositoryException {

    	this.persistence = persistence;
    	this.extensibleTypes = new ExtensibleTypes(persistence.getMetaModelPersistence());
    	this.types = new MetaPropertyTypes();
    	this.metaModel = new MetaModel(persistence.getMetaModelPersistence(), types );
    	this.model = new Model(metaModel, persistence.getModelPersistence());
    	this.diagramTypes = new DiagramTypes(this,persistence.getDiagramTypePersistence());
    	this.diagrams = new Diagrams(diagramTypes, persistence.getDiagramPersistence());
    	this.metaModelDiagramTypes = new MetaModelDiagramTypes(this, persistence.getDiagramTypePersistence());
    	this.metaModelDiagrams = new Diagrams(metaModelDiagramTypes, persistence.getMetaModelDiagramPersistence());
    	this.importMappings = new ImportMappings(persistence.getImportMappingPersistence());
    	this.exportMappings = new ExportMappings(persistence.getExportMappingPersistence());
    	this.scripts = new Scripts(persistence.getScriptPersistence());
    	this.pages = new HTMLPages(persistence.getHTMLPagePeristence());
    	this.images = new Images(persistence.getImagePersistence());

    	
    	
    	key = new UUID(); // may be over-ridden later by load.
    	
        try {
            AllowableElements.initInstance(config);
        } catch (ClassNotFoundException e) {
            throw new RepositoryException("Unable to initialise diagram elements from config", e);
        }
        
		MetaPropertyTypes.extendTypesFromConfig(config);
		types.extend(extensibleTypes);
		
		getImportMappings().setParsers(config);
		diagramTypes.setFamilies(config);
		
        // Changes in meta-model need to update the model and possibly diagram types.
        metaModel.addChangeListener(diagramTypes);
        // TODO - import & export mappings.
        
        // And changes in model need to update any diagrams.
        model.addChangeListener(diagrams);
        
        // Repository needs to know if the types change and so does
        // the master types list.
        extensibleTypes.addListener(this);
        
        try {
			metaModelDiagramTypes.addDefaultType(this);
		} catch (Exception e1) {
			throw new RepositoryException("Unable to set default diagram type for metamodel",e1);
		}
        
         // Set up search engine
        search = new SearchEngine();
        model.addChangeListener(search);

        // Set up scripting
        try {
            ScriptManager.initInstance(config);
            ScriptManager.getInstance().declareObject("repository", 
                        new RepositoryProxy(this), 
                        RepositoryProxy.class);
        } catch (BSFException e) {
            throw new RepositoryException("Unable to initialise Script Manager: " + e.getMessage(),e);
        }

    }

    @Override
    public void initialiseNew() {
    	key = new UUID(); // may be over-ridden later by load.
         
        try {
        	String user = System.getProperty("user.name");

        	// Create a blank repository properties.
        	RepositoryProperties props = new RepositoryProperties();
        	props.getVersion().createBy(user);
        	updateProperties(props);
        	
 	        // Event maps need to be initialised with the allowable events
	        // even if no handlers defined.
	        EventMap events = new EventMap();
	        ensureEvents(events);
	        events.getVersion().createBy(user);
	        persistence.getRepositoryEventMapPersistence().set(events.toDto());

	        EventMap modelEvents = new EventMap();
	        ModelDiagramType.defineEventMap(modelEvents);
	        modelEvents.getVersion().createBy(user);
	        persistence.getModelViewerEventMapPersistence().set(modelEvents.toDto());
	        
	        EventMap metaModelEvents = new EventMap();
	        MetaModelDiagramType.defineEventMap(metaModelEvents);
	        metaModelEvents.getVersion().createBy(user);
	        persistence.getMetaModelViewerEventMapPersistence().set(metaModelEvents.toDto());
	        
        } catch (Exception e) {
        	throw new RepositoryException("Unable to define event maps", e);
        }
 
    }
    
    @Override
    public void bindToExisting() {
    	key = new UUID(); // may be over-ridden later by load.
    	
        try {
            search.indexModel(null,model);
        } catch (IOException e) {
            throw new InputException("Unable to index model: " + e.getMessage(),e);
        }

        try {
            getEventMap().fireEvent(POST_LOAD_EVENT, scripts);
        } catch (Exception ex) {
            throw new InputException("Problem running post-load event script: " + ex.getMessage());
        }
    }
    
    @Override
    public void disconnect() throws Exception{
    	persistence.disconnect();
    }
    
	/**
	 * @param events
	 */
	private void ensureEvents(EventMap events) {
		events.ensureEvent(POST_LOAD_EVENT);
		events.ensureEvent(PRE_SAVE_EVENT);
	}
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#loadXML(java.lang.String, alvahouse.eatool.repository.LoadProgress)
     */
    @Override
    public void loadXML(String uri, LoadProgress counter) throws InputException {
        
        
        XMLLoader loader = new XMLLoader();
        loader.setNameSpaces(true);
        
        // Set up handlers for the different objects.
        
        CountHandler countHandler = new CountHandler(counter.getStatus());
		loader.registerContent(NAMESPACE,"RepositorySizes",countHandler);
		loader.registerContent(OLD_NAMESPACE,"RepositorySizes",countHandler);

		RepositoryProperties props = new RepositoryProperties();
		RepositoryPropertiesFactory propsf = new RepositoryPropertiesFactory(counter,props);
		loader.registerContent(NAMESPACE,"RepositoryProperties",propsf);
		loader.registerContent(OLD_NAMESPACE,"RepositoryProperties",propsf);

		EventMap events = new EventMap(); // basic empty map to load into
		ensureEvents(events);
        EventMapFactory eventsmf = new EventMapFactory(counter,events,scripts);
		loader.registerContent(NAMESPACE,"EventMap",eventsmf);
		loader.registerContent(OLD_NAMESPACE,"EventMap",eventsmf);

		ScriptFactory sf = new ScriptFactory(counter,scripts);
		loader.registerContent(NAMESPACE,"Script",sf);
		loader.registerContent(OLD_NAMESPACE,"Script",sf);

     
        MetaEntityFactory mef = new MetaEntityFactory(counter,types, metaModel);
        loader.registerContent(NAMESPACE,"MetaEntity",mef);
        loader.registerContent(OLD_NAMESPACE,"MetaEntity",mef);
        
        MetaRelationshipFactory mrf = new MetaRelationshipFactory(counter,types, metaModel);
        loader.registerContent(NAMESPACE,"MetaRelationship",mrf);
        loader.registerContent(OLD_NAMESPACE,"MetaRelationship",mrf);
        
        EntityFactory ef = new EntityFactory(counter,model,metaModel);
        loader.registerContent(NAMESPACE,"Entity",ef);
        loader.registerContent(OLD_NAMESPACE,"Entity",ef);
        
        RelationshipFactory rf = new RelationshipFactory(counter,model,metaModel);
        loader.registerContent(NAMESPACE,"Relationship",rf);
        loader.registerContent(OLD_NAMESPACE,"Relationship",rf);

        DisplayHintFactory dhf = new DisplayHintFactory(counter,metaModel);
        loader.registerContent(NAMESPACE,"DisplayHint",dhf);
        loader.registerContent(OLD_NAMESPACE,"DisplayHint",dhf);
        
        DiagramTypeFactory dtf = new DiagramTypeFactory(counter,diagramTypes, this, eventsmf, scripts);
        loader.registerContent(NAMESPACE,"DiagramType",dtf);
        loader.registerContent(OLD_NAMESPACE,"DiagramType",dtf);

		DiagramFactory metadf = new DiagramFactory(counter,diagrams,diagramTypes, this, eventsmf);
		loader.registerContent(NAMESPACE,"MetaDiagrams",metadf);
		loader.registerContent(OLD_NAMESPACE,"MetaDiagrams",metadf);
        
		DiagramFactory df = new DiagramFactory(counter,diagrams,diagramTypes, this,  eventsmf);
		loader.registerContent(NAMESPACE,"Diagrams",df);
		loader.registerContent(OLD_NAMESPACE,"Diagrams",df);

		ImportMappingFactory imf = new ImportMappingFactory(counter,importMappings, metaModel);
		loader.registerContent(NAMESPACE,"ImportTranslation",imf);
		loader.registerContent(OLD_NAMESPACE,"ImportTranslation",imf);

		ExportMappingFactory emf = new ExportMappingFactory(counter,exportMappings);
		loader.registerContent(NAMESPACE,"ExportTranslation",emf);
		loader.registerContent(OLD_NAMESPACE,"ExportTranslation",emf);
		
		TypesFactory tf = new TypesFactory(counter,extensibleTypes);
		loader.registerContent(NAMESPACE,"Types",tf);
		loader.registerContent(OLD_NAMESPACE,"Types",tf);
		
		loader.registerContent(NAMESPACE,"Pages",
				new HTMLPageFactory(counter,pages, scripts));

		loader.registerContent(NAMESPACE,"Images",
				new ImageFactory(counter, images));

		EventMap metaModelEvents = new EventMap();
        MetaModelDiagramType.defineEventMap(metaModelEvents);
		loader.registerContent(NAMESPACE,"MetaModelEventMap", 
				new EventMapFactory(counter,metaModelEvents,scripts));

		EventMap modelEvents = new EventMap();
        ModelDiagramType.defineEventMap(modelEvents);
		loader.registerContent(NAMESPACE,"ModelEventMap",
				new EventMapFactory(counter,modelEvents,scripts));

		loader.registerContent(NAMESPACE,"MetaModel",
				new MetaModelFactory(metaModel));
		
		loader.registerContent(NAMESPACE,"Model",
				new ModelFactory(model));
		
		loader.registerContent(NAMESPACE,"Repository",
				new RepositoryFactory(this));

        try {
            loader.parse(uri);
            //System.out.println("Entity Count " + model.getEntityCount());
        }
        catch(SAXParseException e) {
            throw new InputException("SAX Parse Exception. Line " + e.toString() +
                " at line " + e.getLineNumber() + ", column " + e.getColumnNumber(), e);
        }
        catch(SAXException e) {
            throw new InputException("Problem parsing xml file: " + e.getMessage(),e);
        }
        
        try {
        	// ReadModifyWrite as there will already be a record from initialisation.
        	RepositoryPropertiesPersistence rpp = persistence.getRepositoryPropertiesPersistence();
        	RepositoryPropertiesDto original = rpp.get();
        	RepositoryPropertiesDto updated = props.toDto();
        	String rev = original.getVersion().getVersion();
        	updated.getVersion().update(rev);
        	rpp.set(updated);
        } catch (Exception e) {
        	throw new InputException("Unable to save properties into model when loading XML",e);
        }
        
        try {
        	updateEventMap(events.toDto(), persistence.getRepositoryEventMapPersistence());
        	updateEventMap(metaModelEvents.toDto(),persistence.getMetaModelViewerEventMapPersistence());
        	updateEventMap(modelEvents.toDto(),persistence.getModelViewerEventMapPersistence());
        } catch (Exception e) {
        	throw new InputException("Unable to save events when loading XML",e);
        }
        
        try {
            search.indexModel(null,model);
        } catch (IOException e) {
            throw new InputException("Unable to index model: " + e.getMessage(),e);
        }

        try {
            getEventMap().fireEvent(POST_LOAD_EVENT, scripts);
        } catch (Exception ex) {
            throw new InputException("Problem running post-load event script: " + ex.getMessage());
        }
        
        //System.out.println("**Loaded XML");
    }

	/**
	 * Allow an event map to be updated with read/modify/write.  This is needed as event maps are
	 * created as part of the repository intialisation and just writing will result in a stale
	 * data exception when the versions do not match.
	 * @param updated is the EventMapDto to write.
	 * @param emp is the EventMapPersistence to use.
	 * @throws Exception
	 */
	private void updateEventMap(EventMapDto updated, EventMapPersistence emp) throws Exception {
		EventMapDto original = emp.get();
		String rev = original.getVersion().getVersion();
		updated.getVersion().update(rev);
		emp.set(updated);
	}
    
	/* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#saveXML(java.lang.String)
     */
    @Override
    public void saveXML(String uri) throws OutputException {
        try {

            getEventMap().fireEvent(PRE_SAVE_EVENT, scripts);
            
            XMLWriter writer = new XMLWriterSAX(new FileOutputStream(uri));
            try{
	            writer.startXML();
	            writer.setNamespace("ea",NAMESPACE);
	            writer.startEntity("Repository");
	            writer.addAttribute("uuid", key.toString());
	            
	            RepositoryProperties properties = new RepositoryProperties(
	            		persistence.getRepositoryPropertiesPersistence().get());
	            properties.writeXML(writer);
	            
	            CountHandler counts = new CountHandler(this);
	            counts.writeXML(writer);
	            
	            scripts.writeXML(writer);
	            getEventMap().writeXML(writer);
	            extensibleTypes.writeXML(writer);
	            metaModel.writeXML(writer);
	            model.writeXML(writer);
	            images.writeXML(writer);
	            diagramTypes.writeXML(writer);
	            metaModelDiagrams.writeXML(writer,"MetaModelDiagrams");
	            diagrams.writeXML(writer,"Diagrams");
	            importMappings.writeXML(writer);
	            exportMappings.writeXML(writer);
	            pages.writeXML(writer);
	    		getMetaModelViewerEvents().writeXML(writer, "MetaModelEventMap"); 
	    		getModelViewerEvents().writeXML(writer, "ModelEventMap");

	            writer.stopEntity();
	            writer.stopXML();
            } finally {
                writer.close();
            }
        }
        catch(Exception e) {
            throw new OutputException("problem saving xml file: " + e.getMessage(),e);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#ImportXML(java.lang.String, alvahouse.eatool.repository.mapping.ImportMapping)
     */
    @Override
    public void ImportXML(String uri, ImportMapping mapping)
        throws InputException {
            
      
        /* need to split the ImportTranslation to get the 2 sub-fields
        * EntityTranslationSet and RelationTranslationSet as shown below:
        * 
        * <ImportTranslation name = "Chameleon">
        *   <EntityTranslationSet>
        *     <EntityTranslation type="Business Process" uuid="9e6b51-cd45-11d5-936a-00047660c89a">
        *       <PropertyTranslation type="Name" uuid="000000-0000-0000-0000-00000000" key="true"/>
        *       <PropertyTranslation type="SourceSystemID" uuid="000000-0000-0000-0000-00000000" key="true"/>
        *     </EntityTranslation>
        *   </EntityTranslationSet>
        *   <RelationTranslationSet>
        *   </RelationTranslationSet>
        * </ImportTranslation>
        */

        String parserClass = importMappings.lookupParser(mapping.getParserName());
        if(parserClass == null) {
            throw new InputException("Can't get parser class name for " + mapping.getParserName());
        }
        XMLLoader loader = new XMLLoader(parserClass, mapping.getTransformPath());
        
        EntityImportFactory ef = new EntityImportFactory(mapping,model, metaModel);
        loader.registerContent("Entity",ef);
        
        RelationshipImportFactory rf = new RelationshipImportFactory(mapping, model, metaModel);
        loader.registerContent("Relationship",rf);

        try {
            FileInputStream fis = new FileInputStream(uri);
            loader.parse(fis);
            
            rf.dispose();
            ef.dispose();
        }
        catch(SAXParseException e) {
            throw new InputException("SAX Parse Exception. Line " + e.toString() +
                " at line " + e.getLineNumber() + ", column " + e.getColumnNumber(), e);
        }
        catch(SAXException e) {
            throw new InputException("Problem parsing xml file: " + e.getMessage(),e);
        } catch (FileNotFoundException e) {
            throw new InputException("File " + uri + " not found");
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#ExportXML(java.lang.String, alvahouse.eatool.repository.mapping.ExportMapping)
     */
    @Override
    public void ExportXML(String uri, ExportMapping mapping)
    throws InputException {
        try {
            
            XSLTTransform transform = new XSLTTransform(mapping.getTransformPath());
            StreamResult result = new StreamResult(new FileOutputStream(uri));
            result.setSystemId(uri);
            transform.setResult(result);
            
          XMLWriter writer = new XMLWriterSAX(transform.asContentHandler());
          writer.startXML();
          writer.setNamespace("ea",NAMESPACE);
          writer.startEntity("Repository");

          if(mapping.hasComponent(TYPES)){
              extensibleTypes.writeXML(writer);
          }
          
          if(mapping.hasComponent(META_MODEL)){
              metaModel.writeXML(writer);
          }
          
          if(mapping.hasComponent(MODEL)){
              model.writeXML(writer);
          }
          
          if(mapping.hasComponent(DIAGRAM_TYPES)){
              diagramTypes.writeXML(writer);
          }
          
          if(mapping.hasComponent(DIAGRAMS)){
              diagrams.writeXML(writer,"Diagrams");
          }
                    
          if(mapping.hasComponent(IMPORT_MAPPINGS)){
              importMappings.writeXML(writer);
          }
          
          if(mapping.hasComponent(EXPORT_MAPPINGS)){
              exportMappings.writeXML(writer);
          }

          if(mapping.hasComponent(SCRIPTS)){
              scripts.writeXML(writer);
          }
          
          if(mapping.hasComponent(EVENTS)){
              getEventMap().writeXML(writer);
          }

          if(mapping.hasComponent(PAGES)){
              pages.writeXML(writer);
          }
          
          if(mapping.hasComponent(IMAGES)){
              images.writeXML(writer);
          }
          
          writer.stopEntity();
          writer.stopXML();
          writer.close();
      }
      catch(Exception e) {
          throw new OutputException("problem exporting xml file: " + e.getMessage(),e);
      }
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getModel()
     */
    @Override
    public Model getModel() {
        return model;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getMetaModel()
     */
    @Override
    public MetaModel getMetaModel() {
        return metaModel;
    }
 
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getMetaModelDiagrams()
     */
    @Override
    public Diagrams getMetaModelDiagrams(){
        return metaModelDiagrams;
    }
    
    
    
 	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.Repository#getMetaModelDiagramTypes()
	 */
	@Override
	public MetaModelDiagramTypes getMetaModelDiagramTypes() {
		return metaModelDiagramTypes;
	}

	/* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getDiagrams()
     */
    @Override
 	public Diagrams getDiagrams() {
 		return diagrams;
 	}
 	
 	/* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getDiagramTypes()
     */
 	@Override
 	public DiagramTypes getDiagramTypes() {
 		return diagramTypes;
 	}
 	
 	/* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getImportMappings()
     */
 	@Override
 	public ImportMappings getImportMappings() {
 	    return importMappings;
 	}
 	
 	/* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getExportMappings()
     */
 	@Override
 	public ExportMappings getExportMappings() {
 	    return exportMappings;
 	}
 	
 	/* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getScripts()
     */
 	@Override
 	public Scripts getScripts(){
 	    return scripts;
 	}
 	
 	/* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getEventMap()
     */
 	@Override
 	public EventMap getEventMap() throws Exception{
 	    return new EventMap(persistence.getRepositoryEventMapPersistence().get());
 	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.Repository#setEventMap(alvahouse.eatool.repository.scripting.EventMap)
	 */
	@Override
	public void setEventMap(EventMap events) throws Exception {
		persistence.getRepositoryEventMapPersistence().set(events.toDto());
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.Repository#getMetaModelViewerEvents()
	 */
	@Override
	public EventMap getMetaModelViewerEvents() throws Exception{
		return new EventMap(persistence.getMetaModelViewerEventMapPersistence().get());
	}
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.Repository#setMetaModelViewerEvents(alvahouse.eatool.repository.scripting.EventMap)
	 */
	@Override
	public void setMetaModelViewerEvents(EventMap metaModelViewerEvents) throws Exception {
		persistence.getMetaModelViewerEventMapPersistence().set(metaModelViewerEvents.toDto());
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.Repository#getModelViewerEvents()
	 */
	@Override
	public EventMap getModelViewerEvents() throws Exception{
		return new EventMap(persistence.getModelViewerEventMapPersistence().get());
	}

 	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.Repository#setModelViewerEvents(alvahouse.eatool.repository.scripting.EventMap)
	 */
	@Override
	public void setModelViewerEvents(EventMap modelViewerEvents) throws Exception {
		persistence.getModelViewerEventMapPersistence().set(modelViewerEvents.toDto());
	}

	/* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getProperties()
     */
    @Override
 	public RepositoryProperties getProperties() throws Exception{
 	    return new RepositoryProperties(persistence.getRepositoryPropertiesPersistence().get());
 	}
 	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.Repository#updateProperties(alvahouse.eatool.repository.RepositoryProperties)
	 */
    @Override
	public void updateProperties(RepositoryProperties properties) throws Exception {
		persistence.getRepositoryPropertiesPersistence().set(properties.toDto());
	}

 	/* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getTypes()
     */
    @Override
 	public MetaPropertyTypes getTypes() throws Exception {
 	    return new MetaPropertyTypes(extensibleTypes);
 	}
 	
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getExtensibleTypes()
     */
    @Override
    public ExtensibleTypes getExtensibleTypes() {
        return extensibleTypes;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getPages()
     */
    @Override
    public HTMLPages getPages(){
        return pages;
    }
    
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getImages()
     */
    @Override
    public Images getImages() {
        return images;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getDeleteDependencies(alvahouse.eatool.repository.model.Entity)
     */
    @Override
    public DeleteDependenciesList getDeleteDependencies(Entity e)  throws Exception{
        DeleteDependenciesList dependencies = new DeleteDependenciesList();
        getModel().getDeleteDependencies(dependencies,e, this);
        return dependencies;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getDeleteDependencies(alvahouse.eatool.repository.model.Relationship)
     */
    @Override
    public DeleteDependenciesList getDeleteDependencies(Relationship r)  throws Exception{
        DeleteDependenciesList dependencies = new DeleteDependenciesList();
        getModel().getDeleteDependencies(dependencies,r);
        return dependencies;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getDeleteDependencies(alvahouse.eatool.repository.metamodel.MetaEntity)
     */
    @Override
    public DeleteDependenciesList getDeleteDependencies(MetaEntity me)  throws Exception{
        DeleteDependenciesList dependencies = new DeleteDependenciesList();
        getMetaModel().getDeleteDependencies(dependencies,me, this);
        
        // Need to delete all the entities that belong to this meta-entity
        for(Entity e : getModel().getEntities()){
            if(me.equals(e.getMeta()))
                getModel().getDeleteDependencies(dependencies, e, this);
        }
        return dependencies;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#getDeleteDependencies(alvahouse.eatool.repository.metamodel.MetaRelationship)
     */
    @Override
    public DeleteDependenciesList getDeleteDependencies(MetaRelationship mr) throws Exception {
        DeleteDependenciesList dependencies = new DeleteDependenciesList();
        getMetaModel().getDeleteDependencies(dependencies,mr);

        // Need to delete all the entities that belong to this meta-entity
        for(Relationship r : getModel().getRelationships()) {
            if(mr.equals(r.getMeta()))
                getModel().getDeleteDependencies(dependencies, r);
        }
        return dependencies;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#deleteContents()
     */
    @Override
    public void deleteContents() throws Exception{
    	getDiagrams().deleteContents();
    	getDiagramTypes().deleteContents();
        getModel().deleteContents();
        getMetaModel().deleteContents();
        getImportMappings().deleteContents();
        getScripts().deleteContents();
        getEventMap().deleteHandlers();
        
        getExtensibleTypes().deleteContents();
        getPages().deleteContents();
        getImages().deleteContents();
        
        RepositoryProperties props = getProperties();
        props.reset(); 
        updateProperties(props);
        
        EventMap eventMap = getEventMap();
        eventMap.deleteHandlers();
        ensureEvents(eventMap);
        setEventMap(eventMap);
        
        EventMap modelViewerEvents = getModelViewerEvents();
        modelViewerEvents.deleteHandlers();
        ModelDiagramType.defineEventMap(modelViewerEvents);
        setModelViewerEvents(modelViewerEvents);

        EventMap metaModelViewerEvents = getMetaModelViewerEvents();
        metaModelViewerEvents.deleteHandlers();
        MetaModelDiagramType.defineEventMap(metaModelViewerEvents);
        setMetaModelViewerEvents(metaModelViewerEvents);

    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#typeAdded(alvahouse.eatool.repository.metamodel.types.TypeEvent)
     */
    @Override
    public void typeAdded(TypeEvent event) {
    	// NOP
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#typeChanged(alvahouse.eatool.repository.metamodel.types.TypeEvent)
     */
    @Override
    public void typeChanged(TypeEvent event) throws Exception{
        MetaPropertyType changedType = event.getType();
        List<MetaProperty> changed = new LinkedList<MetaProperty>();
        for(MetaEntity me : metaModel.getMetaEntities()){
            changed.clear();
            for(MetaProperty mp : me.getMetaProperties()){
                if(mp.getMetaPropertyType().equals(changedType)){
                    changed.add(mp);
                }
            }
            
            if(!changed.isEmpty()){
                for(Entity entity : model.getEntitiesOfType(me)){
                    boolean isChanged = false;
                    for(MetaProperty mp : changed){
                        Property p = entity.getPropertyByMeta(mp);
                        try {
                            mp.getMetaPropertyType().validate(p.getValue());
                        } catch (IllegalArgumentException x){
                            p.setValue(mp.getDefaultValue());
                            isChanged = true;
                        }
                    }
                    if(isChanged){
                        model.updateEntity(entity);
                    }
                }

            }
            
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#typeDeleted(alvahouse.eatool.repository.metamodel.types.TypeEvent)
     */
    @Override
    public void typeDeleted(TypeEvent event) throws Exception{
        MetaPropertyType deletedType = event.getType();
        for(MetaEntity me : metaModel.getMetaEntities()){
            Collection<MetaProperty> metaProperties = new LinkedList<MetaProperty>();
            metaProperties.addAll(me.getMetaProperties()); // copy as will delete from original
            for(MetaProperty mp : metaProperties){
                if(mp.getMetaPropertyType().equals(deletedType)){
                    me.deleteMetaProperty(mp.getKey());
                }
            }
        }
    }

    
    @SuppressWarnings("unused")  // as just for debugging
	private class DebugContentHandler implements ContentHandler {

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
         */
        public void setDocumentLocator(Locator arg0) {
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#startDocument()
         */
        public void startDocument() throws SAXException {
            System.out.println("Start Document");
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#endDocument()
         */
        public void endDocument() throws SAXException {
            System.out.println("End Document");
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
         */
        public void startPrefixMapping(String arg0, String arg1) throws SAXException {
            System.out.println("Start Prefix Mapping");
            
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
         */
        public void endPrefixMapping(String arg0) throws SAXException {
            System.out.println("End Prefix Mapping");
            
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException {
            System.out.println("Start Element" + arg0 + "," + arg1 + "," + arg2);
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        public void endElement(String arg0, String arg1, String arg2) throws SAXException {
            System.out.println("End Element" + arg0 + "," + arg1 + "," + arg2);
            
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#characters(char[], int, int)
         */
        public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
            String str = new String(arg0,arg1,arg2);
            System.out.println("Characters " + str);
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
         */
        public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
         */
        public void processingInstruction(String arg0, String arg1) throws SAXException {
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
         */
        public void skippedEntity(String arg0) throws SAXException {
        }
        
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#index(alvahouse.eatool.repository.LoadProgress)
     */
    @Override
    public void index(LoadProgress progress) throws IOException {
        search.indexModel(progress, model);
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#searchForEntities(java.lang.String)
     */
    @Override
    public Set<Entity> searchForEntities(String query) throws RepositoryException{
        try {
            return search.searchForEntities(query);
        } catch (Exception x){
            throw new RepositoryException("Unable to search: " + x.getMessage(),x);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.Repository#searchForEntitiesOfType(java.lang.String, java.util.Set)
     */
    @Override
    public Set<Entity> searchForEntitiesOfType(String query, Set<MetaEntity> contents) throws RepositoryException{
        try {
            return search.searchForEntitiesOfType(query, contents);
        } catch (Exception x){
            throw new RepositoryException("Unable to search: " + x.getMessage(),x);
        }
    }

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.base.KeyedItem#getKey()
	 */
	@Override
	public UUID getKey() {
		return key;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.base.KeyedItem#setKey(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void setKey(UUID uuid) {
		this.key = uuid;
	}



    
    
}
