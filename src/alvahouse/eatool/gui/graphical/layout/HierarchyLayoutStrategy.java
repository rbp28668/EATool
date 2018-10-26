/*
 * HierarchyLayoutStrategy.java
 * Project: EATool
 * Created on 15-Feb-2006
 *
 */
package alvahouse.eatool.gui.graphical.layout;

import java.awt.geom.Rectangle2D;


/**
 * HierarchyLayoutStrategy is a layout strategy that lays out a hiearchy of nodes
 * in a standard tree fashion (root at top).
 * 
 * @author rbp28668
 */
public class HierarchyLayoutStrategy extends HieararchyLayoutStrategyBase {

    private float xSpacing = 10;
    private float ySpacing = 40;
    
    /**
     * 
     */
    public HierarchyLayoutStrategy() {
        super();
    }

    protected void layoutHierarchy(NodeGraph graph, HNode hierarchy){
        calculateNodeWidths(hierarchy);
    
	    float leftX = xSpacing;	// allow margin;
	    float y = hierarchy.getHeight()/2 + ySpacing;
	    layout(hierarchy,leftX,y);
	    
    }

 
    private float calculateNodeWidths(HNode root){
        Rectangle2D.Float bounds = root.getNode().getBounds();
        
        float width = 0;
        for(HNode child : root.getChildren()){
            width += calculateNodeWidths(child);
        }
    
        int childCount = root.getChildren().size();
        if(childCount > 1){
            width += childCount * xSpacing;
        }
        
        width = Math.max(width, bounds.width);
        root.setCompositeWidth(width);
        root.setHeight(bounds.height);
        return width;
    }
    
    
    private void layout(HNode root, float leftX, float yPosition){
        
        float x = leftX + root.getCompositeWidth()/2;
        root.getNode().setPosition(x,yPosition);
        
        yPosition += root.getHeight() + ySpacing;
        
        for(HNode child : root.getChildren()){
            layout(child, leftX, yPosition);
            leftX += child.getCompositeWidth() + xSpacing;
        }
    }
    

}
