/**
 * 
 */
package alvahouse.eatool.repository.dto.html;

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
import alvahouse.eatool.repository.dto.VersionedDto;
import alvahouse.eatool.repository.dto.scripting.EventMapDto;
import alvahouse.eatool.util.Base64InputStream;
import alvahouse.eatool.util.Base64OutputStream;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "htmlPage")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "htmlJson","dynamic","eventMap","version"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type_name")
public class HTMLPageDto extends NamedRepositoryItemDto implements VersionedDto {

    private String html;
    private boolean isDynamic;
    private EventMapDto eventMap;
    private VersionDto version;
    private String rev;
    
    /**
	 * @return the html
	 */
    @JsonIgnore
	public String getHtml() {
		return html;
	}
	/**
	 * @param html the html to set
	 */
	public void setHtml(String html) {
		this.html = html;
	}


    @XmlElement(name="html",required=true)
	@JsonProperty(value="html")
	public String getHtmlJson() throws IOException {
    	if(html == null) {
    		return null;
    	}
    	
    	String data = null;
        try(Base64OutputStream stream = new Base64OutputStream()){
	        byte[] bytes = html.getBytes("UTF-8");
	        stream.write(bytes);
	        data = stream.getData();
        }
        return data;
	}
	/**
	 * @param script the script to set
	 * @throws UnsupportedEncodingException 
	 */
	public void setHtmlJson(String base64) throws IOException {
	   StringBuilder html = new StringBuilder();
        try(InputStream stream = new Base64InputStream(base64)){
	        try(Reader reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"))){
	        	int ch = 0;
	        	while((ch = reader.read()) != -1) {
	        		html.append((char)ch);
	        	}
	        }
        }
		this.html = html.toString();
	}

	
	/**
	 * @return the isDynamic
	 */
	@XmlElement
	public boolean isDynamic() {
		return isDynamic;
	}
	/**
	 * @param isDynamic the isDynamic to set
	 */
	public void setDynamic(boolean isDynamic) {
		this.isDynamic = isDynamic;
	}
	
	/**
	 * @return the eventMap
	 */
	@XmlElement
	public EventMapDto getEventMap() {
		return eventMap;
	}
	/**
	 * @param eventMap the eventMap to set
	 */
	public void setEventMap(EventMapDto eventMap) {
		this.eventMap = eventMap;
	}

	/**
	 * @return the version
	 */
	@XmlElement
	@Override
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
	@Override
	@JsonProperty("_rev")
	public String getRev() {
		return rev;
	}

	/**
	 * @param rev the rev to set
	 */
	@Override
	public void setRev(String rev) {
		this.rev = rev;
	}
	

}
