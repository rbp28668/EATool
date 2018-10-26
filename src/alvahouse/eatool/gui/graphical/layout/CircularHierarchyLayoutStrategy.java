/*
 * CircularHierarchyLayoutStrategy.java
 * Project: EATool
 * Created on 15-Feb-2006
 *
 */
package alvahouse.eatool.gui.graphical.layout;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * CircularHierarchyLayoutStrategy lays out a hierarchy of nodes into a circular
 * pattern with the root at the centre.
 * 
 * There is only 1 level 1 node: the root.
 * Count level 2 nodes and equi-space adjusting the radius to fit.
 * Count level N nodes ajusting radius to fit outside N-1 nodes.
 * @author rbp28668
 */
public class CircularHierarchyLayoutStrategy extends HieararchyLayoutStrategyBase {

    /**
     * 
     */
    public CircularHierarchyLayoutStrategy() {
        super();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.layout.HieararchyLayoutStrategyBase#layoutHierarchy(alvahouse.eatool.gui.graphical.NodeGraph, alvahouse.eatool.gui.graphical.layout.HieararchyLayoutStrategyBase.HNode)
     */
    protected void layoutHierarchy(NodeGraph graph, HNode hierarchy) {
        
        float size = sizeItems(hierarchy);
        size = (float)Math.sqrt(size);
        
        int depth = getDepth(hierarchy);
        
        float[] radii = getRadii(hierarchy, size, depth);
        
        getFraction(hierarchy, 0, size, radii);
        updateRadii(hierarchy, radii);
        layout(hierarchy, 1.0f, 0.0f, 0, radii);
    }


    /**
     * Gets the depth of the hierarchy.
     * @param node is the root of the hierarchy.
     * @return the depth.
     */
    private int getDepth(HNode node){
        
        int max = 1;
        for(HNode child : node.getChildren()){
            int depth = 1 + getDepth(child);
            if(depth > max){
                max = depth;
            }
        }
        return max;
    }

//    private void validateFraction(HNode node){
//        float sum = 0.0f;
//        for(Iterator iter = node.getChildren().iterator(); iter.hasNext();){
//            HNode child = (HNode)iter.next();
//            sum += child.getCompositeWidth();
//        }
//        
//        if(sum > 1.0f) {
//            System.err.println("Invalid sum " + sum);
//        }
//        
//        for(Iterator iter = node.getChildren().iterator(); iter.hasNext();){
//            HNode child = (HNode)iter.next();
//            validateFraction(child);
//        }
//        
//    }

    /**
     * Works out the radii for the different rings. 
     * @param root is the root of the hierarchy.
     * @param size is the nominal size of a node.
     * @param maxDepth is the maximum depth of the hierarchy.
     * @return an array of float containing the radii.
     */
    private float[] getRadii(HNode root, float size, int maxDepth){

        float[] radii = new float[maxDepth];
        

        List<HNode> currentLevel = new LinkedList<HNode>();
        currentLevel.add(root);
        float radius = 0;
        
        int depth = 0;
        while(!currentLevel.isEmpty()){
            
            System.out.println("Radius " + depth + " is " + radius);

            radii[depth] = radius;
            ++depth;
            
            List<HNode> nextLevel = new LinkedList<HNode>();
            
            float deltaR0 = 0.0f;
            float diameter = 0.0f;
            for(Iterator<HNode> iter = currentLevel.iterator(); iter.hasNext(); ){
                HNode child = iter.next();
                Collection<HNode> children = child.getChildren();
                
                // Work out the size of this segment at the next level down
                // based on 0.5 size spacing between nodes and at either end.
                float width = segmentWidth(children.size(),size);
                diameter += width;
                nextLevel.addAll(children);
                
                // Want to ensure sufficient radius is given to stop any
                // one set of children spreading round the circle so push
                // out to 90 degrees.  In practice, for a segment arc
                // size A, the increase in radius needs to be at least
                // 2A/PI (allow to go to 180 2 -> 1.0)
                float dr = (float)(width * 1.0 / Math.PI);
                deltaR0 = Math.max(deltaR0,dr);
            }
            
            float deltaR1 = 2 * size;

            float deltaR2 = (float)(diameter / (Math.PI * 2));
            deltaR2 -= radius;
            
            float deltaR = Math.max(deltaR0,Math.max(deltaR1,deltaR2));
            
            radius += deltaR;	// go out a level.
            currentLevel = nextLevel;
        }
        
        return radii;
    }
    

    /**
     * In some circumstances the radii might be optimistic: move out if
     * needed.
     * @param hierarchy
     * @param radii
     */
    private void updateRadii(HNode hierarchy, float[] radii) {

        List<HNode> currentLevel = new LinkedList<HNode>();
        currentLevel.add(hierarchy);
        
        int depth = 0;
        float scale = 1.0f;
        while(!currentLevel.isEmpty()){
            
            List<HNode> nextLevel = new LinkedList<HNode>();
            
            float sum = 0.0f;
            for(Iterator<HNode> iter = currentLevel.iterator(); iter.hasNext(); ){
                HNode child = iter.next();
                
                sum += child.getCompositeWidth();
                
                Collection<HNode> children = child.getChildren();
                nextLevel.addAll(children);
                
           }
            
            
            if(sum > scale){
                scale = sum;
            }
                
            
            for(Iterator<HNode> iter = currentLevel.iterator(); iter.hasNext(); ){
                HNode child = iter.next();
                float width =  child.getCompositeWidth();
                width /= scale;
                child.setCompositeWidth(width);
            }
            
            radii[depth] *= scale;
            ++depth;

            currentLevel = nextLevel;
            
        }
        
    }

    /**
     * Work out the segment diameter needed for a given number of nodes.
     * @param nodeCount
     * @return
     */
    private float segmentWidth(int nodeCount, float size){
        float segment = nodeCount  * 1.2f * size;
        return segment;
    }
    
    /**
     * Get the fraction of a circle that the node needs to occupy.
     * @param root
     * @param depth
     * @param size
     * @param radii
     * @return
     */
    private float getFraction(HNode root, int depth, float size,  float[] radii){
        
        float fraction = 1.0f; 
        if( depth > 0) {
            float dia = (float)(radii[depth] * 2 * Math.PI);
            fraction = segmentWidth(1, size) / dia;
        }    

        float sumFraction = 0.0f;
        for(HNode child : root.getChildren()){
            float fractChild = getFraction(child, depth+1, size, radii);
            sumFraction += fractChild;
        }
        
        if(sumFraction > fraction){
            fraction = sumFraction;
        }
        root.setCompositeWidth(fraction);
        
        return fraction;
    }

    /**
     * Do the actual layout.
     * @param root is the root of the hierarchy.
     * @param fraction is the fraction of the circle that this node can use
     * for drawing.
     * @param theta is the fractional angle to place this node on (0..1)
     * @param depth is the depth in the hierarchy.
     * @param radii is the array of radii for each level.
     */
    private void layout(HNode root, float fraction, float theta, int depth, float[] radii){
        
        float radius = radii[depth];

        
        float x = (float)(radius * Math.cos(theta * 2 * Math.PI));
        float y = (float)(radius * Math.sin(theta * 2 * Math.PI));
        root.getNode().setPosition(x,y);

        Collection<HNode> children = root.getChildren();
        if(!children.isEmpty()){
            
	        //float dia = (float)(2 * Math.PI * radii[depth+1]);

	        float totalWidth = 0.0f;
	        for(HNode child : children ){
	            totalWidth += child.getCompositeWidth();
	        }

	        float padding = (fraction - totalWidth)/children.size();
	        
	        float angle = theta - (fraction / 2);
	        for(HNode child : children ){
	            float arc = child.getCompositeWidth() + padding;
	            layout(child, arc, angle + arc/2, depth+1, radii);
	            angle += arc;
	        }
        }
    }
    

    /**
     * Calculate the size of all the nodes for scaling the hierarchy
     * diagram.
     * @param hierarchy
     * @return nominal size of a node to use in scaling.
     */
    private float sizeItems(HNode hierarchy){
        Rectangle2D.Float bounds = hierarchy.getNode().getBounds();
        
        float s2 = bounds.width * bounds.width + bounds.height * bounds.height;
        
        for(HNode child : hierarchy.getChildren()){
            float childSize = sizeItems(child);
            if(childSize > s2){
                s2 = childSize;
            }
        }
        
        return s2;
    }
}
