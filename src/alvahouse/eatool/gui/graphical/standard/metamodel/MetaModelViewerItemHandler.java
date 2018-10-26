/*
 * MetaModelViewerItemHandler.java
 * Project: EATool
 * Created on 02-Feb-2006
 *
 */
package alvahouse.eatool.gui.graphical.standard.metamodel;

import java.awt.Component;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.gui.ItemSelectionDialog;
import alvahouse.eatool.gui.MetaEntityEditor;
import alvahouse.eatool.gui.MetaRelationshipEditor;
import alvahouse.eatool.gui.PositionalPopup;
import alvahouse.eatool.gui.graphical.layout.Arc;
import alvahouse.eatool.gui.graphical.standard.ItemHandler;
import alvahouse.eatool.gui.graphical.standard.StandardDiagramViewer;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.exception.LogicException;
import alvahouse.eatool.repository.graphical.standard.Connector;
import alvahouse.eatool.repository.graphical.standard.ConnectorType;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.repository.graphical.standard.Symbol;
import alvahouse.eatool.repository.graphical.standard.SymbolType;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.impl.MetaEntityImpl;
import alvahouse.eatool.repository.metamodel.impl.MetaRelationshipImpl;
import alvahouse.eatool.util.SettingsManager;
import alvahouse.eatool.util.UUID;

/**
 * MetaModelViewerItemHandler
 * 
 * @author rbp28668
 */
public class MetaModelViewerItemHandler implements ItemHandler {

    private  StandardDiagramType diagramType;
    private SettingsManager.Element cfg;
    private Application app;
    private Repository repository;

