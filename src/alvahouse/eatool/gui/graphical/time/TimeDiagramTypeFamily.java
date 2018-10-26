/*
 * TimeDiagramTypeFamily.java
 * Project: EATool
 * Created on 03-Oct-2006
 *
 */
package alvahouse.eatool.gui.graphical.time;

import alvahouse.eatool.repository.graphical.DiagramTypeFamily;
import alvahouse.eatool.util.UUID;

/**
 * TimeDiagramTypeFamily
 * 
 * @author rbp28668
 */
public class TimeDiagramTypeFamily extends DiagramTypeFamily {

    public  final static UUID FAMILY_KEY = new UUID("4182df27-a4cd-48a6-a284-6f50e69ed703");

    /**
     */
    public TimeDiagramTypeFamily() {
        super(TimeDiagramType.class, FAMILY_KEY);
    }

}
