/*
 * TimeSeriesType.java
 * Project: EATool
 * Created on 10-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.types;

import java.awt.Component;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.StringTokenizer;

import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.ClassUtils;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * TimeSeriesType described a type that defines a time series.  The definition of a time series 
 * consists of a list of named states or intervals.  The time data itself consists of the
 * dates/times of the transitions between the intervals and the start/end dates/times.
 * 
 * @author rbp28668
 */
public class TimeSeriesType extends ExtensibleMetaPropertyType {

    private LinkedList<String> intervals = new LinkedList<String>();

    /**
     * 
     */
    public TimeSeriesType() {
        super();
    }

    /**
     * @param key
     */ 
    public TimeSeriesType(UUID key) {
        super(key);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.types.MetaPropertyType#getEditor(java.lang.Object)
     */
    public Component getEditor(Object value) {
        TimeSeriesEditor editor = new TimeSeriesEditor(this,(String)value);
        return editor;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.types.MetaPropertyType#getEditValue(java.awt.Component)
     */
    public String getEditValue(Component component) {
        TimeSeriesEditor editor = (TimeSeriesEditor)component;
        return editor.getValue();
    }
    
    public void add(String interval){
        intervals.add(interval);
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType#writeXML(alvahouse.eatool.util.XMLWriter)
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity(ClassUtils.baseClassNameOf(this));
        writeAttributesXML(out);
        for(String value : intervals){
            out.textEntity("Interval",value);
        }
        out.stopEntity();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.types.MetaPropertyType#validate(java.lang.String)
     */
    public String validate(String value) throws IllegalArgumentException {
        getEvents(value);
        return value;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.types.MetaPropertyType#initialise()
     */
    public String initialise() {
        int count = intervals.size();
        StringBuffer buff = new StringBuffer();
        DateFormat format = DateFormat.getDateInstance();
        String now = format.format(new Date());
        buff.append(now);
        for(int i=0;i<count; ++i){
            buff.append('|');
            buff.append("+7"); // 7 days apart
        }
        return buff.toString();
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        if(local.equals("Interval")){
            add(getXMLValue());
        }
    }

    /**
     * @return
     */
    public Collection<String> getIntervals() {
        return Collections.unmodifiableCollection(intervals);
    }

    /**
     * Gets an array of Date that gives the event dates from a property value.
     * @param value is the string containing the event information.
     * @return an array of Date with the event info.
     * @throws IllegalArgumentException
     */
    public Date[] getEvents(String value) throws IllegalArgumentException {

        StringTokenizer toks = new StringTokenizer(value,"|");
        if(toks.countTokens() != intervals.size()+1){
            throw new IllegalArgumentException("Invalid number of events");
        }
        
        Date[] dates = new Date[intervals.size() + 1]; //
        int idx = 0;
        
        DateFormat format = DateFormat.getDateInstance();
        String tok = toks.nextToken();
        tok = tok.trim();
        
        try {
            Date start = format.parse(tok);
            dates[idx++] = start;
            long previous = start.getTime();
            
            final int TO_MILLIS = 24 * 60 * 60 * 1000;
            while(toks.hasMoreTokens()){
                tok = toks.nextToken();
                if(tok.startsWith("+")){
                    int offset = Integer.parseInt(tok.substring(1));
                    // Default is offset in days:
                    if(tok.endsWith("h")){
                        offset *= TO_MILLIS / 24; 
                    } else if (tok.endsWith("d")){
                        offset *= TO_MILLIS;
                    } else if (tok.endsWith("w")){
                        offset *= 7 * TO_MILLIS;
                    } else if (tok.endsWith("m")){
                        offset *= 356.25/12 * TO_MILLIS;
                    } else if (tok.endsWith("y")){
                        offset *= 365.25 * TO_MILLIS;
                    } else { // default to days.
                        offset *= TO_MILLIS;
                    }
                    
                    long time = previous + offset;
                    dates[idx++] = new Date(time);
                    previous = time;
                } else {
                    Date now = format.parse(tok);
                    if(now.getTime() < previous){
                        throw new IllegalArgumentException("Dates not in sequence");
                    }
                    dates[idx++] = now;
                    previous = now.getTime();
                }
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date");
        }
        
        return dates;
    }

    /**
     * 
     */
    public void clear() {
        intervals.clear();
        
    }

}
