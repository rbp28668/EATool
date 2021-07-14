/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dto.RepositoryItemDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "imageDisplay")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "imageKeyJson"})
public class ImageDisplayDto extends RepositoryItemDto {

	private float x;
    private float y;
	private float width;
	private float height;
	private UUID imageKey;

	/**
	 * 
	 */
	public ImageDisplayDto() {
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
	 * @return the imageKey
	 */
	@JsonIgnore
	public UUID getImageKey() {
		return imageKey;
	}

	/**
	 * @param imageKey the imageKey to set
	 */
	public void setImageKey(UUID imageKey) {
		this.imageKey = imageKey;
	}
	
	/**
	 * @return the imageKey
	 */
	@JsonProperty("imageKey")
	@XmlElement(name="imageKey")
	public String getImageKeyJson() {
		return imageKey.asJsonId();
	}

	/**
	 * @param imageKey the imageKey to set
	 */
	public void setImageKeyJson(String imageKey) {
		this.imageKey = UUID.fromJsonId(imageKey);
	}
	

}
