/*
 * ScriptFactory.java
 * Project: EATool
 * Created on 05-Mar-2006
 *
 */
package alvahouse.eatool.repository.scripting;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.base.NamedRepositoryItemFactory;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;

/**
 * ScriptFactory
 * 
 * @author rbp28668
 */
public class ScriptFactory extends NamedRepositoryItemFactory 
implements IXMLContentHandler{

    private Scripts scripts;
    private Script currentScript = null;
    private StringBuffer buff = new StringBuffer(256);
    private ProgressCounter counter;

    
    /**
     * @param counter
     * 
     */
    public ScriptFactory(ProgressCounter counter, Scripts scripts) {
        super();
        this.scripts = scripts;
        this.counter = counter;
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String local, Attributes attrs) throws InputException {
        if(local.equals("Script")){
            if(currentScript != null){
                throw new InputException("Nested scripts");
            }
            UUID uuid = getUUID(attrs);
            
            String lang = attrs.getValue("language");
            if(lang == null){
                throw new InputException("Missing language specifier in script");
            }
            
            currentScript = new Script(uuid);
            super.getCommonFields(currentScript,attrs);
            currentScript.setLanguage(lang);
            buff.delete(0,buff.length());
        } else if (local.equals("Version")) {
        	VersionImpl.readXML(attrs, currentScript);
        }
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        if(local.equals("Script")){
            currentScript.setScript(buff.toString());
            buff.delete(0,buff.length());
            try {
				scripts._add(currentScript);
			} catch (Exception e) {
				throw new InputException("Unable to add script",e);
			}
            currentScript = null;
            counter.count("Script");
        }
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) throws InputException {
        buff.append(str);
    }

}
