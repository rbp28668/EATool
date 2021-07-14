/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import java.awt.Color;
import java.awt.Font;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dto.RepositoryItemDto;

/**
 * @author bruce_porteous
 *
 */
@XmlType(propOrder = {"textColourJson", "backColourJson", "borderColourJson", "fontJson"})
public abstract class TextualObjectDto extends RepositoryItemDto {

	private float x;
	private float y;
	private float width;
	private float height;
	private Color textColour;
	private Color backColour;
	private Color borderColour;
	private Font font;
	

	/**
	 * 
	 */
	public TextualObjectDto() {
	}

	/**
	 * @return the x
	 */
	@XmlAttribute
	public float getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	@XmlAttribute
	public float getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @return the width
	 */
	@XmlAttribute
	public float getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(float width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	@XmlAttribute
	public float getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(float height) {
		this.height = height;
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
