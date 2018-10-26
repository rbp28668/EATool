/*
 * TextObjectSettings.java
 * Created on 29-May-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.awt.Color;
import java.awt.Font;

/**
 * TextObjectSettings describes the generic settings for a symbol.
 * @author Bruce.Porteous
 *
 */
public interface TextObjectSettings {
	
	/**
	 * Gets the background colour of the symbol.
	 * @return the symbol's background colour.
	 */
	Color getBackColour();
	
	/**
	 * Gets the text colour of the symbol.
	 * @return the symbol's text colour.
	 */
	Color getTextColour();
	
	/**
	 * Gets the border colour of the symbol.
	 * @return the symbol's border colour.
	 */
	Color getBorderColour();
	
	/**
	 * Sets the background colour of the symbol.
	 * @param colour is the new background colour for the symbol.
	 */
	void setBackColour(Color colour);
	
	/**
	 * Sets the text colour of the symbol.
	 * @param colour is the new text colour for the symbol.
	 */
	void setTextColour(Color colour);
	
	/**
	 * Sets the border colour of the symbol.
	 * @param colour is the new border colour for the symbol.
	 */
	void setBorderColour(Color colour);
	
	/**
	 * Gets the display font for the symbol.
	 * @return the symbol's current font.
	 */
	Font getFont();
	
	/**
	 * Sets the symbol's display font.
	 * @param font is the symbol's new display font.
	 */
	void setFont(Font font);

}
