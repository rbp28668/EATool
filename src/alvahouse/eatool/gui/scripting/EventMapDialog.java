/*
 * EventMapDialog.java
 * Project: EATool
 * Created on 20-Mar-2006
 *
 */
package alvahouse.eatool.gui.scripting;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JDialog;

import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.repository.scripting.Scripts;

/**
 * EventMapDialog is a dialog for editing an EventMap.
 * 
 * @author rbp28668
 */
public class EventMapDialog extends BasicDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private EventMapPanel panel;
    /**
     * @param parent
     * @param title
     */
    public EventMapDialog(JDialog parent, String title, EventMap eventMap, Scripts scripts) throws Exception {
        super(parent, title);
        init(eventMap, scripts);
    }

    /**
     * @param parent
     * @param title
     */
    public EventMapDialog(Component parent, String title, EventMap eventMap, Scripts scripts) throws Exception {
        super(parent, title);
        init(eventMap, scripts);
    }

    /**
     * @param eventMap
     */
    private void init(EventMap eventMap, Scripts scripts) throws Exception {
        panel = new EventMapPanel(eventMap,scripts);
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
        panel.onOK();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        return panel.validateInput();
    }

}
