/*
 * LoadProgress.java
 * Project: EATool
 * Created on 08-Mar-2007
 *
 */
package alvahouse.eatool.repository;


/**
 * LoadProgress counts the number of "things" happening and updates a
 * ProgressStatus as necessary.
 * 
 * @author rbp28668
 */
public class LoadProgress implements ProgressCounter {

    private ProgressStatus progress;
    private String currentTask = null;
    private int count = 0;
    private int chunk = 0;
    private final int GRANULARITY = 10;
    
    public LoadProgress(ProgressStatus progress){
        this.progress = progress;
    }
    

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.ProgressCounter#count(java.lang.String)
     */
    public void count(String type) {
        ++count;
        ++chunk;
        if(chunk == GRANULARITY){
            chunk = 0;
            progress.setPosition(count);
        }
        
        if(type != currentTask){
            progress.setTask("Loading " + type);
            currentTask = type;
            progress.setPosition(count);
        }
        
    }
    
    public ProgressStatus getStatus(){
        return progress;
    }
}