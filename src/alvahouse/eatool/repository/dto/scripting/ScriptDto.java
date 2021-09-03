/**
 * 
 */
package alvahouse.eatool.repository.dto.scripting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import alvahouse.eatool.repository.dto.NamedRepositoryItemDto;
import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.util.Base64InputStream;
import alvahouse.eatool.util.Base64OutputStream;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "script")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "language", "scriptJson", "version"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type_name")
public class ScriptDto extends NamedRepositoryItemDto {

    private String language;
    private String script;	// the actual script.
    private VersionDto version;
	private String rev;
	
	/**
	 * @return the language
	 */
    @XmlElement(required=true)
	public String getLanguage() {
		return language;
	}
	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	/**
	 * @return the script
	 */
	@JsonIgnore
	public String getScript() {
		return script;
	}
	/**
	 * @param script the script to set
	 */
	public void setScript(String script) {
		this.script = script;
	}

	/**
	 * @return the script as base64 encoded data.
	 * @throws IOException 
	 */
    @XmlElement(name="script",required=true)
	@JsonProperty(value="script")
	public String getScriptJson() throws IOException {
    	if(script == null) {
    		return null;
    	}
    	String data = null;
        try(Base64OutputStream stream = new Base64OutputStream()){
	        byte[] bytes = script.getBytes("UTF-8");
	        stream.write(bytes);
	        data = stream.getData();
        }
        return data;
	}
	/**
	 * @param script the script to set
	 * @throws UnsupportedEncodingException 
	 */
	public void setScriptJson(String base64) throws IOException {
	   StringBuilder script = new StringBuilder();
        try(InputStream stream = new Base64InputStream(base64)){
	        try(Reader reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"))){
	        	int ch = 0;
	        	while((ch = reader.read()) != -1) {
	        		script.append((char)ch);
	        	}
	        }
        }
		this.script = script.toString();
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
    
	/**
	 * revision information for CouchDB
	 * @return the rev
	 */
	@JsonProperty("_rev")
	public String getRev() {
		return rev;
	}

	/**
	 * @param rev the rev to set
	 */
	public void setRev(String rev) {
		this.rev = rev;
	}


}
