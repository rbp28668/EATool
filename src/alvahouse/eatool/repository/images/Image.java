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
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
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

    /**
     * Gets the underlying image.
     * @return the Image.
     */
    public java.awt.Image getImage(){
        return image;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Image copy = new Image(getKey());
		cloneTo(copy);
		return copy;
	}

	/**
     * Base64InputStream presents a base64 encoded string
     * as an InputStream. 
     * 
     * @author rbp28668
     */
    private static class Base64InputStream extends InputStream{
        private byte[] decoded;
        int pos = 0;
        
        Base64InputStream(String base64){
            decoded = Base64.decodeBase64(base64.getBytes());
        }

        /* (non-Javadoc)
         * @see java.io.InputStream#read()
         */
        public int read() throws IOException {
            if(pos >= decoded.length){
                return -1;
            } 
            byte val = decoded[pos++];
            if(val < 0){
                return (int)val + 256;
            }
            return val;
        }
    }
    
    /**
     * Base64OutputStream provides an OutputStream which collects
     * the output and presents it as a base64 encoded string.
     * 
     * @author rbp28668
     */
    private static class Base64OutputStream extends OutputStream {

        // Note output chunking in 76 character blocks.
        static final int LIMIT = 76 * 128 * 3;  // Must be multiple of 3 to stop internal padding.
        private byte[] buff = new byte[LIMIT];
        private int pos = 0;
        private StringBuffer output = new StringBuffer();
        
        
        /* (non-Javadoc)
         * @see java.io.OutputStream#write(int)
         */
        public void write(int val) throws IOException {
           if(val < -128 || val >127){
                throw new IOException("Value out of range [-128..127]: " + val);
            }
            buff[pos++] = (byte)val;
            if(pos == LIMIT){
                output.append(new String(Base64.encodeBase64Chunked(buff)));
                pos = 0;
            }
        }
        
        /**
         * Gets the data written to the output stream in a base64 encoded
         * string.
         * @return
         */
        public String getData(){
            if(pos > 0) {
                byte[] data = new byte[pos];
                System.arraycopy(buff,0,data,0,pos);
                output.append(new String(Base64.encodeBase64Chunked(data)));
                pos = 0;
            }
            return output.toString();
        }
        
    }


}
