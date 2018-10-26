/*
 * Script.java
 * Project: EATool
 * Created on 05-Mar-2006
 *
 */
package alvahouse.eatool.repository.scripting;

import java.io.IOException;

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Script constains a script (along with its name, description and language).
 * 
 * @author rbp28668
 */
public class Script extends NamedRepositoryItem {

    private String language = "javascript"; // one of the language strings known to bsf
    private String script;	// the actual script.
    
    /**
     * @param uuid
     */
    public Script(UUID uuid) {
        super(uuid);
    }

    /**
     * Gets the language this script is written in.  Defaults to
     * "javascript" unless setLanguage has been called. The language
     * descriptor should be compatible with BSF 2.3
     * @return the language.
     */
    public String getLanguage() {
        return language;
    }
    
    /** Sets the language descriptor for this script.
     * @param language is the BSF language descriptor.
     */
    public void setLanguage(String language) {
        this.language = language;
    }
    
    /**
     * Get the actual text of a script.
     * @return the script text.
     */
    public String getScript() {
        return script;
    }
    
    /**
     * Sets the actual text of a script.
     * @param script is the script text.
     */
    public void setScript(String script) {
        this.script = script;
    }
    
    /**
     * Writes the Script out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Script");
        super.writeAttributesXML(out);
        out.addAttribute("language",getLanguage());
        out.text(script);
        out.stopEntity();
    }
    
    public String toString(){
        return super.toString();
    }
}
