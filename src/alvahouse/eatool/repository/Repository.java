/*
 * Repository.java
 * Project: EATool
 * Created on 23 Dec 2007
 *
 */
package alvahouse.eatool.repository;

import java.io.IOException;
import java.util.Set;

import alvahouse.eatool.gui.graphical.standard.metamodel.MetaModelDiagramTypes;
import alvahouse.eatool.repository.base.DeleteDependenciesList;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.exception.OutputException;
import alvahouse.eatool.repository.exception.RepositoryException;
import alvahouse.eatool.repository.graphical.DiagramTypes;
import alvahouse.eatool.repository.graphical.Diagrams;
import alvahouse.eatool.repository.html.HTMLPages;
import alvahouse.eatool.repository.images.Images;
import alvahouse.eatool.repository.mapping.ExportMapping;
import alvahouse.eatool.repository.mapping.ExportMappings;
import alvahouse.eatool.repository.mapping.ImportMapping;
import alvahouse.eatool.repository.mapping.ImportMappings;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.types.ExtensibleTypes;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyTypes;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.repository.scripting.Scripts;

public interface Repository extends KeyedItem {

    // Bitmasks for selecting different parts to export.
    public final static int META_MODEL = 1;
    public final static int MODEL = 2;
    public final static int DIAGRAM_TYPES = 4;
    public final static int DIAGRAMS = 8;
    public final static int IMPORT_MAPPINGS = 16;
    public final static int EXPORT_MAPPINGS = 32;
    public final static int SCRIPTS = 64;
    public final static int EVENTS = 128;
    public final static int TYPES = 256;
    public final static int PAGES = 512;
    public final static int IMAGES = 1024;

    
    public void initialiseNew();
    public void bindToExisting();


    /** loads the repository from an XML stream obtained from the given uri.
     * @param uri is the resource identifier that determines where to read
     * the input data from.
     * @throws InputException if there is invalid input data
     */
    public abstract void loadXML(String uri, LoadProgress counter)
            throws InputException;

    /** 
     * Saves the repository as XML.
     * @param uri describes where to save the xml to
     * @throws OutputException in the event of a problem
     */
    public abstract void saveXML(String uri) throws OutputException;

    /** Imports external data from an XML stream obtained from the given uri.
     * @param uri is the resource identifier that determines where to read
     * the input data from.
     * @paramConfig is the configuration that specifies how to map the external
     * data on to the internal meta-model.
     * @throws InputException if there is invalid input data
     */
    public abstract void ImportXML(String uri, ImportMapping mapping)
            throws InputException;

    public abstract void ExportXML(String uri, ExportMapping mapping)
            throws InputException;

    /**
     * Gets the repository's model.
     * @return the Model.
     */
    public abstract Model getModel();

    /**
     * Gets the repository's meta-model.
     * @return the MetaModel.
     */
    public abstract MetaModel getMetaModel();

    public abstract Diagrams getMetaModelDiagrams();
    
	/**
	 * @return
	 */
	public abstract MetaModelDiagramTypes getMetaModelDiagramTypes();


    /**
     * Gets the repository's diagrams.
     * @return the Diagrams.
     */
    public abstract Diagrams getDiagrams();

    /**
     * Gets the repository's diagram types.
     * @return the DiagramTypes.
     */
    public abstract DiagramTypes getDiagramTypes();

    /**
     * Gets the repository's import importMappings.
     * @return the ImportMappings.
     */
    public abstract ImportMappings getImportMappings();

    /**
     * Gets the repository's export mappings.
     * @return the ExportMappings.
     */
    public abstract ExportMappings getExportMappings();

    /**
     * Gets the repository's scripts.
     * @return the Scripts.
     */
    public abstract Scripts getScripts();

    /**
     * Gets the repository event map.
     * @return the event map.
     */
    public abstract EventMap getEventMap() throws Exception;

    /**
     * Updates the repository event map.
     * @param events is the new event map.
     */
    public abstract void setEventMap(EventMap events) throws Exception;

