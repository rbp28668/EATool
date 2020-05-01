/*
 * StandardDiagramExportProxy.java
 * Project: EATool
 * Created on 14-May-2006
 *
 */
package alvahouse.eatool.webexport;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Iterator;

import alvahouse.eatool.gui.graphical.time.TimeBar;
import alvahouse.eatool.gui.graphical.time.TimeDiagram;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.util.XMLWriter;

/**
 * StandardDiagramExportProxy
 * 
 * @author rbp28668
 */
public class TimeDiagramExportProxy implements DiagramExportProxy {

    private TimeDiagram diagram;
    /**
     * 
     */
    public TimeDiagramExportProxy() {
        super();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.webexport.ExportProxy#export(alvahouse.eatool.util.XMLWriter)
     */
    public void export(XMLWriter out) throws IOException {
        if(diagram == null){
            throw new IllegalStateException("StandardDiagram not set in diagram export proxy");
        }
        
        out.startEntity("TimeDiagram");
        out.textEntity("Name", diagram.getName());
        out.textEntity("Description", diagram.getDescription());
        out.textEntity("UUID", diagram.getKey().toString());
        
        for(Iterator iter = diagram.getBars().iterator(); iter.hasNext();){
            TimeBar bar = (TimeBar)iter.next();
            
            KeyedItem connected = bar.getItem();
            
            if(connected instanceof Entity){
                Entity e = (Entity)connected;
                MetaEntity me = e.getMeta();
                if(me.isAbstract() || me.getDisplayHint() == null){
                    continue;
                }
                
                out.startEntity("EntitySymbol");
                out.textEntity("Name", e.toString());
                out.textEntity("UUID", e.getKey().toString());
                out.textEntity("TypeName", me.getName());
                out.textEntity("TypeUUID", me.getKey().toString());
                
                // Output symbols bounds in format used by a HTMLProxy image map.
                Rectangle2D.Float bounds = bar.getBounds();
                out.startEntity("Bounds");
                int x1 = Math.round(bounds.x);
                int y1 = Math.round(bounds.y);
                int x2 = Math.round(bounds.x + bounds.width);
                int y2 = Math.round(bounds.y + bounds.height);
                out.addAttribute("x1",x1);
                out.addAttribute("y1",y1);
                out.addAttribute("x2",x2);
                out.addAttribute("y2",y2);
                out.stopEntity(); // Bounds
                out.stopEntity(); // EntitySymbol;
            }
            
            
        }
        out.stopEntity();

    }

    /**
     * Sets the diagram to export.
     * @param diagram
     */
    public void setDiagram(Diagram diagram) {
        this.diagram = (TimeDiagram)diagram;
    }

}
