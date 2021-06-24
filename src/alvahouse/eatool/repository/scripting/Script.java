/*
 * Script.java
 * Project: EATool
 * Created on 05-Mar-2006
 *
 */
package alvahouse.eatool.repository.scripting;

import java.io.IOException;

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.repository.dto.scripting.ScriptDto;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Script constains a script (along with its name, description and language).
 * 
 * @author rbp28668
 */
public class Script extends NamedRepositoryItem implements Versionable {

    private String language = "javascript"; // one of the language strings known to bsf
    private String script;	// the actual script.
    private VersionImpl version = new VersionImpl();
    
    /**
     * @param uuid
     */
    public Script(UUID uuid) {
        super(uuid);
    }

    public Script(ScriptDto dto) {
    	super(dto);
    	language = dto.getLanguage();
    	script = dto.getScript();
    	version = new VersionImpl(dto.getVersion());
    }
    
    public ScriptDto toDto() {
    	ScriptDto dto = new ScriptDto();
    	copyTo(dto);
    	return dto;
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
        version.writeXML(out);
        out.text(script);
        out.stopEntity();
    }
    
    public String toString(){
        return super.toString();
    }

    
	protected void cloneTo(Script copy) {
		super.cloneTo(copy);
		version.cloneTo(copy.version);
		copy.language = language;
		copy.script = script;
	}

	protected void copyTo(ScriptDto dto) {
		super.copyTo(dto);
		dto.setLanguage(language);
		dto.setScript(script);
		dto.setVersion(version.toDao());
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone()  {
		Script copy = new Script(getKey());
		cloneTo(copy);
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
