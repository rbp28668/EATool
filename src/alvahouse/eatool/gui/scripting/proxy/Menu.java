/*
 * Menu.java
 * Project: EATool
 * Created on 19-Mar-2006
 *
 */
package alvahouse.eatool.gui.scripting.proxy;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.scripting.Script;
import alvahouse.eatool.repository.scripting.Scripts;
import alvahouse.eatool.scripting.proxy.Scripted;
import alvahouse.eatool.util.UUID;

/**
 * Menu is a proxy class for a menu.  A MenuBar contains a number of Menu.
 * 
 * @author rbp28668
 */
@Scripted(description="A menu on the user item. A menu bar contains a number of menus.")
public class Menu {

    private JMenu menu;
    private alvahouse.eatool.Application app;
    private Repository repository;

    /**
     * Creates a new Menu attached to an underlying JMenu.
     * @param menu is the underlying JMenu.
     */
    Menu(JMenu menu, alvahouse.eatool.Application app, Repository repository) {
        super();
        this.menu = menu;
        this.app = app;
        this.repository = repository;
    }

    /**
     * Adds a script to the menu.
     * @param uuid is the ID of the script (as String).
     * @param display is the text to display on the menu item.
     */
    @Scripted(description="Adds a script to the menu."
    		+ " Parameters are the key for the script and the text to display in the menu item.")
    public void addScript(String uuid, String display) throws Exception{
        Scripts scripts = repository.getScripts(); 
        Script script = scripts.lookupScript(new UUID(uuid));
        
        if(script != null){
            ScriptAction action = new ScriptAction(script,app);
            action.putValue(Action.NAME,display);
            
            JMenuItem menuItem = new JMenuItem(action);
            menu.add(menuItem);
         }
    }

    /**
     * Inserts a menu item at a given position in a menu.
     * @param uuid is the ID of the script (as String).
     * @param display is the text to display on the menu item.
     * @param pos is the zero-based position where the item should be inserted.
     */
    @Scripted(description="Inserts a script into the menu."
    		+ " Parameters are the key for the script, the text to display in the menu item."
    		+ " and the zero based index where the menu item should be inserted.")
    public void insertScript(String uuid, String display, int pos) throws Exception{
        Scripts scripts = repository.getScripts(); 
        Script script = scripts.lookupScript(new UUID(uuid));
        
        if(script != null){
            ScriptAction action = new ScriptAction(script,app);
            action.putValue(Action.NAME,display);
            
            menu.insert(action,pos);
         }
    }
    
    /**
     * Adds a separator at the end of the menu.
     */
    @Scripted(description="Adds a separator at the end of the menu.")
    public void addSeparator(){
        menu.addSeparator();
    }
    
    /**
     * Removes an item from the menu.
     * @param pos is the zero-based index of the item to remove.
     */
    @Scripted(description="Removes an item from the given (zero based) position.")
    public void remove(int pos){
        menu.remove(pos);
    }
    
    /**
     * Clears the menu by removing all items from it.
     */
    @Scripted(description="Clears the menu by removing all items from it.")
    public void clear(){
        menu.removeAll();
    }
    
    /**
     * Get the number of items in the menu.
     * @return the count of items in the menu.
     */
    @Scripted(description="Returns a count of the number of items in the menu.")
    public int getItemCount(){
        return menu.getItemCount();
    }
    
}
