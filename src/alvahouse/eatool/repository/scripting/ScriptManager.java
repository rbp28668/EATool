/*
 * ScriptManager.java
 * Project: EATool
 * Created on 04-Mar-2006
 *
 */
package alvahouse.eatool.repository.scripting;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import alvahouse.eatool.repository.scripting.ScriptContext.ContextObject;
import alvahouse.eatool.util.SettingsManager;
import alvahouse.eatool.util.SettingsManager.Element;

/**
 * ScriptManager
 * 
 * @author rbp28668
 */
public class ScriptManager {

    /*  Language known to BSF 2.3 */
//    private static final String[] languages = {
//    "javascript","org.apache.bsf.engines.javascript.JavaScriptEngine","js",
//    "jacl","org.apache.bsf.engines.jacl.JaclEngine","jacl",
//    "netrexx","org.apache.bsf.engines.netrexx.NetRexxEngine","nrx",
//    "java","org.apache.bsf.engines.java.JavaEngine","java",
//    "javaclass","org.apache.bsf.engines.javaclass.JavaClassEngine","class",
//    "bml","org.apache.bml.ext.BMLEngine","bml",
//    "vbscript","org.apache.bsf.engines.activescript.ActiveScriptEngine","vbs",
//    "jscript","org.apache.bsf.engines.activescript.ActiveScriptEngine","jss",
//    "perlscript","org.apache.bsf.engines.activescript.ActiveScriptEngine","pls",
//    "perl","org.apache.bsf.engines.perl.PerlEngine","pl",
//    "jpython","org.apache.bsf.engines.jpython.JPythonEngine","py",
//    "jython","org.apache.bsf.engines.jython.JythonEngine","py",
//    "lotusscript","org.apache.bsf.engines.lotusscript.LsEngine","lss",
//    "xslt","org.apache.bsf.engines.xslt.XSLTEngine","xslt",
//    "pnuts","pnuts.ext.PnutsBSFEngine","pnut",
//    "beanbasic","org.apache.bsf.engines.beanbasic.BeanBasicEngine","bb",
//    "beanshell","bsh.util.BeanShellBSFEngine","bsh",
//    "ruby","org.jruby.javasupport.bsf.JRubyEngine","rb",
//    "judoscript","com.judoscript.BSFJudoEngine","judo|jud"
//    };
    /* */
    
    private BSFManager manager;
    private List<String> available = new LinkedList<String>();
    private static ScriptManager instance = null;
    private ScriptRunner runner;
    
    
    /**
     * @throws BSFException
     * 
     */
    private ScriptManager(SettingsManager config) throws BSFException {
        super();
        addLanguages(config);
        manager = new BSFManager();
        runner = new ScriptRunner(manager);
        runner.start();
    }
    
    public static void initInstance(SettingsManager config) throws BSFException{
        if(instance == null) {
            instance = new ScriptManager(config);
        }

    }
    public static ScriptManager getInstance() throws BSFException{
        if(instance == null) {
            throw new IllegalStateException("ScriptManager not initialised");
        }
        return instance;
    }
    
    public void runScript(Script script) throws BSFException{
        runner.queueScript(new ScriptContext(script));
    }

    public void runScript(ScriptContext context) throws BSFException{
        runner.queueScript(context);
    }

    public void addListener(ScriptErrorListener listener){
        runner.addListener(listener);
    }
    
    public void removeListener(ScriptErrorListener listener){
        runner.removeListener(listener);
    }

    public void declareObject(String name, Object obj, Class<?> objClass) throws BSFException{
        synchronized(manager){
            manager.declareBean(name, obj, objClass);
        }
    }

    public void undeclareObject(String name) throws BSFException{
        synchronized(manager){
            manager.undeclareBean(name);
        }
    }

    /**
     * Uses the config file to determine which scripting languages
     * to use. 
     */
    private void addLanguages(SettingsManager config){
        SettingsManager.Element cfg = config.getOrCreateElement("/Scripting");

        for(SettingsManager.Element script : cfg.getChildren()){
            String lang = processScript(script);
            if(lang != null){
                available.add(lang);
            }
        }
    }

