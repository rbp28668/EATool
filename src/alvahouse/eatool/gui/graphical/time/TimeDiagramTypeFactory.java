/*
 * TimeDiagramTypeFactory.java
 * Project: EATool
 * Created on 07-Dec-2006
 *
 */
package alvahouse.eatool.gui.graphical.time;

import java.awt.Color;
import java.util.Vector;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypeDetailFactory;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.util.UUID;

/**
 * TimeDiagramTypeFactory is a DiagramTypeDetailFactory to resolve 
 * the polymorphic elements of a TimeDiagramType.
 * 
 * @author rbp28668
 */
public class TimeDiagramTypeFactory extends FactoryBase implements
        DiagramTypeDetailFactory {

    private MetaModel metaModel;
    private TimeDiagramType diagramType;
    private TimeDiagramType.TypeEntry currentTarget = null;
    private Vector colours = null;
    
    
    /**
     * Creates an uninitialised TimeDiagramTypeFactory - call init(DiagramType, MetaModel)
     * to initialise.
     */
    public TimeDiagramTypeFactory() {
        super();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.DiagramTypeDetailFactory#init(alvahouse.eatool.gui.graphical.DiagramType, alvahouse.eatool.repository.metamodel.MetaModel)
     */
    public void init(DiagramType diagramType, MetaModel metaModel) {
        this.diagramType = (TimeDiagramType)diagramType;
        this.metaModel = metaModel;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String local, Attributes attrs)
            throws InputException {
        if(local.equals("TypeEntry")){
            if(currentTarget != null){
                throw new InputException("Nested TypeEntries in TimeDiagramType");
            }
            
            String entityKey = attrs.getValue("entity");
            if(entityKey == null){
                throw new InputException("Missing entity key attribute in TypeEntry");
            }
            
            String propertyKey = attrs.getValue("property");
            if(propertyKey == null){
                throw new InputException("Missing property key attribute in TypeEntry");
            }
            
            MetaEntity metaEntity = metaModel.getMetaEntity(new UUID(entityKey));
            if(metaEntity == null){
                throw new InputException("No Meta Entity corresponding to key " + entityKey);
            }
            
            MetaProperty metaProperty = metaEntity.getMetaProperty(new UUID(propertyKey));
            if(metaProperty == null){
                throw new InputException("No Meta Property corresponding to key " + propertyKey);
            }
            
            currentTarget = new TimeDiagramType.TypeEntry(metaEntity, metaProperty);
            colours = new Vector();
        } else if (local.equals("Colour")){
            Color colour = processColour(attrs);
            colours.add(colour);
        }
        

    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        if(local.equals("TypeEntry")){
            currentTarget.setColours(colours);
            colours = null;
            
            diagramType.addTarget(currentTarget);
            currentTarget = null;
        }

    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) throws InputException {
        // NOP.
    }

}
