/*
 * HTMLDisplayProxy
 * Project: EATool
 * Created on 24-Feb-2006
 *
 */
package alvahouse.eatool.gui.html;

import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.ExplorerTreeModel;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.html.HTMLPage;
import alvahouse.eatool.repository.images.Images;
import alvahouse.eatool.util.SettingsManager;

/**
 * HTMLDisplayProxy is a HTMLProxy editor for setting up custom screens/frameworks.
 * 
 * @author rbp28668
 */
public class HTMLEditor extends JInternalFrame {

    private static final long serialVersionUID = 1L;
    private DisplayPane display;
    private EditorPane editor;
    private TreePane tree;
    private final HTMLEditorActionSet actions;
    private HTMLPage page;
    private Application app;
    private Images images;
    private boolean edited = false;
    private Completion completion;
    private final static String WINDOW_SETTINGS = "/Windows/HTMLEditor";
	private static final String MENU_CONFIG = "/HTMLEditor/menus";
    
    /**
     * Creates a new, empty browser.
     */
    public HTMLEditor(Application app,  Repository repository){
        this.app = app;
        this.images = repository.getImages();
        
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);

        JTabbedPane tabs = new JTabbedPane();
        
        display = new DisplayPane();
        JScrollPane scrollPane = new JScrollPane(display);
        tabs.add("HTMLProxy",scrollPane);
        
        editor = new EditorPane();
 
        scrollPane = new JScrollPane(editor);
        tabs.add("Text",scrollPane);
        
        tree = new TreePane();
        scrollPane = new JScrollPane(tree);
        tabs.add("Tree",scrollPane);
        
        actions = new HTMLEditorActionSet(this, repository);
        
        
        JMenuBar menuBar = new JMenuBar();
        SettingsManager config = app.getConfig();
        SettingsManager.Element cfg = config.getElement(MENU_CONFIG);
        GUIBuilder.buildMenuBar(menuBar, actions, cfg);
        setJMenuBar(menuBar);
        
        updateKeymap(editor);

        getContentPane().add(tabs, java.awt.BorderLayout.CENTER);
        
        Document doc = editor.getDocument();
        doc.addDocumentListener(display);
        
