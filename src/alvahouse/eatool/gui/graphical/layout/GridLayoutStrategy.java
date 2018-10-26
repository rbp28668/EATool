/*
 * GridLayoutStrategy.java
 *
 * Created on 12 February 2002, 18:03
 */

package alvahouse.eatool.gui.graphical.layout;

import java.awt.geom.Rectangle2D;

/**
 * Very simple layout strategy that just puts the nodes on a grid
 * in whatever order they start in.  Has no real intelligence of
 * its own but is used to set up starting positions for other
 * algorithms in conjunction with CompositeGraphicslLayoutStrategy.
 * @author  rbp28668
 */
public class GridLayoutStrategy implements IGraphicalLayoutStrategy {

    /** Creates new GridLayoutStrategy */
    public GridLayoutStrategy() {
    }

    public void layout(NodeGraph model) {
        int nNodes = model.nodeCount();
		if(nNodes == 0)
			return;
        
        
        
        // Set default spacing based on symbol size.
		float xs = 0;
		float ys = 0;
        
		for(Node node : model.getNodes()) {
			Rectangle2D.Float bounds = node.getBounds();
			if(bounds.width > xs){
				xs = (int)bounds.width;
			}
			if(bounds.height > ys){
				ys = (int)bounds.height;
			}
		}
	
		int xSpacing = (int)(xs * 1.5f);
		int ySpacing = (int)(ys * 1.5f);
		
        
        int width = (int)Math.round(Math.sqrt(nNodes));
        
        int ix = 1;
        int iy = 1;
        
        // put disconnected nodes first so they don't get lost in
        // any subsequent re-organisation
        for(Node node : model.getNodes()) {
			if(node.arcCount() == 0) {
                node.setPosition(ix * xSpacing,iy * ySpacing);
                ++ix;
                if(ix > width) {
                    ix = 1;
                    ++iy;
                }
            }
        }
        
        // and now do the connected nodes.
        for(Node node : model.getNodes()) {
			if(node.arcCount() > 0) {
                node.setPosition(ix * xSpacing, iy * ySpacing);
                ++ix;
                if(ix > width) {
                    ix = 1;
                    ++iy;
                }
            }
        }
        complete = true; // only takes 1 pass
    }
    
    public void reset() {
        complete = false;
    }
    
    public boolean isComplete() {
        return complete;
    }

    private boolean complete = false;
}
