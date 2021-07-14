/*
 * ConnectorEndArrowHead.java
 * Created on 07-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical.standard;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import alvahouse.eatool.gui.graphical.standard.UnitVector;
import alvahouse.eatool.repository.dto.graphical.ConnectorEndArrowHeadDto;
import alvahouse.eatool.repository.dto.graphical.ConnectorEndDto;

/**
 * ConnectorEndArrowHead
 * @author Bruce.Porteous
 *
 */
public class ConnectorEndArrowHead extends ConnectorEnd {

	private Polygon triangle = new Polygon();
	/**
	 * 
	 */
	public ConnectorEndArrowHead() {
		super();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.standard.ConnectorEnd#toDto()
	 */
	@Override
	public ConnectorEndDto toDto() {
		return new ConnectorEndArrowHeadDto();
	}
	

	public void draw(Graphics2D g, UnitVector pos, float zoom) {
		if(pos == null) {
			throw new NullPointerException("Null position vector on drawing connector end");
		}
		if(g == null){
			throw new NullPointerException("Null graphics on drawing connector end");
		}
		
		double theta = Math.atan2(pos.getDirectionY(), pos.getDirectionX());
		AffineTransform xform = new AffineTransform();
		xform.translate(pos.getStartX() * zoom, pos.getStartY() * zoom);
		xform.rotate(theta);
		
		Shape baseShape = getScaledShape(g, zoom);
		Shape transformed = xform.createTransformedShape(baseShape);		
		g.fill(transformed);
	}
	
	private Shape getScaledShape(Graphics2D g, float zoom) {
		FontMetrics fm = g.getFontMetrics(scaleFont);
		int size = fm.getHeight();
		int s = (int)(size * zoom);
		int s2 = (int)(size / 2 * zoom);
		triangle.reset();
		triangle.addPoint(0,0);
		triangle.addPoint(s,s2);
		triangle.addPoint(s,-s2);
		return triangle;
	}


}
