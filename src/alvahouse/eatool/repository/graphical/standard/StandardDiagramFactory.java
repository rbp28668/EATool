/*
 * DiagramFactory.java
 * Created on 01-May-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.base.RepositoryItem;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.exception.LogicException;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramDetailFactory;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.images.Image;
import alvahouse.eatool.repository.images.Images;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.util.UUID;

/**
 * StandardDiagramFactory is a detail handler for deserialising StandardDiagrams.
 * @author Bruce.Porteous
 *
 */
public class StandardDiagramFactory extends FactoryBase implements DiagramDetailFactory {

    private MetaModel metaModel;
    private Model model;
    private Images images;
    
    private StandardDiagram currentDiagram = null;
    private StandardDiagramType currentDiagramType = null;
    
    private Symbol currentSymbol = null;
    private TextBox currentTextBox = null;
    private ImageDisplay currentImage = null;
    
    private TextObjectSettings currentTextObject = null;
    private Connector currentConnector = null;
    private ConnectorStrategy currentConnectorStrategy = null;
    private Symbol currentStart = null;
    private Symbol currentFinish = null;
    
    private StringBuffer text = new StringBuffer();
    
	// Translation map that translates old classnames to their current ones
	// when packages have changed.
	private static final Map typeTranslation = new HashMap();
	static {
	    typeTranslation.put("alvahouse.eatool.gui.graphical.StraightConnectorStrategy",StraightConnectorStrategy.class.getName());
	    typeTranslation.put("alvahouse.eatool.gui.graphical.QuadraticConnectorStrategy",QuadraticConnectorStrategy.class.getName());
	    typeTranslation.put("alvahouse.eatool.gui.graphical.CubicConnectorStrategy",CubicConnectorStrategy.class.getName());
	}
    
	/**
	 * Create a DiagramFactory for a given diagrams collection and meta model.
	 */
	public StandardDiagramFactory() {
		super();
	}

