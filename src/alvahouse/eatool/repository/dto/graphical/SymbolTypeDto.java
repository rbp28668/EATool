/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import java.awt.Color;
import java.awt.Font;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dto.NamedRepositoryItemDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "symbolType")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"symbolClass", "metaEntityKeyJson", "backColourJson", "textColourJson", "borderColourJson", "fontJson"})

public class SymbolTypeDto extends NamedRepositoryItemDto {

	/** The class used to display the symbol (a subclass of BasicSymbol) */
	private String symbolClass;
	
	/** Which MetaEntity this symbol type represents */
	private UUID metaEntityKey;

	/** Default background colour for symbols of this type */
	private Color backColour;

	/** Default text colour for symbols of this type */
	private Color textColour;

	/** Default border colour for symbols of this type */
	private Color borderColour;

	/** Default font for symbols of this type */
	private Font font;

	/**
	 * 
	 */
	public SymbolTypeDto() {
		// TODO Auto-generated constructor stub
	}

	
	
	/**
	 * @return the symbolClass
	 */
	@XmlElement
	public String getSymbolClass() {
		return symbolClass;
	}



	/**
	 * @param symbolClass the symbolClass to set
	 */
	public void setSymbolClass(String symbolClass) {
		this.symbolClass = symbolClass;
	}



	/**
	 * @return the metaEntityKey
	 */
	@JsonIgnore
	public UUID getMetaEntityKey() {
		return metaEntityKey;
	}



	/**
	 * @param metaEntityKey the metaEntityKey to set
	 */
	public void setMetaEntityKey(UUID metaEntityKey) {
		this.metaEntityKey = metaEntityKey;
	}

	/**
	 * @return the metaEntityKey
	 */
	@JsonProperty("metaEntityKey")
	@XmlElement(name="metaEntityKey")
	public String getMetaEntityKeyJson() {
		return metaEntityKey.asJsonId();
	}



	/**
	 * @param metaEntityKey the metaEntityKey to set
	 */
	public void setMetaEntityKeyJson(String metaEntityKey) {
		this.metaEntityKey = UUID.fromJsonId(metaEntityKey);
	}


	/**
	 * @return the textColour
	 */
	@JsonIgnore
	public Color getTextColour() {
		return textColour;
	}

	/**
	 * @param textColour the textColour to set
	 */
	public void setTextColour(Color textColour) {
		this.textColour = textColour;
	}

	/**
	 * @return the textColour
	 */
	@JsonProperty("textColour")
	@XmlElement(name="textColour")
	public ColourDto getTextColourJson() {
		return new ColourDto(textColour);
	}

	/**
	 * @param textColour the textColour to set
	 */
	public void setTextColourJson(ColourDto textColour) {
		this.textColour = textColour.toColor();
	}

	/**
	 * @return the backColour
	 */
	@JsonIgnore
	public Color getBackColour() {
		return backColour;
	}

	/**
	 * @param backColour the backColour to set
	 */
	public void setBackColour(Color backColour) {
		this.backColour = backColour;
	}

	/**
	 * @return the backColour
	 */
	@XmlElement(name="backColour")
	@JsonProperty("backColour")
	public ColourDto getBackColourJson() {
		return new ColourDto(backColour);
	}

	/**
	 * @param backColour the backColour to set
	 */
	public void setBackColourJson(ColourDto backColour) {
		this.backColour = backColour.toColor();
	}

	/**
	 * @return the borderColour
	 */
	@JsonIgnore
	public Color getBorderColour() {
		return borderColour;
	}

	/**
	 * @param borderColour the borderColour to set
	 */
	public void setBorderColour(Color borderColour) {
		this.borderColour = borderColour;
	}

	/**
	 * @return the borderColour
	 */
	@XmlElement(name="borderColour")
	@JsonProperty("borderColour")
	public ColourDto getBorderColourJson() {
		return new ColourDto(borderColour);
	}

	/**
	 * @param borderColour the borderColour to set
	 */
	public void setBorderColourJson(ColourDto borderColour) {
		this.borderColour = borderColour.toColor();
	}

	/**
	 * @return the font
	 */
	@JsonIgnore
	public Font getFont() {
		return font;
	}

	/**
	 * @param font the font to set
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * @return the font
	 */
	@XmlElement(name="font")
	@JsonProperty("font")
	public FontDto getFontJson() {
		return new FontDto(font);
	}

	/**
	 * @param font the font to set
	 */
	public void setFontJson(FontDto font) {
		this.font = font.toFont();
	}

}
