/*
 * CSVHierarchyXMLReader.java
 * Project: EATool
 * Created on 16-Jan-2006
 *
 */
package alvahouse.eatool.repository.mapping;

import java.io.IOException;
import java.util.AbstractSequentialList;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import alvahouse.eatool.util.AbstractXMLReader;
import alvahouse.eatool.util.CSVBasicXMLReader;

/**
 * CSVHierarchyXMLReader reads in a hierarchy of names presented as a CSV table.
 * E.g.
 * <pre>
 * A B C D
 * A B C E
 * A B F G
 * A H I J
 * A H I K
 * 
 * Is mapped to:
 *       A
 *      / \
 *     B   H
 *    /\    \
 *   C  F   I
 *  /\  |   /\
 * D  E G  J K
 * </pre>
 * @author rbp28668
 */
public class CSVHierarchyXMLReader extends AbstractXMLReader {

//    /** This is the real ContentHandler that the outside world knows about */
//    private ContentHandler contentHandler;


    /**
     * Creates a reader.
     */
    public CSVHierarchyXMLReader() {
        super();
     }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
     */
    public void parse(InputSource source) throws IOException, SAXException {
        CSVBasicXMLReader reader = new CSVBasicXMLReader();
        Builder builder = new Builder(reader);
        reader.setContentHandler(builder);
        reader.parse(source);
        builder.emit(getContentHandler());
    }

 

    /**
     * Builder is the class that does the main work - it builds a tree of Node
     * in memory and then converts it into SAX events to describe the tree as
     * Entities (of type "entry") and Relationships (of type "hierarchy"). 
     * 
     * @author rbp28668
     */
    private class Builder implements ContentHandler {

        private CSVBasicXMLReader reader;
        private Node root;
        private Node parent;
        private StringBuffer tag = new StringBuffer();
        
        /** an empty attribute for use with SAX */
        private final Attributes EMPTY_ATTR = new AttributesImpl();
        
        
        Builder(CSVBasicXMLReader reader){
            this.reader = reader;
        }
        
        /**
         * Write out the completed tree to another ContentHandler.
         * @param contentHandler is the destination handler.
         * @throws SAXException
         */
        public void emit(ContentHandler contentHandler) throws SAXException {
            contentHandler.startDocument( );
            
            contentHandler.startElement("","","Document",EMPTY_ATTR);

            emitEntities(contentHandler,root);
            emitRelationships(contentHandler,root);
            
            contentHandler.endElement("","","Document");
            contentHandler.endDocument( );
            
        }

        /**
         * This emits all the parent-child relationships by walking the node tree.
         * @param contentHandler is the destination handler.
         * @param node is the Node that is the current root of the tree to emit.
         * @throws SAXException
         */
        private void emitRelationships(ContentHandler contentHandler, Node node) throws SAXException {
            
            AttributesImpl attrs = new AttributesImpl();

            for(Node child : node.getChildren()){
                
                attrs.clear();
	            attrs.addAttribute("","","type","CDATA","hierarchy");
	            contentHandler.startElement("","","Relationship",attrs);
	
	            attrs.clear();
	            attrs.addAttribute("","","type","CDATA","parent");
	            contentHandler.startElement("","","Role",attrs);
	            attrs.clear();
	            attrs.addAttribute("","","type","CDATA","name");
	            attrs.addAttribute("","","value","CDATA",node.getName());
	            contentHandler.startElement("","","PropertyKey",attrs);
	            contentHandler.endElement("","","PropertyKey");
	            contentHandler.endElement("","","Role");
	
	            attrs.clear();
	            attrs.addAttribute("","","type","CDATA","child");
	            contentHandler.startElement("","","Role",attrs);
	            attrs.clear();
	            attrs.addAttribute("","","type","CDATA","name");
	            attrs.addAttribute("","","value","CDATA",child.getName());
	            contentHandler.startElement("","","PropertyKey",attrs);
	            contentHandler.endElement("","","PropertyKey");
	            contentHandler.endElement("","","Role");
	            
	            contentHandler.endElement("","","Relationship");

                emitRelationships(contentHandler,child);
            }
            
            
        }

        /**
         * Emits the parent/child relationships inherent in the hierarchy.
         * @param contentHandler is the destination handler.
         * @param node is the root node that should be emitted.
         * @throws SAXException
         */
        private void emitEntities(ContentHandler contentHandler, Node node) throws SAXException {
            AttributesImpl attrs = new AttributesImpl();

            attrs.addAttribute("","","type","CDATA","entry");
            contentHandler.startElement("","","Entity",attrs);

            attrs.clear();
            attrs.addAttribute("","","type","CDATA","name");
            attrs.addAttribute("","","value","CDATA",node.getName());
            contentHandler.startElement("","","Property",attrs);
            contentHandler.endElement("","","Property");
            
            contentHandler.endElement("","","Entity");

            for(Node child : node.getChildren()){
                emitEntities(contentHandler,child);
            }
        }
        
        

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
         */
        public void setDocumentLocator(Locator arg0) {
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#startDocument()
         */
        public void startDocument() throws SAXException {
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#endDocument()
         */
        public void endDocument() throws SAXException {
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
         */
        public void startPrefixMapping(String arg0, String arg1) throws SAXException {
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
         */
        public void endPrefixMapping(String arg0) throws SAXException {
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        public void startElement(String uri, String local, String raw, Attributes attrs) throws SAXException {
            if(reader.isFileTag(raw)){
                root = new Node("root");
            } else if(reader.isLineTag(raw)){
                parent = root;
            } else if(reader.isValueTag(raw)){
                tag.delete(0,tag.length());
            }
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        public void endElement(String uri, String local, String raw) throws SAXException {
            
            // Process each value by descending in the tree, creating new
            // nodes if needed.  The parent node is reset to root at the
            // start of each line.
            if(reader.isValueTag(raw)){
                String value = tag.toString();
                Node node = parent.find(value);
                if(node == null){
                    node = new Node(value);
                    parent.addChild(node);
                }
                parent = node;
            }
        }


        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#characters(char[], int, int)
         */
        public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
            tag.append(arg0,arg1,arg2);
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
         */
        public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
         */
        public void processingInstruction(String arg0, String arg1) throws SAXException {
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
         */
        public void skippedEntity(String arg0) throws SAXException {
        }
        
    }
    
    /**
     * Node is a simple tree node for building up the hierarchy of values.
     * 
     * @author rbp28668
     */
    private class Node {
        
        private String name;
        private LinkedList<Node> children = new LinkedList<Node>();
        
        Node(String name){
            this.name = name;
        }

        /**
         * Gets the node name that is the value for this node.
         * @return the node name.
         */
        String getName() {
            return name;
        }

        /**
         * Gets the list of child nodes (if any) of this node.
         * @return a list of nodes, never null, maybe empty.
         */
        public AbstractSequentialList<Node> getChildren() {
            return children;
        }

        /**
         * Adds a child node to the end of the list.
         * @param node is the node to add.
         */
        public void addChild(Node node) {
            children.addLast(node);
        }

        /**
         * Looks for a child node with the given value.
         * @param value is the String value to look for.
         * @return the node with the given value or <code>null</code> if not found.
         */
        public Node find(String value) {
            for(Node child : children){
                if(child.name.equals(value)){
                    return child;
                }
            }
            return null;
        }
    }
}
