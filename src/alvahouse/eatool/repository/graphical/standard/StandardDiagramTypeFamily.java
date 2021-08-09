/*
 * StandardDiagramTypeFamily.java
 * Project: EATool
 * Created on 03-Oct-2006
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.dto.graphical.DiagramTypeDto;
import alvahouse.eatool.repository.dto.graphical.StandardDiagramTypeDto;
import alvahouse.eatool.repository.graphical.DiagramType;
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

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramTypeFamily#newDiagramType(alvahouse.eatool.repository.Repository)
	 */
	@Override
	public DiagramType newDiagramType(Repository repository) throws Exception {
		StandardDiagramType type = new StandardDiagramType(repository);
		type.setFamily(this);
		return type;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramTypeFamily#newDiagramType(alvahouse.eatool.repository.dto.graphical.DiagramTypeDto)
	 */
	@Override
	public DiagramType newDiagramType(Repository repository, DiagramTypeDto dto) throws Exception {
		StandardDiagramTypeDto sdtdto = (StandardDiagramTypeDto) dto;
		StandardDiagramType type = new StandardDiagramType(repository, this,sdtdto);
		type.setFamily(this);
		return type;
	}

}
