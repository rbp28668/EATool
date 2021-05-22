/*
 * RepositoryProperties.java
 * Project: EATool
 * Created on 12-May-2006
 *
 */
package alvahouse.eatool.repository;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.XMLWriter;


/**
 * RepositoryProperties provides a basic set of properties for the repository such as
 * name and originator.
 * 
 * @author rbp28668
 */
public class RepositoryProperties  implements Versionable{

    private static final long serialVersionUID = 1L;
    public final static String NAME = "Repository Name";
    public final static String ORIGINATOR = "Originator";
    
    private Properties properties = new Properties();
    private VersionImpl version = new VersionImpl();
    
    /**
     * 
     */
    public RepositoryProperties() {
        super();
        init();
    }

    /**
     * @param arg0
     */
    public RepositoryProperties(Properties arg0) {
        init();
        _set(arg0);
    }

    private void init(){
        properties.setProperty(NAME,"");
        properties.setProperty(ORIGINATOR,"");
    }
    
    /**
     * Internal method to be called when loading properties.  Doesn't
     * update version information.
     * @param key
     * @param value
     * @return
     */
    public void _set(Properties props) {
		if(props.containsKey(NAME))	{
			String name = (String)props.get(NAME);
			properties.setProperty(NAME, name);
		}
    	if(props.containsKey(ORIGINATOR)) {
    		String originator = (String)props.get(ORIGINATOR);
    		properties.setProperty(ORIGINATOR, originator);
    	}
    }
    
    public Properties get() {
    	Properties copy = new Properties();
    	copy.putAll(this.properties);
    	return copy;
    }
    
    public void set(Properties props) {
		String user = System.getProperty("user.name");
		version.modifyBy(user);
		_set(props);
    }
    
    /**
     * Writes the properties out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("RepositoryProperties");
        version.writeXML(out);
        for(Map.Entry<Object,Object> entry : properties.entrySet()){
            out.startEntity("Property");
            out.textEntity("Name", (String)entry.getKey());
            out.textEntity("Value", (String)entry.getValue());
            out.stopEntity();
        }

        out.stopEntity();
    }

    /**
     * Resets the properties to their default state.
     */
    public void reset(){
        properties.clear();
        init();
    }

    /**
     * Gets a count of the number of properties.
     * @return a property count.
     */
    public int getPropertyCount() {
        return properties.size();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone() {
    	RepositoryProperties copy = new RepositoryProperties(properties);
    	version.cloneTo(copy.version);
    	return copy;
    }
    
	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.version.Versionable#getVersion()
	 */
	@Override
	public Version getVersion() {
		return version;
	}
}
