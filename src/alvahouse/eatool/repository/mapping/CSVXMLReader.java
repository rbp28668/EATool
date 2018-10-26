/*
 * CSVXMLReader.java
 * Project: EATool
 * Created on 16-Jan-2006
 *
 */
package alvahouse.eatool.repository.mapping;

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import alvahouse.eatool.util.AbstractXMLReader;
import alvahouse.eatool.util.CSVBasicXMLReader;

/**
 * CSVXMLReader reads in a CSV file converting it into SAX events that provide
 * the correct XML for the import routines.
 * 
 * @author rbp28668
 */
public class CSVXMLReader extends AbstractXMLReader {

    /**
     * 
     */
    public CSVXMLReader() {
        super();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
     */
    public void parse(InputSource source) throws IOException, SAXException {
        CSVBasicXMLReader reader = new CSVBasicXMLReader();
        ContentFilter filter = new ContentFilter(reader, getContentHandler());
        reader.setContentHandler(filter);
        reader.parse(source);
    }
    
    /**
     * ContentFilter filters the SAXEvents to convert the raw CSV generated
     * events into the format of XML required by the import routines.
     * 
     * @author rbp28668
     */
    private class ContentFilter implements ContentHandler {

        /** The source CSVBasicXMLReader needed to distingish file/line/value tags */
        private CSVBasicXMLReader reader;
        
        /** String buffer to build up the value strings */
        private StringBuffer tag = new StringBuffer();
        
        /** Destination handler for output events */
        private ContentHandler handler;
        
        /** an empty attribute for use with SAX */
        private final Attributes EMPTY_ATTR = new AttributesImpl();
        
        
        /**
         * Create a ContentFilter.
         * @param reader is the CSVBasicXMLReader providing the input.
         * @param contentHandler is the ContentHandler that receives the output.
         */
        ContentFilter(CSVBasicXMLReader reader, ContentHandler contentHandler){
            this.reader = reader;
            this.handler = contentHandler;
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
         */
        public void setDocumentLocator(Locator arg0) {
            handler.setDocumentLocator(arg0);
        }


        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#startDocument()
         */
        public void startDocument() throws SAXException {
            handler.startDocument();
        }


        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#endDocument()
         */
        public void endDocument() throws SAXException {
            handler.endDocument();
        }


        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
         */
        public void startPrefixMapping(String arg0, String arg1) throws SAXException {
            handler.startPrefixMapping(arg0,arg1);
        }


        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
         */
        public void endPrefixMapping(String arg0) throws SAXException {
            handler.endPrefixMapping(arg0);
        }


        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
            AttributesImpl outAttrs = new AttributesImpl();
            
            if(reader.isFileTag(qName)){
                handler.startElement("","","Document",EMPTY_ATTR);
            } else if(reader.isLineTag(qName)){
                outAttrs.addAttribute("","","type","CDATA","entry");
                handler.startElement("","","Entity",outAttrs);
            } else if(reader.isValueTag(qName)){
                tag.delete(0,tag.length());
            }
        }


        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            if(reader.isFileTag(qName)){
                handler.endElement("","","Document");
            } else if(reader.isLineTag(qName)){
                handler.endElement("","","Entity");
            } else if(reader.isValueTag(qName)){
                AttributesImpl attrs = new AttributesImpl();
                attrs.addAttribute("","","type","CDATA",qName);
                attrs.addAttribute("","","value","CDATA",tag.toString());
                handler.startElement("","","Property",attrs);
                handler.endElement("","","Property");
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
            handler.ignorableWhitespace(arg0,arg1,arg2);
        }


        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
         */
        public void processingInstruction(String arg0, String arg1) throws SAXException {
            handler.processingInstruction(arg0,arg1);
        }


        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
         */
        public void skippedEntity(String arg0) throws SAXException {
            handler.skippedEntity(arg0);
        }
    }


}
