/*
 * MenuBar.java
 * Project: EATool
 * Created on 19-Mar-2006
 *
 */
package alvahouse.eatool.gui.scripting.proxy;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.scripting.proxy.Scripted;

/**
 * MenuBar is a proxy for a window's JMenuBar.
 * 
 * @author rbp28668
 */
@Scripted(description="A window's menu bar.")
public class MenuBar {

    private JMenuBar menuBar;
    private alvahouse.eatool.Application app;
    private Repository repository;
    
    /**
     * Creates a MenuBar attached to a JMenuBar.
     * @param menuBar is the underlying JMenuBar.
     */
    MenuBar(JMenuBar menuBar, alvahouse.eatool.Application app, Repository repository) {
        super();
        this.menuBar = menuBar;
        this.app = app;
        this.repository = repository;
    }

    /**
     * Gets the number of Menus in the MenuBar.
     * @return the menu count.
     */
    @Scripted(description="Gets the number of Menus in the MenuBar.")
    public int getMenuCount(){
        return menuBar.getMenuCount();
    }
    
    /**
     * Gets a menu from the menu bar.
     * @param idx is the 0 based index of the menu to get.
     * @return the given menu.
     */
    @Scripted(description="Gets a menu from the menu bar by (zero based) index.")
    public Menu getMenu(int idx){
        return new Menu(menuBar.getMenu(idx), app, repository);
    }
    
    /**
     * Adds a new menu at the end of the menu bar.
     * @param name is the name of the menu.
     * @return the new Menu.
     */
    @Scripted(description="Adds a new empty menu with the given name at the end of the menu bar.")
    public Menu addMenu(String name){
        
        JMenu menu = new JMenu(name);
        menuBar.add(menu);
        menuBar.repaint();
        return new Menu(menu, app, repository);
    }
}
