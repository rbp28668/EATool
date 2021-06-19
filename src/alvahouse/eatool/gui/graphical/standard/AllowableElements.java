/*
 * AllowableElements.java
 * Project: EATool
 * Created on 01-Oct-2006
 *
 */
package alvahouse.eatool.gui.graphical.standard;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import alvahouse.eatool.Application;
import alvahouse.eatool.Main;
import alvahouse.eatool.repository.graphical.standard.AbstractSymbol;
import alvahouse.eatool.repository.graphical.standard.Connector;
import alvahouse.eatool.util.SettingsManager;


/** AllowableElements
 * Sets up a list of allowable elements for a diagram.
 * This determines which symbols, connectors and
 * connecto ends can be displayed when diagram types
 * are being created or edited.
 * @author bruce.porteous
 *
 */
public class AllowableElements {
	
    /** Singleton instance */
    private static AllowableElements allowable = null;
    
	private Map allowableSymbols = new HashMap();  // Class keyed by friendly name
	private Map allowableConnectors = new HashMap(); // Class keyed by friendly name
	
	private AllowableElements(SettingsManager config)
	throws ClassNotFoundException {
		SettingsManager.Element root = config.getElement("/DiagramClasses");
		if(root != null) {
			processList(root, allowableSymbols,"Symbols", AbstractSymbol.class);
			processList(root, allowableConnectors,"Connectors", Connector.class);
		}
	}
	
	public static void initInstance(SettingsManager config) throws ClassNotFoundException{
	    if(allowable == null){
	        allowable = new AllowableElements(config);
	    }
	}
	
	public static AllowableElements getAllowable() 
	throws ClassNotFoundException {
		if(allowable == null) {
		    throw new IllegalStateException("Allowable Elements instance not initialised");
		}
		return allowable;
	}
	
	private void processList(SettingsManager.Element root, Map store, String segment, Class type)
	throws ClassNotFoundException{
		SettingsManager.Element classes = root.findChild(segment);
		if(classes != null) {
			for(SettingsManager.Element element : classes.getChildren()){
				if(element.getName().equals("GraphicalElement")){
					String friendlyName = element.attributeRequired("name");
					String className = element.attributeRequired("class");
			
					Class c = Class.forName(className);
					
					// Check that the loaded class is of valid type
					boolean isValidType = false;
					Class superClass = c; 
					while(superClass != null && !isValidType) {
						if(superClass == type){
							isValidType = true;
							break;
						}
						
						// Might be an interface type....
						if(!isValidType && type.isInterface()){
							Class[] interfaces = superClass.getInterfaces();
							for(int i=0; i<interfaces.length; ++i){
								if(interfaces[i] == type){
									isValidType = true;
									break;
								}
							}
						}
						
						superClass = superClass.getSuperclass();							
					}
					if(!isValidType){
						throw new IllegalArgumentException(className + " does not extend " + type.getName());
					}
					
					store.put(friendlyName, c);				
				}
			}
		}
	}
	
	public Map getAllowableSymbols() {
		return allowableSymbols;
	}
	
	public Map getAllowableConnectors() {
		return allowableConnectors;
	}
	
}