    /**
     * Sets up a single scripting language from a config element.
     * @param script
     * @return the language name (or null if not valid).
     */
    private String processScript(Element script) {
        
        if(!script.getName().equals("Language")){
            return null;
        }
        
        String name = script.attributeRequired("name");
        String className = script.attributeRequired("class");
        String extnList = script.attributeRequired("ext");
        
        
        StringTokenizer toks = new StringTokenizer(extnList,"|");
        String[] extns = new String[toks.countTokens()];
        int idx = 0;
        while(toks.hasMoreTokens()){
            extns[idx++] = toks.nextToken();
        }
        
        if(!BSFManager.isLanguageRegistered(name)){        
            BSFManager.registerScriptingEngine(name, className, extns);
        }
        
        return name;
    }
    
    public String[] getAvailableLanguages(){
        String[] langs = new String[available.size()]; 
        return (String[])available.toArray(langs);
     }
    
    void exec(Script script) throws BSFException{
        manager.exec(script.getLanguage(), script.getName(),0,0, script.getScript());
    }
    
//    public static void main(String[] args){
//        try {
//            BSFManager.getLangFromFilename("wibble.js");
////            String lang = "javascript"; 
//            String lang = "ruby"; 
//                       System.out.println(lang);
//            boolean reg = BSFManager.isLanguageRegistered(lang);
//            System.out.println(reg);
//       
//            for(int i=0; i<languages.length; i+=3){
//                lang = languages[i];
//                reg = BSFManager.isLanguageRegistered(lang);
//                //System.out.println(lang + ": " +reg);
//                
//                try {
//                    Class langClass = Class.forName(languages[i+1]);
//                    System.out.println(lang + " FOUND");
//                    
//                } catch (Throwable t) {
//                    System.out.println(lang + " not found");
//                }
//            }
//            
//            BSFManager manager = new BSFManager();
//            
//                        
//        } catch (BSFException e) {
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        }
//    }
    
    private static class ScriptRunner extends Thread {
        
        private LinkedList<ScriptContext> queue = new LinkedList<ScriptContext>();
        private List<ScriptErrorListener> listeners = new LinkedList<ScriptErrorListener>();
        
        private BSFManager manager;
        
        ScriptRunner(BSFManager manager){
            this.manager = manager;
            setDaemon(true);
        }
        
        private void declareObject(String name, Object obj, Class<?> objClass) throws BSFException{
            synchronized(manager){
                manager.declareBean(name, obj, objClass);
            }
        }

        private void undeclareObject(String name) throws BSFException{
            synchronized(manager){
                manager.undeclareBean(name);
            }
        }
        
        public void run(){
            ScriptContext script = null;
            while((script = nextScript()) != null){
                synchronized(manager){
                    exec(script);
                }
            }
        }
        
        void exec(ScriptContext context){

            List<ContextObject> ctx = context.getContext();
            
            try {
                for(ContextObject co : ctx){
                    declareObject(co.name, co.object, co.objClass);
                }
                
                ScriptErrorListener listener = context.getErrorHandler();
                if(listener != null){
                    addListener(listener);
                }
                
                Script script = context.getScript();
                try {
                    manager.exec(script.getLanguage(), script.getName(),0,0, script.getScript());
                } catch (BSFException e) {
                    synchronized(listeners){
                        for(ScriptErrorListener l : listeners){
                            l.scriptError(e.getMessage());
                        }
                    }
                } finally {

                    if(listener != null){
                        removeListener(listener);
                    }
                    
                    for(ListIterator<ContextObject> iter = ctx.listIterator(ctx.size()); iter.hasPrevious();){
                        ContextObject co = iter.previous();
                        undeclareObject(co.name);
                    }
                }
            } catch (BSFException e) {
                synchronized(listeners){
                    for(ScriptErrorListener l : listeners){
                        l.scriptError(e.getMessage());
                    }
                }
            }
            
        }

        private synchronized ScriptContext nextScript(){
            try {
                if(queue.isEmpty()){
                    wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            if(interrupted()){
                return null;
            }
            
            return (ScriptContext)queue.removeFirst();
        }
        
        private synchronized boolean scriptsWaiting(){
            return !queue.isEmpty();
        }
        
        private synchronized void queueScript(ScriptContext context){
            queue.addLast(context);
            notifyAll();
        }
        
        private void addListener(ScriptErrorListener listener){
            synchronized (listeners) {
                listeners.add(listener);
            }
        }
        
        private void removeListener(ScriptErrorListener listener){
            synchronized (listeners) {
                listeners.remove(listener);
            }
        }
        
    }
}
