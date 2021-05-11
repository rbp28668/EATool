/*
 * ExportProxy.java
 * Project: EATool
 * Created on 10-May-2006
 *
 */
package alvahouse.eatool.webexport;

import alvahouse.eatool.util.XMLWriter;

/**
 * ExportProxy
 * 
 * @author rbp28668
 */
public interface ExportProxy {

    public void export(XMLWriter out)  throws Exception;
    
}
