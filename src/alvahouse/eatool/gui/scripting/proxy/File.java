/*
 * File.java
 * Project: EATool
 * Created on 24-Apr-2007
 *
 */
package alvahouse.eatool.gui.scripting.proxy;

import java.awt.EventQueue;
import java.awt.FileDialog;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;


/**
 * File creates an output file for writing (reports) to disk.
 * 
 * @author rbp28668
 */
public class File {

    private PrintWriter writer = null;
    private alvahouse.eatool.Application app;
    /**
     * 
     */
    File(alvahouse.eatool.Application app) {
        super();
        this.app = app;
    }
    
    /**
     * Opens the file.
     * @param path is the path to the file to create.
     * @throws FileNotFoundException
     */
     void open(String path) throws FileNotFoundException{
        if(writer != null){
            throw new IllegalStateException("File is already open");
        }
        FileOutputStream fos = new FileOutputStream(path);
        writer = new PrintWriter(fos);
    }

     /**
      * Allows the user to specify the file to create.
      * @param title
      * @throws InterruptedException
      * @throws InvocationTargetException
      * @throws FileNotFoundException
      */
     void choose(String title) throws InterruptedException, InvocationTargetException, FileNotFoundException{
         if(writer != null){
             throw new IllegalStateException("File is already open");
         }
         
         FileDialog dialog = new FileDialog(app.getCommandFrame(), title, FileDialog.SAVE);
         EventQueue.invokeAndWait( new DialogRunner(dialog));
         String fileName = dialog.getFile();
    	 String dir = dialog.getDirectory();
         if(fileName != null && dir != null){
        	 java.io.File file = new java.io.File(dir,fileName);
             FileOutputStream fos = new FileOutputStream(file);
             writer = new PrintWriter(fos);
         }
     }
     
     private class DialogRunner implements Runnable {

         private FileDialog dialog;
         
         DialogRunner(FileDialog dialog){
             this.dialog = dialog;
         }
         
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         public void run() {
             dialog.setVisible(true);
         }
     }
     
    /**
     * Closes the file.
     */
    public void close() {
        if(writer == null){
            throw new IllegalStateException("File is not open");
        }
        writer.close();
        writer = null;
    }
    
    /**
     * Determines whether the file is open.
     * @return true if open, false if not.
     */
    public boolean isOpen(){
        return writer != null;
    }
    
    /**
     * Prints the given text without a terminating linefeed.
     * @param text is the text to be printerd.
     */
    public void print(String text){
        if(writer == null){
            throw new IllegalStateException("File is not open");
        }
        writer.print(text);
    }
    
    /**
     * Prints the given text with a terminating linefeed.
     * @param text is the text to be printerd.
     */
    public void println(String text){
        if(writer == null){
            throw new IllegalStateException("File is not open");
        }
        writer.println(text);
    }
    
    /**
     * Convenience method to print a page of HTMLProxy.
     * @param html is the HTMLProxy to print.
     */
    public void printHTML(HTMLProxy html){
        writer.println(html.getPage());
    }
    

}


    

