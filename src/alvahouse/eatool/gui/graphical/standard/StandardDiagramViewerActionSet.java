/*
 * StandardDiagramViewerActionSet.java
 * Created on 17-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.gui.graphical.standard;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.PositionalPopup;
import alvahouse.eatool.gui.graphical.DiagramViewerActionSet;
import alvahouse.eatool.gui.graphical.layout.BoxHierarchyLayoutStrategy;
import alvahouse.eatool.gui.graphical.layout.CircularHierarchyLayoutStrategy;
import alvahouse.eatool.gui.graphical.layout.FlexAnnealLayoutStrategy;
import alvahouse.eatool.gui.graphical.layout.GridAnnealLayoutStrategy;
import alvahouse.eatool.gui.graphical.layout.GridConnectivityLayoutStrategy;
import alvahouse.eatool.gui.graphical.layout.GridLayoutStrategy;
import alvahouse.eatool.gui.graphical.layout.HierarchyLayoutStrategy;
import alvahouse.eatool.gui.graphical.layout.IGraphicalLayoutStrategy;
import alvahouse.eatool.gui.graphical.layout.SpringLayoutStrategy;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.GraphicalObject;
import alvahouse.eatool.repository.graphical.standard.ImageDisplay;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.graphical.standard.Symbol;
import alvahouse.eatool.repository.graphical.standard.TextBox;
import alvahouse.eatool.repository.images.Image;
import alvahouse.eatool.util.UUID;

/**
 * StandardDiagramViewerActionSet
 * @author Bruce.Porteous
 *
 */
public class StandardDiagramViewerActionSet extends DiagramViewerActionSet {

