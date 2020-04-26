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

import org.apache.bsf.BSFException;

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
public class EventMap {

    private Map<String,Script> handlers = new HashMap<String,Script>(); // keyed by event name.
    
    /**
     * Creates a new, empty event handler.
     */
    public EventMap() {
        super();
    }

    /**
     * Adds an event to the event map.  The event is given a null handler.
     * @param event is the event name to add.
     * @throws IllegalStateException if the event already exists in the event map.
     */
    public void addEvent(String event){
        if(handlers.containsKey(event)){
            throw new IllegalStateException("Event map already contains the event " + event);
        }
        handlers.put(event,null);
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
        handlers.put(event,handler);
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
    public Map<String,Script> getMap(){
        return Collections.unmodifiableMap(handlers);
    }
    
    /**
     * Fires an event - if the event has a handler that handler script is run
     * by the scripting engine.
     * Note - use this when event has no extra parameters appart from app.
     * @param event is the event to fire.
     * @throws BSFException if there is an error in the script.
     * @throws IllegalStateException if the event map does not contain the given event.
     */
    public void fireEvent(String event) throws BSFException{
        if(!handlers.containsKey(event)){
            throw new IllegalStateException("Event map does not contain the event " + event);
        }
        Script handler = (Script)handlers.get(event);
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
        
        for(Map.Entry<String,Script> entry : handlers.entrySet()){
            Script handler = (Script)entry.getValue();
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
        copy.handlers.clear();
        copy.handlers.putAll(handlers);
    }

    /**
     * Gets a ScriptContext for an event where the caller needs to add some more
     * context around the event.
     * @param event is the event that is being fired.
     * @return a ScriptContext for the event or null if no handler registered.
     */
    public ScriptContext getContextFor(String event) {
        if(!handlers.containsKey(event)){
            throw new IllegalStateException("Event map does not contain the event " + event);
        }
        ScriptContext context = null;
        Script handler = (Script)handlers.get(event);
        if(handler != null) {
            context = new ScriptContext(handler);
        }
        return context;
    }
    
    
}
