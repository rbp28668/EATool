/*
 * EventMap.java
 * Project: EATool
 * Created on 20-Mar-2006
 *
 */
package alvahouse.eatool.repository.scripting;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.bsf.BSFException;

import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.XMLWriter;

/**
 * EventMap provides a mapping from named events to scripts which 
 * handle that event.  Note that this typically keeps a full list of
 * events although some may have null handlers. To use this correctly
 * add the events using <code>addEvent</code>.  Then Script
 * handlers can be added using <code>setHandler<code> and <code>removeHandler</code>.
 * Events are fired by calling <code>fireEvent</code> with the appropriate event
 * string.  If there is a handler registered to that event it will be run by
 * the scripting engine.
 *  
 * 
 * @author rbp28668
 */
public class EventMap implements Versionable{

    private final Map<String,ScriptProxy> handlers = new HashMap<String,ScriptProxy>(); // keyed by event name.
    private final VersionImpl version = new VersionImpl();
    /**
     * Creates a new, empty event handler.
     * @param scripts is the scripts collection used to look up any scripts if an event is fired
     * or script accessed.
     */
    public EventMap() {
        super();
    }

    /**
     * Adds an event to the map with a null handler if it doesn't already exist.
     * @param event is the event name to add.
     */
    public void ensureEvent(String event){
        if(!handlers.containsKey(event)){
            handlers.put(event,new ScriptProxy());
        }
    }
    
    /**
     * Sets a handler for a given event.
     * @param event is the event to set.
     * @param handler is the script to run in response to the given event.
     * @throws IllegalStateException if the event map does not contain the given event.
     */
    public void setHandler(String event, Script handler){
        if(!handlers.containsKey(event)){
            throw new IllegalStateException("Event map does not contain the event " + event);
        }
        if(handler == null){
            throw new NullPointerException("Can't set a null event handler");
        }
        ScriptProxy proxy = new ScriptProxy();
        proxy.set(handler);
        handlers.put(event,proxy);
    }
    
    /**
     * Clears the handler for a given event.
     * @param event is the event to clear the handler for.
     * @throws IllegalStateException if the event map does not contain the given event.
     */
    public void removeHandler(String event){
        if(!handlers.containsKey(event)){
            throw new IllegalStateException("Event map does not contain the event " + event);
        }
        handlers.put(event,null);
    }
    
    /**
     * Removes all the handlers leaving the events intact.
     */
    public void deleteHandlers(){
        for(String event : handlers.keySet()){
            removeHandler(event);
        }
    }
    
    /**
     * Deletes all the events.
     */
    public void clear(){
        handlers.clear();
    }
    /**
     * Gets the event map of <code>Script</code> keyed by the event names.
     * @return an unmodifiable event map.
     */
    public Set<String> getEvents(){
        return Collections.unmodifiableSet(handlers.keySet());
    }

    /**
     * Determines whether there's a handler for the given event.
     * @param event is the event (name) to check for.
     * @return true if there's a handler, false if not or if the event doesn't exist.
     */
    public boolean hasHandler(String event) {
    	ScriptProxy proxy = handlers.get(event);
    	if(proxy == null) return false;
    	return !proxy.isNull();
    }
    /**
     * Gets a script for the given event.
     * @param event
     * @return
     * @throws Exception
     */
    public Script get(String event, Scripts scripts) throws Exception {
    	ScriptProxy proxy = handlers.get(event);

    	if(proxy == null){
            throw new IllegalStateException("Event map does not contain the event " + event);
        }

    	return proxy.get(scripts);
    }
    
    /**
     * Gets a ScriptContext for an event where the caller needs to add some more
     * context around the event.
     * @param event is the event that is being fired.
     * @return a ScriptContext for the event or null if no handler registered.
     */
    public ScriptContext getContextFor(String event, Scripts scripts) throws Exception{

    	ScriptProxy proxy = handlers.get(event);

    	if(proxy == null){
            throw new IllegalStateException("Event map does not contain the event " + event);
        }
        ScriptContext context = null;
        Script handler = proxy.get(scripts);
        if(handler != null) {
            context = new ScriptContext(handler);
        }
        return context;
    }

    /**
     * Fires an event - if the event has a handler that handler script is run
     * by the scripting engine.
     * Note - use this when event has no extra parameters appart from app.
     * @param event is the event to fire.
     * @throws BSFException if there is an error in the script.
     * @throws IllegalStateException if the event map does not contain the given event.
     */
    public void fireEvent(String event, Scripts scripts) throws Exception{
        ScriptProxy proxy = handlers.get(event);
    	
    	if(proxy == null){
            throw new IllegalStateException("Event map does not contain the event " + event);
        }
        
        Script handler = proxy.get(scripts);
        if(handler != null) {
            ScriptManager.getInstance().runScript(handler);
        }
    }

  
   
    /**
     * Writes the EventMap out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        writeXML(out,"EventMap");
    }

    /**
     * Writes the EventMap out as XML using the given tag
     * @param out is the XMLWriterDirect to write the XML to
     * @param tag is the element name to use.
     */
    public void writeXML(XMLWriter out, String tag) throws IOException {
        out.startEntity(tag);

        version.writeXML(out);
        
        for(Map.Entry<String,ScriptProxy> entry : handlers.entrySet()){
            ScriptProxy handler = entry.getValue();
            if(handler != null){
                out.startEntity("Event");
                out.addAttribute("name",(String)entry.getKey());
                out.addAttribute("handler", handler.getKey().toString());
                out.stopEntity();
            }
        }

        out.stopEntity();
    }

    /**
     * Writes the EventMap out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXMLUnversioned(XMLWriter out) throws IOException {
        writeXMLUnversioned(out,"EventMap");
    }

    /**
     * Writes the EventMap out as XML using the given tag but without version
     * information for when the event map is embedded in another versioned component.
     * @param out is the XMLWriterDirect to write the XML to
     * @param tag is the element name to use.
     */
    public void writeXMLUnversioned(XMLWriter out, String tag) throws IOException {
        out.startEntity(tag);
        
        for(Map.Entry<String,ScriptProxy> entry : handlers.entrySet()){
            ScriptProxy handler = entry.getValue();
            if(handler != null){
                out.startEntity("Event");
                out.addAttribute("name",(String)entry.getKey());
                out.addAttribute("handler", handler.getKey().toString());
                out.stopEntity();
            }
        }

        out.stopEntity();
    }

    /**
     * Gets a count of the number of events.
     * @return an event count.
     */
    public int getEventCount() {
        return handlers.size();
    }

    /**
     * Clones this event map to a copy.
     * @param copy is the destination of the copy.
     */
    public void cloneTo(EventMap copy) {
        version.cloneTo(copy.version);
        copy.handlers.clear();
        for( Map.Entry<String,ScriptProxy> entry: handlers.entrySet()) {
        	String key = entry.getKey();
        	ScriptProxy script = entry.getValue();
        	script =  (ScriptProxy)script.clone();
        	copy.handlers.put(key, script);
        }
    }

    /**
     * Clones the event map and ties it to a scripts collection that will be used
     * to access any script (lazy loading)
     * @param scripts will be the source of any scripts if an event is fired or script accessed.
     * @return a clone of the event map bound to the given scripts collection.
     */
    @Override
    public Object clone() {
    	EventMap copy = new EventMap();
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
