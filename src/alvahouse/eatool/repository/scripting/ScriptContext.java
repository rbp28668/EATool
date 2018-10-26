/*
 * ScriptContext.java
 * Project: EATool
 * Created on 26-Mar-2006
 *
 */
package alvahouse.eatool.repository.scripting;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * ScriptContext provides the collection of context objects which
 * will be made available to the script as global objects.
 * 
 * @author rbp28668
 */
public class ScriptContext {

    private Script script;
    private ScriptErrorListener listener;
    private LinkedList<ContextObject> context= new LinkedList<ContextObject>();
    /**
     * 
     */
    public ScriptContext(Script script) {
        super();
        this.script = script;
    }
    
    public void addObject(String name, Object object, Class<?> objClass){
        ContextObject co = new ContextObject(name,object,objClass);
        context.addLast(co);
    }
    
    Script getScript(){
        return script;
    }
    
    List<ContextObject> getContext(){
        return Collections.unmodifiableList(context);
    }

    /**
     * @param errHandler
     */
    public void setErrorHandler(ScriptErrorListener listener) {
        this.listener = listener;
    }

    /**
     * Get any listener associated with this context.
     * @return the listener or null if none set.
     */
    public ScriptErrorListener getErrorHandler() {
        return listener;
    }

    static class ContextObject{
        String name;
        Object object;
        Class<?> objClass;
        
        ContextObject(String name, Object object, Class<?> objClass){ 
            this.name = name;
            this.object = object;
            this.objClass = objClass;
        }
    }
    
    
}
