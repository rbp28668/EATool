/**
 *
 * @author  rbp28668
 */

package alvahouse.eatool.util;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.exception.InputException;

public interface IXMLContentHandler {
    public void startElement(String uri, String local, Attributes attrs) throws InputException;
    public void endElement(String uri, String local) throws InputException;
    public void characters(String str) throws InputException;
}

