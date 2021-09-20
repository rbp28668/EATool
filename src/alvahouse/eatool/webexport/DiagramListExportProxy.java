/*
 * DiagramListExportProxy.java
 * Project: EATool
 * Created on 14-May-2006
 *
 */
package alvahouse.eatool.webexport;

import java.io.IOException;
import java.util.Collection;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypeFamily;
import alvahouse.eatool.repository.graphical.DiagramTypes;
import alvahouse.eatool.util.XMLWriter;

/**
 * DiagramListExportProxy
 * 
 * @author rbp28668
 */
public class DiagramListExportProxy implements ExportProxy {

    private Repository repository;
    /**
     * 
     */
    public DiagramListExportProxy(Repository repository) {
        super();
        this.repository = repository;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.webexport.ExportProxy#export(alvahouse.eatool.util.XMLWriter)
     */
    public void export(XMLWriter out) throws IOException {
        out.startEntity("DiagramTypes");
        
        try {
			DiagramTypes types = repository.getDiagramTypes();
			for(DiagramTypeFamily family : types.getDiagramTypeFamilies()){
			    for(DiagramType type : family.getDiagramTypes()){
			        exportDiagramsOfType(out, type);
			    }
			}
		} catch (Exception e) {
			throw new IOException("Unable to export diagram types",e);
		}
        
        out.stopEntity();
    }

    /**
     * @param out
     * @param type
     * @throws Exception
     */
    private void exportDiagramsOfType(XMLWriter out, DiagramType type) throws Exception {
        Collection<Diagram> diagrams = repository.getDiagrams().getDiagramsOfType(type);
        if(diagrams.size() > 0){
            out.startEntity("DiagramType");
            out.textEntity("Name", type.getName());
            out.textEntity("UUID", type.getKey().toString());
            
            out.startEntity("Diagrams");
            for(Diagram diagram : diagrams){
                
                out.startEntity("Diagram");
                out.textEntity("Name", diagram.getName());
                out.textEntity("UUID", diagram.getKey().toString());
                out.textEntity("Description", diagram.getDescription());
                out.stopEntity(); // Diagram
            }
            out.stopEntity(); // Diagrams
            
            out.stopEntity(); // DiagramType
        }
    }

}
