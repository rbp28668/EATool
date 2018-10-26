/*
 * DiagramExportProxy.java
 * Project: EATool
 * Created on 17-Mar-2007
 *
 */
package alvahouse.eatool.webexport;

import alvahouse.eatool.repository.graphical.Diagram;

/**
 * DiagramExportProxy
 * 
 * @author rbp28668
 */
public interface DiagramExportProxy extends ExportProxy {
    
    /**
     * Sets the diagram to export.
     * @param diagram
     */
    public void setDiagram(Diagram diagram);
}
