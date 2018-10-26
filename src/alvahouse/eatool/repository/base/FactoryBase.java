/*
 * FactoryBase.java
 *
 * Created on 19 January 2002, 16:08
 */

package alvahouse.eatool.repository.base;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * The base class for the various XML object factories.  This 
 * provides a number of utility classes for getting data from
 * the XML events.
 * @author  rbp28668
 */
public class FactoryBase  {

    /** Creates new FactoryBase */
    public FactoryBase() {
    }
    
    /**
     * Gets a UUID attribute of the current entity.
	 * @param attrs is the entities attribute set.
	 * @return a UUID
	 * @throws IllegalArgumentException - if no uuid attribute there.
	 */
	protected UUID getUUID(Attributes attrs){
        return getUUID(attrs,"uuid");
    }
	
	/**
	 * Get a UUID from a given attribute.
     * @param attrs is the attributes set.
     * @param name is the name of the attribute holding the UUID.
     * @return the corresponding UUID.
     */
    protected UUID getUUID(Attributes attrs, String name) {
        String struuid = attrs.getValue(name);
        if(struuid == null)
            throw new IllegalArgumentException("Missing UUID attribute " + name + " in XML");
        UUID uuid = new UUID(struuid);
        return uuid;
    }


	/**
	 * Gets an int from a set of attributes.
	 * @param attrs is the set of attributes.
	 * @param name is the name of the attribute containing the int.
	 * @return the given int.
	 * @throws InputException if the attribute is missing or can't be converted.
	 */
	protected int getInt(Attributes attrs, String name) throws InputException{
	    String asText = attrs.getValue(name);
	    if(asText == null){
	        throw new InputException("Missing int value " + name);
	    }
	    int value = 0;
	    try {
            value = Integer.parseInt(asText);
        } catch (NumberFormatException e) {
            throw new InputException("Unable to convert value " + asText + " for " + name + " to int");
        }
	    return value;
	}
	/**
	 * Gets a float from a set of attributes.
	 * @param attrs is the set of attributes.
	 * @param name is the name of the attribute containing the float.
	 * @return the given float.
	 * @throws InputException if the attribute is missing or can't be converted.
	 */
	protected float getFloat(Attributes attrs, String name) throws InputException{
	    String asText = attrs.getValue(name);
	    if(asText == null){
	        throw new InputException("Missing float value " + name);
	    }
	    float value = 0.0f;
	    try {
            value = Float.parseFloat(asText);
        } catch (NumberFormatException e) {
            throw new InputException("Unable to convert value " + asText + " for " + name + " to float");
        }
	    return value;
	}
	
	/**
	 * Processes a colour entity.
	 * @param attrs is the entities attribute set.
	 * @return a Color corresponding to the values of red,green,blue and alpha in the attributes.
	 * @throws InputException if any of the component parts won't parse as an integer.
	 */
	protected Color processColour(Attributes attrs) throws InputException {
		Color colour = null;
		try{
			int red = Integer.parseInt(attrs.getValue("red"));
			int green = Integer.parseInt(attrs.getValue("green"));
			int blue = Integer.parseInt(attrs.getValue("blue"));
			int alpha = Integer.parseInt(attrs.getValue("alpha"));
			colour = new Color(red,green,blue,alpha);
		} catch (NumberFormatException nfe){
			throw new InputException("Invalid colour ",nfe);
		}
		return colour;
	}
	
	/**
     * Gets a font entity
	 * @param attrs is the entities attribute set.
	 * @return a font corresponding to the font name, style and size.
	 * @throws InputException
	 */
	protected Font processFont(Attributes attrs) throws InputException {
		Font font = null;
		try {
			String name = attrs.getValue("name");
			int style = Integer.parseInt(attrs.getValue("style"));
			int size = Integer.parseInt(attrs.getValue("size"));
			font = new Font(name,style,size);
		} catch (NumberFormatException nfe){
			throw new InputException("Invalid font in input ",nfe);
		}
		return font;
	}

	/**
	 * Helper method for serializing a colour to XML.
	 * @param out is the XMLWriter to write to.
	 * @param colourName is the name of the entity to hold the colour.
	 * @param colour is the colour to write.
	 * @throws IOException
	 */
	public static void writeColour(XMLWriter out, String colourName, Color colour) throws IOException{
		out.startEntity(colourName);
		out.addAttribute("red", colour.getRed());
		out.addAttribute("green", colour.getGreen());
		out.addAttribute("blue", colour.getBlue());
		out.addAttribute("alpha", colour.getAlpha());
		out.stopEntity();
	}

	/**
	 * Helper method for serializing a font to XML.
	 * @param out is the XMLWriter to write to.
	 * @param fontName is the name of the entity to hold the font.
	 * @param font is the font to write.
	 * @throws IOException
	 */
	public static void writeFont(XMLWriter out, String fontName, Font font) throws IOException{
		out.startEntity(fontName);
		out.addAttribute("name", font.getName());
		out.addAttribute("style", font.getStyle());
		out.addAttribute("size", font.getSize());
		out.stopEntity();
	}
	

}
