/*
 * TimeDiagramTypeFamily.java
 * Project: EATool
 * Created on 03-Oct-2006
 *
 */
package alvahouse.eatool.gui.graphical.time;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.dto.graphical.DiagramTypeDto;
import alvahouse.eatool.repository.dto.graphical.TimeDiagramTypeDto;
import alvahouse.eatool.repository.graphical.DiagramType;
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

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramTypeFamily#newDiagramType(alvahouse.eatool.repository.Repository)
	 */
	@Override
	public DiagramType newDiagramType(Repository repository) throws Exception {
		TimeDiagramType type = new TimeDiagramType(repository, new UUID());
		type.setFamily(this);
		return type;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramTypeFamily#newDiagramType(alvahouse.eatool.repository.Repository, alvahouse.eatool.repository.dto.graphical.DiagramTypeDto)
	 */
	@Override
	public DiagramType newDiagramType(Repository repository, DiagramTypeDto dto) throws Exception {
		TimeDiagramTypeDto tdtdto = (TimeDiagramTypeDto)dto;
		TimeDiagramType type = new TimeDiagramType(repository, this, tdtdto);
		return type;
	}

}
