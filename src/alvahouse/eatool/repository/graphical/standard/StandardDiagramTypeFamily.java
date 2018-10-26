/*
 * StandardDiagramTypeFamily.java
 * Project: EATool
 * Created on 03-Oct-2006
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import alvahouse.eatool.repository.graphical.DiagramTypeFamily;
import alvahouse.eatool.util.UUID;

/**
 * StandardDiagramTypeFamily
 * 
 * @author rbp28668
 */
public class StandardDiagramTypeFamily extends DiagramTypeFamily {

	public static final UUID FAMILY_KEY = new UUID("d44500ca-f7bc-4a1e-a94b-9c164378df9b");

    /**
     * @param allTypes
     */
    public StandardDiagramTypeFamily() {
        super(StandardDiagramType.class, FAMILY_KEY);
    }

}
