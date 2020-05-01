/*
 * ModelBrowser.java
 * Project: EATool
 * Created on 24-Feb-2006
 *
 */
package alvahouse.eatool.gui.scripting;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.scripting.proxy.Scripted;

/**
 * ModelBrowser is a HTMLProxy based browser for simple browsing of the model or
 * meta-model.
 * 
 * @author rbp28668
 */
public class FunctionHelpBrowser extends JInternalFrame {

    private static final long serialVersionUID = 1L;
    private DisplayPane display;
    private Application app;
    private final static String WINDOW_SETTINGS = "/Windows/FunctionHelpBrowser";

    /**
     * Creates a new, empty browser.
     */
    public FunctionHelpBrowser(Application app){
        this.app = app;
        setTitle("Function Help");
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS, app);

        display = new DisplayPane();
        JScrollPane scrollPane = new JScrollPane(display);
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
    }
    
    /**
     * Browses a class
     * @param metaModel is the meta-model to browse.
     */
    public void browse(Class<?> objClass){
        display.setPage(objClass);
    }

    
    public void dispose() {
        GUIBuilder.saveBounds(this,WINDOW_SETTINGS, app);
        app.getWindowCoordinator().removeFrame(this);
        super.dispose();
    }

    private class DisplayPane extends JEditorPane{
        private static final long serialVersionUID = 1L;

        public DisplayPane() {
            super();
            setEditable(false);
            setContentType("text/html");
            
            addHyperlinkListener( new HyperlinkListener() {
               public void hyperlinkUpdate(HyperlinkEvent ev){
                   try{
	                   if(ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
	                       URL url = ev.getURL();
	                       if(url != null){

	                           String className = url.getPath();
	                           int idx = className.lastIndexOf("/");
	                           if(idx >= 0){
	                               className = className.substring(idx+1);
	                           }
	                           Class<?> objClass = Class.forName(className);
	                           FunctionHelpBrowser.this.browse(objClass);
		                           
	                       }
	                   }
                   } catch (Exception e){
                       new ExceptionDisplay(FunctionHelpBrowser.this,e);
                   }
                   
               }
            });
         }

        public void setPage(Class<?> objClass){
            ToHTML html = new ToHTML(objClass);
             setText(html.toString());
        }

    }
    
    /**
     * Inner class to generate HTMLProxy for various types of "thing".
     */

    public static class ToHTML {
        private StringBuffer buff = new StringBuffer(512);
        
        private final static Set<String> allowedPackages = new HashSet<String>();
        
        static {
        	allowedPackages.add(alvahouse.eatool.gui.scripting.proxy.ApplicationProxy.class.getPackage().getName());
        	allowedPackages.add(alvahouse.eatool.scripting.proxy.RepositoryProxy.class.getPackage().getName());
        }

        /**
         * Creates HTMLProxy that describes the Meta-Entities in the meta model.
         * @param m is the meta-model to show.
         */
        public ToHTML(Class<?> objClass){
            header();
            
            h1(simpleName(objClass));
            
        	// May be over-ridden by an annotation
            if(objClass.isAnnotationPresent(Scripted.class)) {
            	Scripted annotation = objClass.getAnnotation(Scripted.class);
            	if(!annotation.description().isEmpty()) {
            		buff.append("<p><i>");
            		buff.append(annotation.description());
            		buff.append("</i></p>");
            	}
            }

            
            if(objClass != alvahouse.eatool.gui.scripting.proxy.ApplicationProxy.class){
	            buff.append("<p>Up to ");
	            buff.append(convert(alvahouse.eatool.gui.scripting.proxy.ApplicationProxy.class));
	            buff.append("</p>");
	            hr();
            }
            
            buff.append("<p>");
            buff.append("<table>");
            Method[] methods = objClass.getDeclaredMethods();
            for(int i=0; i<methods.length; ++i){
                Method method = methods[i];
                if(Modifier.isPublic(method.getModifiers())){

                	// Default values for this method name and comment
                	String methodName = method.getName();
                	String comment = "";

                	// May be over-ridden by an annotation
                    if(method.isAnnotationPresent(Scripted.class)) {
                    	Scripted annotation = method.getAnnotation(Scripted.class);
                    	if(!annotation.name().isEmpty()) {
                    		methodName = annotation.name();
                    	}
                    	comment = annotation.description();
                    }

                    buff.append("<tr>");

                	buff.append("<td>");
                    buff.append(' ');
                    Class<?> retClass = method.getReturnType();
                    buff.append(convert(retClass));
                    buff.append(' ');
                    buff.append(methodName);
                    buff.append('(');
                    //Class<?>[] params = method.getParameterTypes();
                    Parameter params[] = method.getParameters();
                    for(int j=0; j<params.length; ++j){
                        if(j > 0){
                            buff.append(',');
                        }
                        buff.append(convert(params[j].getType()));
                        // parameter names not maintained unless specially compiled
                        // buff.append(' ');
                        // buff.append(params[j].getName());
                    }
                    buff.append(')');
                    buff.append("</td>");
                    
                    buff.append("<td>");
                	if(comment.isEmpty()){
                		buff.append(" ");
                	} else {
                		buff.append("<i>");
                		buff.append(comment);
                		buff.append("</i>");
                	}
                    buff.append("<td>");
                    buff.append("</tr>");
                    //br();
                }
                
            }
            buff.append("<table>");
            buff.append("</p>");
            
            footer();
        }

        private String convert(Class<?> type){
            String name;
            if(type == Void.TYPE) {  // void?
            	name = "void";
            } else if(type.getPackage() == null) { // primitive ?
            	name = simpleName(type);
            } else if(type.isArray()){  // array?
                name = convert(type.getComponentType()) + "[]";
            } else {
                if(allowedPackages.contains(type.getPackage().getName())){
                    name = "<a href=\"http://localhost/" + type.getCanonicalName() + "\">" + simpleName(type)+"</a>";
                } else {
                    name = simpleName(type);
                }
            }
            return name;
        }
                
        private String simpleName(Class<?> type){
            String name = type.getName();
            int idx = name.lastIndexOf('.');
            if(idx >= 0){
                name = name.substring(idx+1);
            }

            // By convention, proxy class names end in Proxy so remove if present  
            if(name.endsWith("Proxy")) {
            	name = name.substring(0, name.length() - "Proxy".length());
            }
            
        	// May be over-ridden by an annotation
            if(type.isAnnotationPresent(Scripted.class)) {
            	Scripted annotation = type.getAnnotation(Scripted.class);
            	if(!annotation.name().isEmpty()) {
            		name = annotation.name();
            	}
            }

            return name;
        }
        
        private void header(){
            buff.append("<html><head></head><body>");
        }
        
        private void footer(){
            buff.append("</body></html>");
        }
 
        private void hr(){
            buff.append("<hr></hr>");
        }

        private void br(){
            buff.append("<br></br>");
        }
        

        private void td(String s){
            buff.append("<td>");
            buff.append(s);
            buff.append("</td>");
        }

        private void h1(String h1){
            buff.append("<h1>");
            buff.append(h1);
            buff.append("</h1>");
        }

        private void h2(String h2){
            buff.append("<h2>");
            buff.append(h2);
            buff.append("</h2>");
        }

        private void h3(String h3){
            buff.append("<h3>");
            buff.append(h3);
            buff.append("</h3>");
        }
        
        private void a(String href, String text){
            buff.append("<a href=\"" + href + "\">");
            buff.append(text);
            buff.append("</a>");
        }
        
        public String toString(){
            return buff.toString();
        }
        
    }
 
}