	/**
	 * Create a DiagramFactory for a given diagrams collection and meta model.
	 * @param diagrams will have any de-serialized diagrams added to it.
	 * @param diagramTypes provides the types of any diagram.
	 * @param metaModel is the meta-model that the digrams belong to.
	 */
	public void init(Diagram diagram, DiagramType type,  MetaModel metaModel, Model model, Images images ) {
		currentDiagram = (StandardDiagram)diagram;
		currentDiagramType = (StandardDiagramType)type;
		
		this.metaModel = metaModel;
		this.model = model;
		this.images = images;
	}
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String local, Attributes attrs)
		throws InputException {
			
		if( local.equals("Symbol")){
		    if(currentSymbol != null) {
		        throw new InputException("Nested Symbol in input");
		    }
		    
		    UUID key = getUUID(attrs);
		    
	        String typeid = attrs.getValue("type");
	        if(typeid == null)
	            throw new InputException("Missing type attribute in Symbol XML");
	        UUID typeUUID = new UUID(typeid);

	        SymbolType type = currentDiagramType.getSymbolType(typeUUID);
	        if(type == null){
	            throw new InputException("No SymbolType with ID " + typeUUID);
	        }
	        
			String objId = attrs.getValue("represents");
			if(objId == null){
			    throw new InputException("No represents attribute in Symbol XML");
			}
			UUID objUUID = new UUID(objId);
			KeyedItem target = model.getEntity(objUUID);
			if(target == null){
			    target = metaModel.getMetaEntity(objUUID);
			}
			if(target == null){
			    throw new InputException("Represents UUID does not correspond to a known Entity or MetaEntity");
			}
			
			
			float width = getFloat(attrs,"width");
	 		float height = getFloat(attrs,"height");
	 		float x = getFloat(attrs,"x");
	 		float y = getFloat(attrs,"y");

			try {
                currentSymbol = type.newSymbol(key, target, x, y, width, height );
                currentSymbol.mustSizeSymbol(false);
                currentTextObject = currentSymbol;
            } catch (LogicException e) {
                throw new InputException("Unable to create symbol " + e.getMessage());
            }

		} else if( local.equals("Connector")){
		    if(currentConnector != null){
		        throw new InputException("Nested Connectors");
		    }
		    
			UUID uuid = getUUID(attrs);
			
			UUID typeUUID = getUUID(attrs,"type");
			ConnectorType type = currentDiagramType.getConnectorType(typeUUID);
	        if(type == null){
	            throw new InputException("No ConnectorType with ID " + typeUUID);
	        }

			UUID uuidObj = getUUID(attrs,"represents");
			KeyedItem obj = model.getRelationship(uuidObj);
			if(obj == null){
			    metaModel.getMetaRelationship(uuidObj);
			}
			if(obj == null){
			    throw new InputException("Represents UUID does not correspond to a known Relationship or MetaRelationship");
			}

			try {
                currentConnector = type.newConnector(uuid);
            } catch (LogicException e) {
                throw new InputException("Unable to create connector " + e.getMessage());
            }
            
            currentConnector.setItem(obj);
            
            UUID uuidStart = getUUID(attrs,"start");
            UUID uuidFinish = getUUID(attrs,"finish");
            
            currentStart = currentDiagram.getSymbol(uuidStart);
            if(currentStart == null){
                throw new InputException("Start symbol on connector not found");
            }
            currentFinish = currentDiagram.getSymbol(uuidFinish);
            if(currentFinish == null){
                throw new InputException("Finish symbol on connector not found");
            }

            currentConnector.setEnds(currentStart, currentFinish);
            
		} else if( local.equals("ConnectorStrategy")){
		    
		    if(currentConnectorStrategy != null){
		        throw new InputException("Nested Connector Strategy");
		    }
		    
		    String className = attrs.getValue("class");
		    if(className == null){
		        throw new InputException("Missing class name on connector strategy");
		    }
		    
		    Class csClass = null;
		    className = translate(className);
		    try {
                csClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new InputException("Connector Strategy class " + className + " not found");
            }
            
            try {
                currentConnectorStrategy = (ConnectorStrategy)csClass.newInstance();
            } catch (Exception e) {
                throw new InputException("Unable to create ConnectorStrategy: " + e.getMessage());
            }
            
            // Initialise the end positions - don't want to reset any handle positions by doing it later.
            currentConnectorStrategy.setStart(currentStart.getX(),currentStart.getY());
            currentConnectorStrategy.setFinish(currentFinish.getX(),currentFinish.getY());

		} else if( local.equals("ConnectorColour")){
		    Color colour = processColour(attrs);
		    currentConnectorStrategy.setColour(colour);
		    
		} else if( local.equals("Handle")){
		    int idx = getInt(attrs,"index");
		    float x = getFloat(attrs,"x");
		    float y = getFloat(attrs,"y");
		    
		    currentConnectorStrategy.setHandle(idx,x,y);
            
		} else if( local.equals("TextColour")){
		    Color colour = processColour(attrs);
		    currentTextObject.setTextColour(colour);
		} else if( local.equals("BackColour")){
		    Color colour = processColour(attrs);
		    currentTextObject.setBackColour(colour);
		} else if( local.equals("BorderColour")){
		    Color colour = processColour(attrs);
		    currentTextObject.setBorderColour(colour);
		} else if( local.equals("Font")) {
		    Font font = processFont(attrs);
		    currentTextObject.setFont(font);
		} else if( local.equals("TextBox")){
		    UUID key = getUUID(attrs);
		    currentTextBox = new TextBox(key);
		    currentTextObject = currentTextBox;
		    
			float width = getFloat(attrs,"width");
	 		float height = getFloat(attrs,"height");
	 		float x = getFloat(attrs,"x");
	 		float y = getFloat(attrs,"y");

	 		currentTextBox.setPosition(x,y);
	 		currentTextBox.setSize(width,height);
		} else if( local.equals("Text")){
			if(currentTextBox == null){
				throw new InputException("Text element outside TextBox in input");
			}
			text.delete(0,text.length());
		} else if( local.equals("URL")){
			if(currentTextBox == null){
				throw new InputException("URL element outside TextBox in input");
			}
			text.delete(0,text.length());
		} else if( local.equals("ImageDisplay")){
		    if(currentImage != null){
		        throw new InputException("Nested ImageDisplay in input");
		    }
		    UUID key = getUUID(attrs);
		    currentImage = new ImageDisplay(key);
		    
			float width = getFloat(attrs,"width");
	 		float height = getFloat(attrs,"height");
	 		float x = getFloat(attrs,"x");
	 		float y = getFloat(attrs,"y");

	 		currentImage.setPosition(x,y);
	 		currentImage.setSize(width,height);
	 		
	 		UUID imageKey = getUUID(attrs,"displays");
	 		Image image = images.lookupImage(imageKey);
	 		if(image == null){
	 		   throw new InputException("Key " + imageKey.toString() + " does not match any images");
	 		}
	 		currentImage.setImage(image);
		}

			

	}


    /* (non-Javadoc)
	 * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String local) throws InputException {
		if(local.equals("StandardDiagram")) {
//			diagrams.add(currentDiagram);
//			currentDiagram = null;
//			eventMapFactory.setEventMap(savedEventMap);
//			savedEventMap = null;
		} else if( local.equals("Symbol")){
		    currentDiagram.addSymbol(currentSymbol);
		    currentSymbol = null;
		    currentTextObject = null;
		} else if( local.equals("Connector")){
            currentStart.addArc(currentConnector);
            currentFinish.addArc(currentConnector);
		    currentDiagram.addConnector(currentConnector);
		    
		    currentConnector = null;
		    currentStart = null;
		    currentFinish = null;
		} else if( local.equals("ConnectorStrategy")){
		    currentConnector.setStrategy(currentConnectorStrategy);
		    currentConnectorStrategy = null;
		} else if( local.equals("TextBox")){
		    currentDiagram.addTextBox(currentTextBox);
		    currentTextBox = null;
		    currentTextObject = null;
		} else if( local.equals("Text")){
			currentTextBox.setText(text.toString());
		} else if( local.equals("URL")){
		    currentTextBox.setUrl(text.toString());
		} else if( local.equals("ImageDisplay")){
		    currentDiagram.addImage(currentImage);
		    currentImage = null;
		}
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
	 */
	public void characters(String str) throws InputException {
		text.append(str);
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
