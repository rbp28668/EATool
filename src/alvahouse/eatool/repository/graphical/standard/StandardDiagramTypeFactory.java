/*
 * StandardDiagramTypeFactory.java
 * Project: EATool
 * Created on 02-Oct-2006
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypeDetailFactory;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.util.UUID;

/**
 * StandardDiagramTypeFactory de-serialises the parts of DiagramType particular
 * to StandardDiagramType.
 * 
 * @author rbp28668
 */
public class StandardDiagramTypeFactory extends FactoryBase implements DiagramTypeDetailFactory {

	private SymbolType currentSymbolType = null;
	private ConnectorType currentConnectorType = null;
	private StandardDiagramType currentDiagramType = null;
	/**  Track symbols in current diagram type to ensure that we only have
	 * connectors that connect symbol types already in the diagram type */
	private Set<UUID> symbolSet = new HashSet<>(); // of meta entities
	
	private MetaModel metaModel;

	// Translation map that translates old classnames to their current ones
	// when packages have changed.
	private static final Map<String,String> typeTranslation = new HashMap<>();
	static {
	    typeTranslation.put("alvahouse.eatool.gui.graphical.ConnectorArrow",ConnectorArrow.class.getName());
	    typeTranslation.put("alvahouse.eatool.gui.graphical.BasicConnector",BasicConnector.class.getName());
	}

    /**
     * Creates an un-initialised factory.
     */
    public StandardDiagramTypeFactory() {
        super();
    }
    
    /**
     * Initialises the factory.  Seperate method to allow re-initialisation.
     * @param metaModel
     */
    public void init(DiagramType diagramType, MetaModel metaModel) {
        currentDiagramType = (StandardDiagramType)diagramType;
        this.metaModel = metaModel;
        symbolSet.clear();
    	currentSymbolType = null;
    	currentConnectorType = null;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String local, Attributes attrs)
            throws InputException {
        if (local.equals("SymbolType")) {
			if(currentDiagramType == null) {
				throw new InputException("Symbol type found without enclosing diagram type");
			}
			if(currentSymbolType != null) {
				throw new InputException("Nested symbol type found while loading XML");
			}
			
			UUID uuid = getUUID(attrs);
			currentSymbolType = new SymbolType(uuid);
            
            String attr = attrs.getValue("represents");
            if(attr != null) {
                UUID uuidMeta = new UUID(attr);
                
                MetaEntity me = null;
                try {
                	me = metaModel.getMetaEntity(uuidMeta);
                } catch (Exception e) {
                	throw new InputException("Unable to get meta entity from repository");
                }
                if(me == null) {
                	throw new InputException("Unable to find meta entity for symbol type");
                }
                currentSymbolType.setRepresents(me);
            } else {
            	throw new InputException("Missing represents attribute on symbol type in XML");
            }
            
			attr = attrs.getValue("renderClass");
			if(attr != null) {
				Object renderer = null;
				Class<?> symbolClass = null;
				try {
					symbolClass = Class.forName(attr);
					renderer = symbolClass.newInstance();
				} catch (Exception ex) {
					throw new InputException("Can't find render class for symbol type: " + attr,ex);
				}
				
				if( !(renderer instanceof AbstractSymbol)) {
					throw new InputException("symbol class " + attr + " is not a valid symbol");
				}
				
				currentSymbolType.setRenderClass(symbolClass);
			} else {
				throw new InputException("Missing render class for symbol type");
			}
			
			/* Name is optional - use it if given, otherwise inherit the
			 * name of the meta-entity
			 */
			 attr = attrs.getValue("name");
			 if(attr != null) {
			 	currentSymbolType.setName(attr);
			 } 
		} else if (local.equals("DefaultTextColour")){
			currentSymbolType.setTextColour(processColour(attrs));
		} else if (local.equals("DefaultBackColour")){
			currentSymbolType.setBackColour(processColour(attrs));
		} else if (local.equals("DefaultBorderColour")){
			currentSymbolType.setBorderColour(processColour(attrs));
		} else if (local.equals("DefaultFont")){
			currentSymbolType.setFont(processFont(attrs));
		} else if (local.equals("ConnectorType")) {
			if(currentDiagramType == null) {
				throw new InputException("Connector type found without enclosing diagram type");
			}
			if(currentConnectorType != null) {
				throw new InputException("Nested connector type found while loading XML");
			}
			
			UUID uuid = getUUID(attrs);
			currentConnectorType = new ConnectorType(uuid);
            
            String attr = attrs.getValue("represents");
            if(attr != null) {
                UUID uuidMeta = new UUID(attr);
                
                MetaRelationship mr = null;
                try{
                	mr = metaModel.getMetaRelationship(uuidMeta);
                } catch (Exception e) {
                	throw new InputException("Unable to get meta relationship from repository");
                }
                
                if(mr == null) {
                	throw new InputException("Unable to find meta relationship for connector type");
                }
                
                /* check that the relationship this connector type represents
                 * actually connects symbol types that are allowed in this
                 * diagram (not much point otherwise!)
                 */
                if(! (symbolSet.contains(mr.start().connectionKey())) 
                && symbolSet.contains(mr.finish().connectionKey())) {
                	throw new InputException("Creating connector that connects to entity types for which there is no symbol type defined");
                }
                
                currentConnectorType.setRepresents(mr);
            } else {
            	throw new InputException("Missing represents attribute on connector type in XML");
            }
            
			attr = attrs.getValue("renderClass");
			if(attr != null) {
				Object renderer = null;
				Class connectorClass = null;
				try {
				    attr = translate(attr);
					connectorClass = Class.forName(attr);
					renderer = connectorClass.newInstance();
				} catch (Exception ex) {
					throw new InputException("Invalid render class for connector type: " + attr,ex);
				}
				
				if( !(renderer instanceof Connector)) {
					throw new InputException("connector class " + attr + " is not a valid connector");
				}
				
				currentConnectorType.setRenderClass(connectorClass);
			} else {
				throw new InputException("Missing render class for connector type");
			}
			
			/* Name is optional - use it if given, otherwise inherit the
			 * name of the meta-entity
			 */
			 attr = attrs.getValue("name");
			 if(attr != null) {
			 	currentConnectorType.setName(attr);
			 } 
		}

    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        if (local.equals("SymbolType")) {
			currentDiagramType.add(currentSymbolType);
			symbolSet.add(currentSymbolType.getRepresents().getKey());
			currentSymbolType = null;
		} else if (local.equals("ConnectorType")) {
			currentDiagramType.add(currentConnectorType);
			currentConnectorType = null;
		}
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) throws InputException {
    }

    /**
     * Possibly translate a type name to allow for refactoring of render classes.
     * @param attr is the type name (maybe old).
     * @return a translated (if need be) type name.
     */
    private String translate(String attr) {
        String translated = (String)typeTranslation.get(attr);
        if(translated != null){
            attr = translated;
        }
        return attr;
    }

}
