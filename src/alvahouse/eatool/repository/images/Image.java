/*
 * Image.java
 * Project: EATool
 * Created on 30-Jun-2007
 *
 */
package alvahouse.eatool.repository.images;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.repository.dto.images.ImageDto;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.Base64InputStream;
import alvahouse.eatool.util.Base64OutputStream;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Image wraps a java.awt.Image in a repository friendly fashion to 
 * allow it to be browsed, loaded and saved in the repository.
 * 
 * @author rbp28668
 */
public class Image extends NamedRepositoryItem implements Versionable{

    private BufferedImage image;
    private String format = "png";
    private VersionImpl version = new VersionImpl();
        
    /**
     * Creates an empty Image.  Call importImage or readFrom to 
     * populate the image proper.
     * @param uuid is the key to identify this image.
     */
    public Image(UUID uuid) {
        super(uuid);
    }

    public Image(ImageDto dto) {
    	super(dto);
    	this.image = dto.getImage();
    	this.format = dto.getFormat();
    	this.version = new VersionImpl(dto.getVersion());
    }
    
    public ImageDto toDto(){
    	ImageDto dto = new ImageDto();
    	copyTo(dto);
    	return dto;
    }
    /**
     * Gets the underlying image.
     * @return the Image.
     */
    public java.awt.Image getImage(){
        return image;
    }
    
    /**
     * Allows the image to be set directly.
     * @param img
     */
    public void setImage(BufferedImage img) {
    	this.image = img;
    }
    
    /**
     * Imports the image from a binary InputStream.
     * @param input is the InputStream to import from.
     * @throws IOException
     */
    public void importImage(InputStream input) throws IOException{
        image = ImageIO.read(input);
    }
    
    /**
     * Reads the image data from a base64 encoded string.
     * @param base64 is a string containing image data, in a standard format
     * such as jpg or png, but base64 encoded.
     * @throws IOException
     */
    public void readDataFrom(String base64) throws IOException{
        InputStream stream = new Base64InputStream(base64);
        image = ImageIO.read(stream);
    }
     
    /**
     * Writes the image data as a base64 encoded string of whichever
     * image format is selected by the format property.
     * @return a base64 encoded string. 
     * @throws IOException
     */
    public String writeDataAsString() throws IOException{
        Base64OutputStream stream = new Base64OutputStream();
        ImageIO.write(image,format,stream);
        return stream.getData();
    }
    
    /**
     * Gets the allowable output formats.
     * @return array of String with the output formats.
     */
    public String[] getOutputFormats() {
        return ImageIO.getWriterFormatNames();
    }
    
    
    /**
     * @return Returns the format.
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format The format to set.
     */
    public void setFormat(String format) {
        this.format = format;
    }
    
    /**
     * Writes the Image out as XML.
     * @param out is the XMLWriter to write to.
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Image");
        super.writeAttributesXML(out);
        out.addAttribute("format",format);
        version.writeXML(out);
        out.text(writeDataAsString());
        out.stopEntity();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return getName() + ": " + image.getWidth() + " by " + image.getHeight(); 
    }

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.version.Versionable#getVersion()
	 */
	@Override
	public Version getVersion() {
		return version;
	}

	/**
	 * Copies the data from this object to a copy.
	 * @param copy
	 */
	protected void cloneTo(Image copy) {
		super.cloneTo(copy);
		copy.format = format;
		version.cloneTo(copy.version);
		copy.image = new BufferedImage(image.getWidth(), image.getHeight(),image.getType());
		image.copyData(copy.image.getRaster());
		
	}
	protected void copyTo(ImageDto dto) {
		super.copyTo(dto);
		dto.setFormat(format);
		dto.setImage(image);
		dto.setVersion(version.toDto());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Image copy = new Image(getKey());
		cloneTo(copy);
		return copy;
	}


}