    /**
     * @param diagramType
     */
    public MetaModelViewerItemHandler(StandardDiagramType diagramType, Application app, Repository repository) {
        super();
        this.diagramType = diagramType;
        this.app = app;
        this.repository = repository;
        
        cfg = app.getConfig().getElement("/MetaModelViewer/popups");
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.ItemHandler#editSymbolItem(java.lang.Object)
     */
    public boolean editSymbolItem(Component parent, Object item) {
		MetaEntity me = (MetaEntity)item;
		MetaEntityEditor editor = new MetaEntityEditor(parent, me, repository);
		editor.setVisible(true);
        boolean edited = editor.wasEdited();
        editor.dispose();
        return edited;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.ItemHandler#addSymbolAt(java.awt.Component, int, int)
     */
    public Symbol[] addSymbolsAt(Component parent, int x, int y) throws LogicException {
        Symbol[] symbols = null;
        
		ItemSelectionDialog dlg = new ItemSelectionDialog(parent, "Select Meta-Entities", repository.getMetaModel().getMetaEntities());
		dlg.setVisible(true);
		
		if(dlg.wasEdited()){
		    Collection<MetaEntity> selected =  dlg.getSelectedItems();
		    
		    symbols = new Symbol[selected.size()];
		    int boxSize = (int)(Math.sqrt(selected.size()));
		    int inRow = 0;
		    int startX = x;
		    int idx = 0;
		    MetaModelDiagramType type = MetaModelDiagramType.getInstance();
		    
		    for(MetaEntity metaEntity : selected){
				
				// Need to get the correct symbol type for this entity.
				SymbolType st = type.getSymbolType();
				Symbol symbol = st.newSymbol(metaEntity, x, y);
				symbols[idx++] = symbol;
				// Arbitrarily arrange multiple.
				++inRow;
				x += 50;
				if(inRow > boxSize){
				    inRow = 0;
				    x = startX;
				    y += 20;
				}
		        
		    }
		}
		dlg.dispose();
		return symbols;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.ItemHandler#addConnector(java.awt.Component, alvahouse.eatool.gui.graphical.Symbol, alvahouse.eatool.gui.graphical.Symbol)
     */
    public Connector addConnector(Component parent, Symbol first, Symbol second) throws LogicException {
        
        MetaEntity meFirst = (MetaEntity)first.getItem();
        MetaEntity meSecond = (MetaEntity)second.getItem();
        
        Set<MetaRelationship> cmrFirst = repository.getMetaModel().getMetaRelationshipsFor(meFirst);
        Set<MetaRelationship> cmrSecond = repository.getMetaModel().getMetaRelationshipsFor(meSecond);
        Set<MetaRelationship> allowed = new HashSet<MetaRelationship>();
        allowed.addAll(cmrFirst);
        allowed.retainAll(cmrSecond);
        
        // remove any meta-relationships already connecting these
        // 2 meta-entities.
        Set<Arc> existing = new HashSet<Arc>();
        existing.addAll(first.getArcs());
        existing.retainAll(second.getArcs());
        
        allowed.removeAll(existing);
        
        // intersection should now contain all the MetaRelationships that
        // can join the 2 meta-entities.
        
		KeyedItem selected = null;
		String newOption = "--New--";
		boolean selectNew = false;
		
		if(allowed.isEmpty()){
			int sel = JOptionPane.showConfirmDialog(parent,"Create a new Meta-Relationship?", "EATool", JOptionPane.YES_NO_OPTION);
			if(sel == JOptionPane.YES_OPTION){
				selectNew = true;
			}
		} else {
			Object[] values = new Object[allowed.size() + 1];
			int idx = 0;
			values[idx++] = newOption;
			
			for(MetaRelationship mr : allowed){
				values[idx++] = mr;
			}
			selected = (KeyedItem)JOptionPane.showInputDialog(parent,
			"Select the Meta-Relationship to use", "EATool", JOptionPane.QUESTION_MESSAGE,
			null,values,newOption);
		}
		
		// If the user hasn't selected anything then bail out.
		if(selected == null && !selectNew) {
			return null;
		}
		
		// If the user has selected a new meta-relationship then magic one up
		// and get the user to edit it.
		if(selectNew) { 
		    MetaRelationship mr = new MetaRelationshipImpl(new UUID());
			mr.start().setConnection(meFirst);
			mr.finish().setConnection(meSecond);
		
			MetaRelationshipEditor editor = new MetaRelationshipEditor(parent, mr, repository);
			editor.setVisible(true);
			
			if(!editor.wasEdited()) {
				return null;
			}

			// Only add the new MetaRelationship to the metamodel once the user has confirmed.
			repository.getMetaModel().addMetaRelationship(mr);

			selected = mr;
		} 

		ConnectorType ct = MetaModelDiagramType.getInstance().getConnectorType();
		Connector con = ct.newConnector(new UUID());
		con.setItem(selected);
		return con;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.ItemHandler#getPopupFor(java.lang.Class)
     */
    public PositionalPopup getPopupFor(StandardDiagramViewer viewer, Class<?> targetClass) {
        ActionSet actions = new MetaModelViewerActionSet(viewer,app, repository);
        PositionalPopup popup = GUIBuilder.buildPopup(actions,cfg,targetClass);
       return popup;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.ItemHandler#getBackgroundPopup()
     */
    public PositionalPopup getBackgroundPopup(StandardDiagramViewer viewer) {
        ActionSet actions = new MetaModelViewerActionSet(viewer, app, repository);
        PositionalPopup popup = GUIBuilder.buildPopup(actions,cfg,"Background");
       return popup;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.ItemHandler#addSymbolNewItem(java.awt.Component, int, int)
     */
    public Symbol addSymbolNewItem(Component parent, int x, int y) throws LogicException {
		MetaEntity metaEntity = new MetaEntityImpl(new UUID());
		MetaEntityEditor editor = new MetaEntityEditor(parent, metaEntity, repository);
		editor.setVisible(true);
        boolean edited = editor.wasEdited();
        editor.dispose();
        
        Symbol symbol = null;
        if(edited){
			// Need to get the correct symbol type for this entity.
		    MetaModelDiagramType type = MetaModelDiagramType.getInstance();
			SymbolType st = type.getSymbolType();
			symbol = st.newSymbol(metaEntity, x, y);
			
			repository.getMetaModel().addMetaEntity(metaEntity);
			
        }
        return symbol;
    }

}
