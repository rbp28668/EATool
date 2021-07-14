/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO object for representing an AWT Color object.
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "colour")
@XmlAccessorType(XmlAccessType.NONE)
public class ColourDto {

	private int red;
	private int green;
	private int blue;
	private int alpha;
	
	/**
	 * 
	 */
	public ColourDto() {
	}
	
	public ColourDto(Color colour) {
		red = colour.getRed();
		green = colour.getGreen();
		blue = colour.getBlue();
		alpha = colour.getAlpha();
	}

	public Color toColor() {
		Color colour = new Color(red, green, blue, alpha);
		return colour;
	}

	/**
	 * @return the red
	 */
	@XmlAttribute
	public int getRed() {
		return red;
	}

	/**
	 * @param red the red to set
	 */
	public void setRed(int red) {
		this.red = red;
	}

	/**
	 * @return the green
	 */
	@XmlAttribute
	public int getGreen() {
		return green;
	}

	/**
	 * @param green the green to set
	 */
	public void setGreen(int green) {
		this.green = green;
	}

	/**
	 * @return the blue
	 */
	@XmlAttribute
	public int getBlue() {
		return blue;
	}

	/**
	 * @param blue the blue to set
	 */
	public void setBlue(int blue) {
		this.blue = blue;
	}

	/**
	 * @return the alpha
	 */
	@XmlAttribute
	public int getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	
	
}
