/*
 * ProgressCounter.java
 * Project: EATool
 * Created on 07-Mar-2007
 *
 */
package alvahouse.eatool.repository;

/**
 * ProgressCounter allows a process to count the number of things of a particular
 * type.  For example, when reading in data.
 * 
 * @author rbp28668
 */
public interface ProgressCounter {
    public void count(String type);
}
