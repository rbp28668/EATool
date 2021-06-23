/**
 * 
 */
package alvahouse.eatool.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;

/**
 * Base64InputStream presents a base64 encoded string
 * as an InputStream. 
 * 
 * @author rbp28668
 */
public class Base64InputStream extends InputStream{
    private byte[] decoded;
    int pos = 0;
    
    public Base64InputStream(String base64){
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