/**
 * 
 */
package alvahouse.eatool.util;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;

/**
 * Base64OutputStream provides an OutputStream which collects
 * the output and presents it as a base64 encoded string.
 * 
 * @author rbp28668
 */
public class Base64OutputStream extends OutputStream {

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