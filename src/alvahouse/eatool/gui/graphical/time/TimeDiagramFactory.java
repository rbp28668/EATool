/*
 * TimeDiagramFactory.java
 * Project: EATool
 * Created on 07-Dec-2006
 *
 */
package alvahouse.eatool.gui.graphical.time;

import org.xml.sax.Attributes;


import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramDetailFactory;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.images.Images;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.util.UUID;

/**
 * TimeDiagramFactory is a factory for de-serialising a TimeDiagram.
 * 
 * @author rbp28668
 */
public class TimeDiagramFactory extends FactoryBase implements
        DiagramDetailFactory {

    private TimeDiagram diagram;
    private DiagramType type;
    private MetaModel metaModel;
    private Model model;
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.DiagramDetailFactory#init(alvahouse.eatool.gui.graphical.Diagram, alvahouse.eatool.gui.graphical.DiagramType, alvahouse.eatool.repository.metamodel.MetaModel, alvahouse.eatool.repository.model.Model)
     */
    public void init(Diagram diagram, DiagramType type, MetaModel metaModel,
            Model model, Images images) {
        this.diagram = (TimeDiagram)diagram;
        this.type = type;
        this.metaModel = metaModel;
        this.model = model;

    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String local, Attributes attrs)
            throws InputException {
        if(local.equals("Property")){
            String entityKey = attrs.getValue("entity");
            if(entityKey == null){
                throw new InputException("Missing entity key in TimeDiagram Property");
            }
            String propertyKey = attrs.getValue("uuidMeta");
            if(propertyKey == null){
                throw new InputException("Missing property key in TimeDiagram Property");
            }
            
            Entity e;
			try {
				e = model.getEntity(new UUID(entityKey));
			} catch (Exception ex) {
				throw new InputException("Unable to fetch entity");
			}
			
            if(e == null){
                throw new InputException("No entity with key " + entityKey);
            }
            
            Property p = e.getPropertyByMeta(new UUID(propertyKey));
            if(p == null){
                throw new InputException("No property with meta property key " + propertyKey);
            }
            
            diagram.addProperty(p);
            
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        // NOP
     }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) throws InputException {
        // NOP
    }

}
