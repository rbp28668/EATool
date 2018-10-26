/*
 * EventMapFactory.java
 * Project: EATool
 * Created on 20-Mar-2006
 *
 */
package alvahouse.eatool.repository.scripting;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;

/**
 * EventMapFactory is for de-serialising an EventMap.
 * 
 * @author rbp28668
 */
public class EventMapFactory extends FactoryBase implements IXMLContentHandler{

    private EventMap events;
    private Scripts scripts;
    private ProgressCounter counter;

    
    /**
     * Creates a new handler for a given event map and set of scripts.
     * @param counter
     * @param events is the event map to set up.
     * @param scripts is the set of scripts from which the handler 
     * scripts should be taken.
     */
    public EventMapFactory(ProgressCounter counter, EventMap events, Scripts scripts) {
        super();
        this.events = events;
        this.scripts = scripts;
        this.counter = counter;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String local, Attributes attrs) throws InputException {
        if(local.equals("Event")){
            String name = attrs.getValue("name");
            if(name == null){
                throw new InputException("Missing name for event");
            }
            
            UUID scriptID = getUUID(attrs,"handler");
            
            Script handler = scripts.lookupScript(scriptID);
            if(handler == null){
                throw new InputException("No handler script found for " + scriptID.toString());
            }
            
            events.setHandler(name,handler);
            counter.count("Event");
            
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) throws InputException {
    }

    /**
     * Get the current event map.  This allows handlers to get the existing event map,
     * substitue their own and then restore the original afterwards.
     * @return the current event map.
     */
    public EventMap getEventMap(){
        return events;
    }
    
    /**
     * Set a new eventMap to update.
     * @param eventMap is the new event map.
     */
    public void setEventMap(EventMap eventMap){
        events = eventMap;
    }
}
