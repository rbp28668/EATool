package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import alvahouse.eatool.Main;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaEntityDisplayHint;
import alvahouse.eatool.repository.metamodel.MetaProperty;

/**
 * MetaEntityDisplayHintEditor is an editor dialog for editing the
 * meta entity display hints.  The display hints determine which
 * fields should be used when a textual representation of an entity
 * is needed.  The editor selects those fields.
 * @author bruce.porteous
 */
public class MetaEntityDisplayHintEditor extends BasicDialog {

	private static final long serialVersionUID = 1L;
	private MetaEntityDisplayHint originalHint;
	private MetaEntity metaEntity;
	private SelectItemsPanel<MetaProperty> hintsPanel;
	/**
	 * Constructor for MetaEntityDisplayHintEditor.
	 * @param parent
	 * @param title
	 */
	public MetaEntityDisplayHintEditor(Component parent, MetaEntityDisplayHint dh, MetaEntity me) throws Exception{
        super(parent ,"Edit Display Hint");
        
        if(dh == null) {
            throw new NullPointerException("Can't edit a null MetaEntityDisplayHint");
        }
        if(me == null) {
            throw new NullPointerException("Can't edit display hint for null MetaEntity");
        }

		metaEntity = me;
        originalHint = dh;
        
        JLabel label = new JLabel("Hint for " + metaEntity.getName());
        label.setBorder(componentBorder);
        getContentPane().add(label, BorderLayout.NORTH);
        
        List<MetaProperty> allowed = new LinkedList<>(metaEntity.getMetaProperties());
        List<MetaProperty> selected = new LinkedList<>();
        allowed.forEach( mp -> {if(dh.referencesProperty(mp)) selected.add(mp);} );

        hintsPanel = new SelectItemsPanel<MetaProperty>(allowed, selected);
        getContentPane().add(hintsPanel, BorderLayout.CENTER);
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
	}

	/**
	 * @see alvahouse.eatool.GUI.BasicDialog#onOK()
	 */
	protected void onOK() {
		originalHint.clearPropertyKeys();
		Collection<MetaProperty> properties = hintsPanel.getSelectedItems();
		for (MetaProperty element : properties) {
			originalHint.addPropertyKey(element.getKey());
		}
	}

	/**
	 * @see alvahouse.eatool.GUI.BasicDialog#validateInput()
	 * In this case, any combination of fields is valid so
	 * always return true.
	 */
	protected boolean validateInput() {
		return true;
	}

}
