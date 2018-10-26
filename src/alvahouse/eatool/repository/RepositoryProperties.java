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

import alvahouse.eatool.util.XMLWriter;


/**
 * RepositoryProperties provides a basic set of properties for the repository such as
 * name and originator.
 * 
 * @author rbp28668
 */
public class RepositoryProperties extends Properties {

    private static final long serialVersionUID = 1L;
    public final static String NAME = "Repository Name";
    public final static String ORIGINATOR = "Originator";
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
        super(arg0);
        init();
    }

    private void init(){
        setProperty(NAME,"");
        setProperty(ORIGINATOR,"");
    }
    
    /**
     * Writes the properties out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("RepositoryProperties");
        
        for(Map.Entry<Object,Object> entry : this.entrySet()){
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
        clear();
        init();
    }

    /**
     * Gets a count of the number of properties.
     * @return a property count.
     */
    public int getPropertyCount() {
        return this.size();
    }
}