    private Repository repository;
	/**
	 * 
	 */
	public StandardDiagramViewerActionSet(StandardDiagramViewer viewer, Application app, Repository repository) {
		super(viewer, app.getSettings(), repository);
		this.repository = repository;
		
		addAction("AddNewItem", actionAddNewItem);
		addAction("AddSymbols", actionAddSymbols);
		addAction("AddTextBox", actionAddTextBox);
		addAction("AddImage", actionAddImage);

		// Edit
		addAction("ResizeAllSymbols",actionResizeAllSymbols);
        addAction("SymbolResize", actionSymbolResize);
        addAction("SymbolEdit", actionSymbolEdit);

		// Layout
		addAction("GridLayout", actionGridLayout);
		addAction("GridConnectLayout", actionGridConnectLayout);
		addAction("HierarchyLayout", actionHierarchyLayout);
		addAction("CircularHierarchyLayout", actionCircularHierarchyLayout);
		addAction("BoxHierarchyLayout", actionBoxHierarchyLayout);
		addAction("SpringLayout", actionSpringLayout);
		addAction("GridAnnealLayout", actionGridAnnealLayout);
		addAction("FlexAnnealLayout", actionFlexAnnealLayout);

        // Text box popup
        addAction("TextBoxEdit",actionTextBoxEdit);
        addAction("TextBoxSettings",actionTextBoxSettings);
        addAction("TextBoxDelete", actionTextBoxDelete);

        // Image  popup
        addAction("ImageDelete", actionImageDelete);
        
	}

	
	/** Add a new item- generic as uses ItemHandler to create the object
	 * that is to have a symbol added */
	private final Action actionAddNewItem = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
	        StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
		    try{
		        int x = 0;
		        int y = 0;
		        Object source = e.getSource();
		        if(source != null && source instanceof Component){
		            Container parent = ((Component)source).getParent();
		            if(parent instanceof PositionalPopup){
		                PositionalPopup popup = (PositionalPopup)parent;
		                
		                x = popup.getTargetX();
		                y = popup.getTargetY();
		            }
		        }

			    ItemHandler handler = viewer.getItemHandler();
			    Symbol symbol = handler.addSymbolNewItem(getViewer(),x,y);

				if(symbol != null){
				    StandardDiagram diagram = (StandardDiagram)viewer.getDiagram();
			        diagram.addSymbol(symbol);
				    viewer.repaint();
				}
			    
			} catch(Exception ex) {
				new ExceptionDisplay(viewer,ex);
			}
		}
	};

	/** Add selected items - generic as uses ItemHandler to get the objects
	 * that are to have symbols added */
	private final Action actionAddSymbols = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
	        StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
		    try{
		        int x = 0;
		        int y = 0;
		        Object source = e.getSource();
		        if(source != null && source instanceof Component){
		            Container parent = ((Component)source).getParent();
		            if(parent instanceof PositionalPopup){
		                PositionalPopup popup = (PositionalPopup)parent;
		                
		                x = popup.getTargetX();
		                y = popup.getTargetY();
		            }
		        }

			    ItemHandler handler = viewer.getItemHandler();
			    Symbol[] symbols = handler.addSymbolsAt(getViewer(),x,y);

				if(symbols != null){
				    for(int i=0; i<symbols.length; ++i){
					    StandardDiagram diagram = (StandardDiagram)viewer.getDiagram();
				        diagram.addSymbol(symbols[i]);
				    }
				    viewer.repaint();
				}
			    
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		}
	};

	/** Add Text Box action */
	private final Action actionAddTextBox = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    TextBox box = new TextBox(new UUID());
			    TextBoxDialog dialog = new TextBoxDialog(box,getViewer(),"Text Box");
			    dialog.setVisible(true);
			    if(dialog.wasEdited()){
			        
			        int x = 0;
			        int y = 0;
			        Object source = e.getSource();
			        if(source != null && source instanceof Component){
			            Container parent = ((Component)source).getParent();
			            if(parent instanceof PositionalPopup){
			                PositionalPopup popup = (PositionalPopup)parent;
			                
			                x = popup.getTargetX();
			                y = popup.getTargetY();
			            }
			        }

	                StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
					StandardDiagram diagram = (StandardDiagram)viewer.getDiagram();
					float zoom = viewer.getZoom();
					box.setPosition(x/zoom,y/zoom);
					diagram.addTextBox(box);
					viewer.refresh();
			    }
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		}
	};   

	/** Add Image action */
	private final Action actionAddImage = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
                StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();

			    Image image = (Image)Dialogs.selectNRIFrom(repository.getImages().getImages(),"Select image",viewer);
			    if(image != null){
				    ImageDisplay imageDisplay = new ImageDisplay(new UUID());
				    imageDisplay.setImage(image);
			        int x = 0;
			        int y = 0;
			        Object source = e.getSource();
			        if(source != null && source instanceof Component){
			            Container parent = ((Component)source).getParent();
			            if(parent instanceof PositionalPopup){
			                PositionalPopup popup = (PositionalPopup)parent;
			                
			                x = popup.getTargetX();
			                y = popup.getTargetY();
			            }
			        }

					float zoom = viewer.getZoom();
					imageDisplay.setPosition(x/zoom,y/zoom);

					StandardDiagram diagram = (StandardDiagram)viewer.getDiagram();
					diagram.addImage(imageDisplay);
					viewer.refresh();
			    }
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		}
	};   
	
	/** Resize all symbols - resets all symbol sizes to their default determined
	 * by the symbol's content */
	private final Action actionResizeAllSymbols = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    try{
		        StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
			    StandardDiagram diagram = (StandardDiagram)viewer.getDiagram();
			    Collection symbols = diagram.getSymbols();
			    for(Iterator iter = symbols.iterator(); iter.hasNext();){
			        Symbol symbol = (Symbol)iter.next();
			        symbol.mustSizeSymbol(true);
			    }
			    viewer.repaint();
			    
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		}
	};

	/** Edit a symbol */
	private final Action actionSymbolEdit = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
           try {
               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
               Symbol symbol = viewer.getSelectedSymbol();
               if(symbol != null) {
                   TextObjectSettingsDialog editor;
	               (editor = new TextObjectSettingsDialog(symbol, getViewer(), "Edit Symbol")).setVisible(true);
	               editor.dispose();
               }
            } catch(Throwable t) {
                new ExceptionDisplay(getViewer(),t);
            }
		}
	};

	/** Resize selected symbol to its default size */
	private final Action actionSymbolResize = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
           try {
               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
               Symbol symbol = viewer.getSelectedSymbol();
               if(symbol != null) {
                   symbol.mustSizeSymbol(true);
                   viewer.refresh();
               }
            } catch(Throwable t) {
                new ExceptionDisplay(getViewer(),t);
            }
		}
	};

	/** Grid Layout action */
	private final Action actionGridLayout = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
			try {
				IGraphicalLayoutStrategy strategy = new GridLayoutStrategy();
				viewer.setLayoutStrategy(strategy);
				viewer.layoutDiagram();
			} catch(Exception ex) {
				new ExceptionDisplay(viewer,ex);
			}
		}
	};   
	
	/** Grid Connect Layout action */
	private final Action actionGridConnectLayout = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
			try {
				IGraphicalLayoutStrategy strategy = new GridConnectivityLayoutStrategy();
				viewer.setLayoutStrategy(strategy);
				viewer.layoutDiagram();
				
			} catch(Exception ex) {
				new ExceptionDisplay(viewer,ex);
			}
		}
	};   
	
	/** Run a hierarchy layout */
	private final Action actionHierarchyLayout = new AbstractAction() {
	    StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
		public void actionPerformed(ActionEvent e) {
			try {
				IGraphicalLayoutStrategy strategy = new HierarchyLayoutStrategy();
				viewer.setLayoutStrategy(strategy);
				viewer.layoutDiagram();
			} catch(Exception ex) {
				new ExceptionDisplay(viewer,ex);
			}
		}
	};
	/** Add entities from selection dialog */
	private final Action actionCircularHierarchyLayout = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
			try {
				IGraphicalLayoutStrategy strategy = new CircularHierarchyLayoutStrategy();
				viewer.setLayoutStrategy(strategy);
				viewer.layoutDiagram();
			} catch(Exception ex) {
				new ExceptionDisplay(viewer,ex);
			}
		}
	};
	
	
	private final Action actionBoxHierarchyLayout = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
		    StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
			try {
				IGraphicalLayoutStrategy strategy = new BoxHierarchyLayoutStrategy();
				viewer.setLayoutStrategy(strategy);
				viewer.layoutDiagram();
			} catch(Exception ex) {
				new ExceptionDisplay(viewer,ex);
			}
		}
	};   

	/** Spring Layout action */
	private final Action actionSpringLayout = new AbstractAction() {
	    StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
		public void actionPerformed(ActionEvent e) {
			try {
				IGraphicalLayoutStrategy strategy = new SpringLayoutStrategy();
				viewer.setLayoutStrategy(strategy);
				viewer.layoutDiagram();
			} catch(Exception ex) {
				new ExceptionDisplay(viewer,ex);
			}
		}
	};   

	/** Spring Layout action */
	private final Action actionGridAnnealLayout = new AbstractAction() {
	    StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
		public void actionPerformed(ActionEvent e) {
			try {
				IGraphicalLayoutStrategy strategy = new GridAnnealLayoutStrategy();
				viewer.setLayoutStrategy(strategy);
				viewer.layoutDiagram();
			} catch(Exception ex) {
				new ExceptionDisplay(viewer,ex);
			}
		}
	};   

	/** Spring Layout action */
	private final Action actionFlexAnnealLayout = new AbstractAction() {
	    StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
		public void actionPerformed(ActionEvent e) {
			try {
				IGraphicalLayoutStrategy strategy = new FlexAnnealLayoutStrategy();
				viewer.setLayoutStrategy(strategy);
				viewer.layoutDiagram();
			} catch(Exception ex) {
				new ExceptionDisplay(viewer,ex);
			}
		}
	};   
	
	/** allows the user to edit the text in a text box */
	private final Action actionTextBoxEdit = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
	           try {
	               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
	               GraphicalObject selected = viewer.getSelectedObject();
	               if(selected != null && selected instanceof TextBox){
	                   TextBoxDialog dialog = new TextBoxDialog((TextBox)selected, viewer,"Edit text box");
	                   dialog.setVisible(true);
	                   if(dialog.wasEdited()){
		                   viewer.refresh();
	                   }
	               }
	            } catch(Throwable t) {
	                new ExceptionDisplay(getViewer(),t);
	            }
		}
	};

	/** allows the user to edit the settings (colour and font) of a text box */
	private final Action actionTextBoxSettings = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
	           try {
	               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
	               GraphicalObject selected = viewer.getSelectedObject();
	               if(selected != null && selected instanceof TextBox){
	                   TextObjectSettingsDialog dialog = new TextObjectSettingsDialog((TextBox)selected, viewer,"Edit text box");
	                   dialog.setVisible(true);
	                   if(dialog.wasEdited()){
		                   viewer.refresh();
	                   }
	               }
	            } catch(Throwable t) {
	                new ExceptionDisplay(getViewer(),t);
	            }
		}
	};

	/** allows the user to delete the selected text box */
	private final Action actionTextBoxDelete = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
	           try {
	               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
	               GraphicalObject selected = viewer.getSelectedObject();
	               if(selected != null && selected instanceof TextBox){
	                   if(Dialogs.question(viewer,"Delete text box?")){
		                   StandardDiagram diagram = (StandardDiagram)viewer.getDiagram();
		                   diagram.deleteTextBox((TextBox)selected);
		                   viewer.refresh();
	                   }
	               }
	            } catch(Throwable t) {
	                new ExceptionDisplay(getViewer(),t);
	            }
		}
	};

	/** allows the user to delete the selected image */
	private final Action actionImageDelete = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
	           try {
	               StandardDiagramViewer viewer = (StandardDiagramViewer)getViewer();
	               GraphicalObject selected = viewer.getSelectedObject();
	               if(selected != null && selected instanceof ImageDisplay){
	                   if(Dialogs.question(viewer,"Remove Image?")){
		                   StandardDiagram diagram = (StandardDiagram)viewer.getDiagram();
		                   diagram.deleteImage((ImageDisplay)selected);
		                   viewer.refresh();
	                   }
	               }
	            } catch(Throwable t) {
	                new ExceptionDisplay(getViewer(),t);
	            }
		}
	};
	
}