    /**
     * Gets the event map that should be used for any meta-model viewer.
     * Allows event handlers to be created in the dynamic meta-model viewer.
     * @return
     */
    public abstract EventMap getMetaModelViewerEvents() throws Exception;

    /**
     * Sets the events for the meta model viewer.  As the diagram type for meta model viewer isn't
     * persisted, the repository manages the events on its behalf.
     * @param metaModelViewerEvents are the events to use in the meta model. Typically this will
     * contain the reference to a script to display the meta model in a pleasing and eye catching fashion!
     * @throws Exception
     */
    public abstract void setMetaModelViewerEvents(EventMap metaModelViewerEvents) throws Exception;

    /**
     * Gets the event map that should be used for any model viewer.
     * Allows event handlers to be created in the dynamic model viewer.
     * @return
     */
    public abstract EventMap getModelViewerEvents() throws Exception;

    /**
     * Sets the events for the model viewer. As the diagram type for the model viewer isn't persisted,
     * the repository manages the events on its behalf.
     * @param modelViewerEvents is the value to set.
     * @throws Exception
     */
    public abstract void setModelViewerEvents(EventMap modelViewerEvents) throws Exception;

    /**
     * Get the repository properties.
     * @return the properties.
     */
    public abstract RepositoryProperties getProperties() throws Exception;

	/**
	 * Updates the repository properties.
	 * @param properties
	 */
	public abstract void updateProperties(RepositoryProperties properties) throws Exception;

    /**
     * Gets the types associated with this repository.
     * @return the MetaPropertyTypes.
     */
    public abstract MetaPropertyTypes getTypes() throws Exception;

    /**
     * Gets the extensible types in the repository.
     * @return the extensible types.
     */
    public abstract ExtensibleTypes getExtensibleTypes();

    /**
     * Gets the list of HTMLProxy pages in the repository.
     * @return the HTMLPages.
     */
    public abstract HTMLPages getPages();

    /**
     * Gets the collection of images in the repository.
     * @return Returns the images.
     */
    public abstract Images getImages();

    /**
     * Gets all the delete dependencies for a given Entity.
     * DeleteDependencies are all those items which, if the MetaRelationship
     * is deleted, will also have to be deleted to ensure the repository
     * maintains consistency.
     * @param e is the Entity that may be deleted.
     * @return a new DeleteDependenciesList with the delete dependencies.
     */
    public abstract DeleteDependenciesList getDeleteDependencies(Entity e) throws Exception;

    /**
     * Gets all the delete dependencies for a given Relationship.
     * DeleteDependencies are all those items which, if the Relationship
     * is deleted, will also have to be deleted to ensure the repository
     * maintains consistency.
     * @param r is the Relationship that may be deleted.
     * @return a new DeleteDependenciesList with the delete dependencies.
     */
    public abstract DeleteDependenciesList getDeleteDependencies(Relationship r) throws Exception;

    /**
     * Gets all the delete dependencies for a given MetaEntity.
     * DeleteDependencies are all those items which, if the MetaEntity
     * is deleted, will also have to be deleted to ensure the repository
     * maintains consistency.
     * @param me is the MetaEntity that may be deleted.
     * @return a new DeleteDependenciesList with the delete dependencies.
     */
    public abstract DeleteDependenciesList getDeleteDependencies(MetaEntity me) throws Exception;

    /**
     * Gets all the delete dependencies for a given MetaRelationship.
     * DeleteDependencies are all those items which, if the MetaRelationship
     * is deleted, will also have to be deleted to ensure the repository
     * maintains consistency.
     * @param mr is the MetaRelationship that may be deleted.
     * @return a new DeleteDependenciesList with the delete dependencies.
     */
    public abstract DeleteDependenciesList getDeleteDependencies(
            MetaRelationship mr) throws Exception;

    /**
     * Deletes all the contents of the repository.
     * @throws Exception 
     */
    public abstract void deleteContents() throws Exception;

    public abstract void index(LoadProgress progress) throws IOException;

    public abstract Set<Entity> searchForEntities(String query)
            throws RepositoryException;

    public abstract Set<Entity> searchForEntitiesOfType(String query,
            Set<MetaEntity> contents) throws RepositoryException;



}