        doc = display.getDocument();
        doc.addDocumentListener(tree);
        
    }
    
     
    public void dispose() {
        GUIBuilder.saveBounds(this,WINDOW_SETTINGS, app);
        app.getWindowCoordinator().removeFrame(this);
        
        if(edited && completion != null) {
        	try {
        		completion.onCompletion(page);
        	} catch (Exception e) {
        		new ExceptionDisplay(app.getCommandFrame(),e);
        	}
        }

        super.dispose();
    }

 
    public void setPage(HTMLPage page){
        this.page = page;
        editor.setText(page.getHtml());
    }
    
    public void setText(String html) throws IOException{
        display.setText(html);
    }
    
    /**
     * Sets a handler to be called when editing is complete.
     * @param completion
     */
    public void onCompletion(Completion completion) {
    	if(completion == null) {
    		throw new NullPointerException("Can't have a null completion");
    	}
    	this.completion = completion;
    }
    
    
    /**
     * @param editor
     */
    private void updateKeymap(EditorPane editor) {
        Keymap map = JTextComponent.addKeymap("NextPrevMap",editor.getKeymap());
        bindKey(map, KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK, DefaultEditorKit.nextWordAction);
        bindKey(map, KeyEvent.VK_LEFT,  InputEvent.CTRL_MASK, DefaultEditorKit.previousWordAction);
        bindKey(map, KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK|InputEvent.SHIFT_MASK, DefaultEditorKit.selectionNextWordAction);
        bindKey(map, KeyEvent.VK_LEFT,  InputEvent.CTRL_MASK|InputEvent.SHIFT_MASK, DefaultEditorKit.selectionPreviousWordAction);
    }

    /**
     * Binds an Action to a keystroke in the given Keymap.
     * @param map is the Keymap to add the binding to.
     * @param key is the virtual key code to make up the keystroke.
     * @param mask is the keymask to make up the keystroke.
     * @param actionName is the name of the action to bind.
     */
    private void bindKey(Keymap map, int key, int mask, String actionName) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(key,mask, false);
        map.addActionForKeyStroke(keyStroke,actions.getAction(actionName));
    }
    
    private class DisplayPane extends JEditorPane implements DocumentListener{
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
	                           
	                       }
	                   }
                   } catch (Exception e){
                       new ExceptionDisplay(HTMLEditor.this,e);
                   }
                   
               }
            });

            HTMLEditorKit kit = new HTMLEditorKit() {
                private static final long serialVersionUID = 1L;
                public ViewFactory getViewFactory() {
                   return new HTMLFactory() {
                      public View create(Element elem) {
                          View view = null;
                          if(elem.getName().equals("img")){
                       		  view = new LocalImageView(elem,images);
                          } else {
 	                        view = super.create(elem);
                          }
                         return view;
                      }
                   };
                }
             };
             setEditorKit(kit);

        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
         */
        public void insertUpdate(DocumentEvent arg0) {
            handleEvent(arg0);
        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
         */
        public void removeUpdate(DocumentEvent arg0) {
            handleEvent(arg0);
        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
         */
        public void changedUpdate(DocumentEvent arg0) {
            handleEvent(arg0);
        }
        
        void handleEvent(DocumentEvent event) {
            Document doc = event.getDocument();
            try {
                String text = doc.getText(0,doc.getLength());
                setText(text);
            } catch (BadLocationException e) {
                // NOP
            }
        }


    }
    
    private class EditorPane extends JEditorPane{
        private static final long serialVersionUID = 1L;
        public EditorPane() {
            super();
            setEditable(true);
            setContentType("text/plain");
            
            Font font = new Font("Lucida Console", Font.PLAIN,12);
            setFont(font);
        }
        
    }
    
    private class TreePane extends JTree implements DocumentListener{
        private static final long serialVersionUID = 1L;
        
        private HTMLTreeModel model = new HTMLTreeModel();
        
        TreePane(){
            setModel(model);
        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
         */
        public void insertUpdate(DocumentEvent arg0) {
            handleEvent(arg0);
        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
         */
        public void removeUpdate(DocumentEvent arg0) {
            handleEvent(arg0);
        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
         */
        public void changedUpdate(DocumentEvent arg0) {
            handleEvent(arg0);
        }

        
        void handleEvent(DocumentEvent event) {
            model.update(event.getDocument());
        }
    }
    
    private class HTMLTreeModel extends ExplorerTreeModel {
        private static final long serialVersionUID = 1L;
        HTMLTreeModel(){
            super("Document");
        }
        
        void update(Document doc){
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)getRoot();
            root.removeAllChildren();
            
            Element[] docRoots = doc.getRootElements();
            int idx = 0;
            for(int i=0; i<docRoots.length; ++i){
                idx = addElement(docRoots[i],root,idx);
            }
            reload();
        }

        /**
         * @param element
         * @param root
         * @param idx
         */
        private int addElement(Element element, DefaultMutableTreeNode parent, int idx) {

            try {

                
	            String name = element.getName();
	            if(name.startsWith("bidi")){
	                return idx;
	            }
	                
                String nodeValue = name;
                
                if(element.isLeaf()){
                    int start = element.getStartOffset();
                    int end = element.getEndOffset();
                    Document doc = element.getDocument();
                    nodeValue = doc.getText(start,end-start); 
                    if(nodeValue.trim().length() == 0){
                        return idx;
                    }
                }
                DefaultMutableTreeNode tnElement = new DefaultMutableTreeNode(nodeValue);
                insertNodeInto(tnElement,parent,idx++);
                registerNode(tnElement,element);
                addElementChildren(element,tnElement);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
            return idx;
        }

        /**
         * @param element
         * @param tnElement
         */
        private void addElementChildren(Element element, DefaultMutableTreeNode tnElement) {
            AttributeSet attrs = element.getAttributes();
            int count = attrs.getAttributeCount();
            int idx = 0;
            if(count > 0){
                DefaultMutableTreeNode tnAttributes = new DefaultMutableTreeNode("Attributes");
                insertNodeInto(tnAttributes,tnElement,idx++);
                
                int idxAttr = 0;
                Enumeration<?> ae = attrs.getAttributeNames();
                while(ae.hasMoreElements()){
                    Object name = ae.nextElement();
                    Object value = attrs.getAttribute(name);
                    
                    String text = name.toString() + ":" + value.toString();
                    DefaultMutableTreeNode tnAttribute = new DefaultMutableTreeNode(text);
                    insertNodeInto(tnAttribute,tnAttributes,idxAttr++);
                }
            }
            
            count = element.getElementCount();
            for(int i=0;i<count; ++i){
                Element child = element.getElement(i);
                addElement(child,tnElement,idx++);
            }
            
        }
    }

    /**
     * Updates the editor title from the page name.
     */
    public void updateTitle() {
        if(page != null){
            setTitle(page.getName());
        }
    }


    /**
     * Gets the current HTMLProxy page - note that this won't be updated
     * from the editor per se unless updatePage() is called.
     * @return the current HTMLPage.
     */
    public HTMLPage getPage() {
        return page;
    }


    /**
     * Update the current HTMLPage from the edited text.
     */
    public void updatePage() {
        page.setHtml(editor.getText());
        edited = true;
    }
    
    /**
     * @return
     */
    public boolean wasEdited() {
    	return edited;
    }
    
    /**
     * Callback interface to allow caller to specify what to do when the HTML Page is saved.
     * @author bruce_porteous
     *
     */
    public interface Completion {
    	void onCompletion(HTMLPage page);
    }

	/**
	 * Marks the page as edited.
	 */
	public void setModified() {
		edited = true;
	}

 
}
