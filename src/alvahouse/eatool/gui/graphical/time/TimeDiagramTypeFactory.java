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
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.util.UUID;

/**
 * TimeDiagramTypeFactory is a DiagramTypeDetailFactory to resolve 
 * the polymorphic elements of a TimeDiagramType.
 * 
 * @author rbp28668
 */
public class TimeDiagramTypeFactory extends FactoryBase implements
        DiagramTypeDetailFactory {

    private TimeDiagramType diagramType;
    private TimeDiagramType.TypeEntry currentTarget = null;
    private Vector<Color> colours = null;
    
    
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
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String local, Attributes attrs)
            throws InputException {
        if(local.equals("TypeEntry")){
        	try {
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
	            
	            UUID metaEntityKey = new UUID(entityKey);
	            UUID metaPropertyKey = new UUID(propertyKey);
	            currentTarget = new TimeDiagramType.TypeEntry(metaEntityKey, metaPropertyKey);
	            colours = new Vector<Color>();
        	} catch (Exception e) {
        		throw new InputException("Unable to load TypeEntry", e);
        	}
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
            try {
            	diagramType.addTarget(currentTarget);
            } catch (Exception e) {
            	throw new InputException("Unable to save time diagram TypeEntry",e);
            }
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
