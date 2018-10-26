/*
 * Repository.java
 * Project: EATool
 * Created on 9 Dec 2007
 *
 */
package alvahouse.eatool.scripting.proxy;

import alvahouse.eatool.gui.graphical.standard.model.GraphicalModel;
import alvahouse.eatool.gui.graphical.standard.model.ModelDiagramType;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.repository.mapping.ExportMapping;
import alvahouse.eatool.repository.mapping.ExportMappings;
import alvahouse.eatool.repository.mapping.ImportMapping;
import alvahouse.eatool.repository.mapping.ImportMappings;
import alvahouse.eatool.util.UUID;

public class RepositoryProxy {

    private Repository repository;

    /**
     * @param repository
     */
    public RepositoryProxy(Repository repository){
        this.repository = repository;
    }
    
    /**
     * Gets the current meta-model.
     * @return the current meta model.
     */
    public MetaModelProxy metaModel(){
        return new MetaModelProxy(repository.getMetaModel());
    }
    
    /**
     * Gets the current model.
     * @return the current model.
     */
    public ModelProxy model(){
        return new ModelProxy(repository);
    }
    
    /**
     * Gets the diagrams of the current model.
     * @return the diagrams.
     */
    public DiagramsProxy diagrams(){
        return new DiagramsProxy(repository.getDiagrams(), repository);
    }
    
    /**
     * Gets the diagram containing the complete model.
     * @return the whole model in one diagram.
     */
    public StandardDiagramProxy getModelDiagram(){
        StandardDiagramType diagramType = new ModelDiagramType(repository.getMetaModel());
        GraphicalModel gm = new GraphicalModel(repository.getModel(),diagramType,new UUID());
        return new StandardDiagramProxy(gm,repository);
    }
    
    /**
     * Imports data into the model.
     * @param importName is the name of the import mapping.
     * @param path is the file where data should be read from.
     */
    public void importData(String importName, String path){
        ImportMappings mappings = repository.getImportMappings();
        for(ImportMapping mapping : mappings.getImportMappings()){
            if(mapping.getName().equals(importName)){
                repository.ImportXML(path,mapping);
                break;
            }
            
        }
    }
    
    /**
     * Exports data from the model.
     * @param exportName is the name of the export mapping to use.
     * @param path is where the data should be written to.
     */
    public void exportData(String exportName, String path){
        ExportMappings mappings = repository.getExportMappings();
        for(ExportMapping mapping : mappings.getExportMappings()){
             if(mapping.getName().equals(exportName)){
                repository.ExportXML(path,mapping);
                break;
            }
        }
    }

}
