/*
 * ProgressStatus.java
 * Project: EATool
 * Created on 31-Mar-2006
 *
 */
package alvahouse.eatool.repository;

/**
 * ProgressStatus is an interface to allow the repository to show progress status.
 * 
 * @author rbp28668
 */
public interface ProgressStatus {

    public void setRange(int lower, int upper);
    public void setPosition(int value);
    public void setTask(String name);
    public void setIndeterminate(boolean indeterminate);
    public void setComplete();
}
