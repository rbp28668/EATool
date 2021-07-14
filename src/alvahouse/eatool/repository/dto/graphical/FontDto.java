/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import java.awt.Font;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Dto object for representing an AWT Font object.
 * @author bruce_porteous
 */
@XmlRootElement(name = "font")
@XmlAccessorType(XmlAccessType.NONE)
public class FontDto {

	private String name;
	private int style;
	private int size;
		
	/**
	 * 
	 */
	public FontDto() {
	}
	
	public FontDto(Font font) {
		name = font.getName();
		style = font.getStyle();
		size = font.getSize();
	}
	
	public Font toFont() {
		Font font = new Font(name,style,size);
		return font;
	}

	/**
	 * @return the name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the style
	 */
	@XmlAttribute
	public int getStyle() {
		return style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(int style) {
		this.style = style;
	}

	/**
	 * @return the size
	 */
	@XmlAttribute
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	
	

}
