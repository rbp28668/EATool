/**
 * 
 */
package alvahouse.eatool.repository.dto.images;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.dto.NamedRepositoryItemDto;
import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.util.Base64InputStream;
import alvahouse.eatool.util.Base64OutputStream;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "image")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "format", "imageJson", "version"})

public class ImageDto extends NamedRepositoryItemDto {

    private BufferedImage image;
    private String format;
    private VersionDto version;
	/**
	 * @return the image
	 */
    @JsonIgnore
	public BufferedImage getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	/**
	 * @return the image data as base64 encoded for serialisation
	 * @throws IOException 
	 */
	@XmlElement(name="image",required=true)
	@JsonProperty(value="image")
	public String getImageJson() throws IOException {
        Base64OutputStream stream = new Base64OutputStream();
        ImageIO.write(image,"PNG",stream);
        return stream.getData();
	}
	/**
	 * @param base64 has a base64 representation of the image to set
	 * @throws IOException 
	 */
	public void setImageJson(String base64) throws IOException {
        InputStream stream = new Base64InputStream(base64);
        this.image = ImageIO.read(stream);
	}

	/**
	 * @return the format
	 */
	@XmlElement(required=true)
	public String getFormat() {
		return format;
	}
	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	/**
	 * @return the version
	 */
	@XmlElement(required=true)
	public VersionDto getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(VersionDto version) {
		this.version = version;
	}
    
    

}
