/**
 * 
 */
package alvahouse.eatool.repository.dto.metamodel;

import java.util.LinkedList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "timeSeriesType")
@XmlAccessorType(XmlAccessType.NONE)
public class TimeSeriesTypeDto extends ExtensibleMetaPropertyTypeDto {

    private LinkedList<String> intervals = new LinkedList<String>();

	/**
	 * @return the intervals
	 */
    @XmlElement
	public LinkedList<String> getIntervals() {
    	if(intervals == null) {
    		intervals = new LinkedList<String>();
    	}
		return intervals;
	}

	/**
	 * @param intervals the intervals to set
	 */
	public void setIntervals(LinkedList<String> intervals) {
		this.intervals = intervals;
	}

    
}
