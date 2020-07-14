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

import alvahouse.eatool.scripting.proxy.Scripted;


/**
 * File creates an output file for writing (reports) to disk.
 * 
 * @author rbp28668
 */
@Scripted(description="An output file for writing (reports) to disk")
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
     @Scripted(description="Closes the file. This should be called when all the output has been written.")
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
     @Scripted(description="Determines whether the file is open.")
    public boolean isOpen(){
        return writer != null;
    }
    
    /**
     * Prints the given text without a terminating linefeed.
     * @param text is the text to be printerd.
     */
     @Scripted(description="Prints the given text without a terminating linefeed.")
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
     @Scripted(description="Prints the given text with a terminating linefeed.")
    public void println(String text){
        if(writer == null){
            throw new IllegalStateException("File is not open");
        }
        writer.println(text);
    }
 
     /**
      * Outputs a linefeed.  Convenience method, saves having to do println('');
      */
      @Scripted(description="Outputs a linefeed.")
     public void println(){
         if(writer == null){
             throw new IllegalStateException("File is not open");
         }
         writer.println();
     }
  
    /**
     * Convenience method to print a page of HTMLProxy.
     * @param html is the HTMLProxy to print.
     */
     @Scripted(description="Output the contents of a HTML object")
    public void printHTML(HTMLProxy html){
        writer.println(html.getPage());
    }
    

}


    

