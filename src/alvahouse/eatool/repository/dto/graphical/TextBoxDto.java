/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "textBox")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "text","url"})
public class TextBoxDto extends TextualObjectDto {

    private String text = "";
    private String url = "";
	/**
	 * 
	 */
	public TextBoxDto() {
	}
	/**
	 * @return the text
	 */
	@XmlElement
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return the url
	 */
	@XmlElement
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	

}
