/*
 * BoxList.java
 *
 * Created on 22 July 2002, 22:38
 */

package alvahouse.eatool.gui.graphical.layout;

import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


/**
 * BoxPacker packs a set of islands as rectangular areas into an overall
 * bounding box.  The bounding box is extended as necessary to include all
 * the islands.
 * @author Bruce.Porteous
 *
 */
public class BoxPacker {
    
	private LinkedList<Island> islands = new LinkedList<Island>();    // of Islands to be positioned
   // private List<BoxNode> boxes = new LinkedList<BoxNode>();			// of BoxNodes tracking allocated space
    private List<BoxNode> freeSpace = new LinkedList<BoxNode>(); 		// of BoxNodes tracking available free space.
    private Rectangle2D.Float allocatedBounds = new Rectangle2D.Float(0,0,0,0);
    
	int aspectWidth;	// nominal width to set desired aspect ratio of display
	int aspectHeight;	// nominal height to set desired aspect ratio of display
	
    /** Creates a new instance of BoxPacker */
    public BoxPacker() {
    }

    
	/**
	 * Adds an island of nodes for future layout
	 * @param island
	 */
	public void addIsland(Island island) {
		islands.addLast(island);
	}
	
    /**
     * Packs all the islands into the given width and height.  Width and height are not absolute but are used to
     * set the aspect ratio of the layout.
     * @param width desired width for aspect ratio.
     * @param height desired width for aspect ratio.
     */
    public void packInto(int width, int height) {
        
        Collections.sort(islands,new IslandAreaComparator());

		aspectWidth = width;
		aspectHeight = height;

		// allocate in reverse order to fit biggest first.		        
        for(ListIterator<Island> iter = islands.listIterator(islands.size()); iter.hasPrevious();) {
            Island island = iter.previous();
            allocate(island);
        }
    }
    

    /**
     * Adds an island of nodes to the data.
     * @param island
     */
    private void allocate(Island island) {

		Rectangle2D.Float position = null; // position where this island should go

		position = island.getBounds();
        float width = position.width + 2;   // 2 adds unit border 
        float height = position.height + 2;
		
        // First look through the free space list & take the smallest possible box that
        // can hold the island
        BoxNode smallest = null;
        
        float minArea = Float.MAX_VALUE;
        for(Iterator<BoxNode> iter = freeSpace.iterator(); iter.hasNext(); ) {
            BoxNode box = iter.next();
            if(box.canContain(width,height)) {
                float area = box.getArea();
                if(area < minArea) {
                    smallest = box;
                    minArea = area;
                }
            }
        }
        
        // Hopefully, smallest now points to the smallest box that could contain
        // the given width and height.  If however, it's still null, then we need
        // to expand the working space.....
        
        if(smallest != null) {
            // so now subdivide the target box into the area that is to be allocated
            // and 2 other rectangle.  The other rectangles get added to the free list.
            position = allocateFromBox(smallest, width,height);
         } else {// need to expand
            position = addSpace(width, height);
        }
        
        island.setPosition(position.x, position.y);
    }

    /**
     * Adds free space to ensure that we can store an object of the given size.  Free space is added to the right, or below, whichever
     * keeps the overall layout closest to the desired aspect ratio.
     * @param width is the width of the desired object.
     * @param height is the height of the desired object.
     * @return where the desired space should be put.
     */
    private Rectangle2D.Float addSpace(float width, float height) {

        Rectangle2D.Float result = null;
        // So need to work out whether we add to the right or below the current
        // bounds.  Choose whichever gives us the nearest fit to the desired
        // aspect ratio
        float ratioDesired = (float)aspectWidth / (float)aspectHeight;

        float ratioBottom = (float)allocatedBounds.width / (float)(allocatedBounds.height + height);  // add to bottom - height increases.
        float ratioRight = (float)(allocatedBounds.width + width) / (float)allocatedBounds.height;      // add to right - width increases.

        ratioBottom /= ratioDesired;
        ratioRight /= ratioDesired;

        if(ratioBottom < 1.0f) ratioBottom = 1.0f/ratioBottom;
        if(ratioRight < 1.0f) ratioRight = 1.0f/ratioRight;

        BoxNode spare;
        if(ratioBottom < ratioRight) { // choose the bottom option....
            result = new Rectangle2D.Float(allocatedBounds.x,allocatedBounds.y + allocatedBounds.height, width, height);
            spare = new BoxNode(allocatedBounds.x + width, result.y, allocatedBounds.width - width, height); 
        } else { // add to right
            result = new Rectangle2D.Float(allocatedBounds.x + allocatedBounds.width, allocatedBounds.y, width, height);
            spare = new BoxNode(result.x, allocatedBounds.y + height, width, allocatedBounds.height - height);
        }
        allocatedBounds =  (Rectangle2D.Float)allocatedBounds.createUnion(result);
        freeSpace.add(spare);
        
        return result;
     }

    
    /**
     * Allocate space within a box for an object of given dimensions.
     * @param box defines the available space.
     * @param width is the width needed.
     * @param height is the height needed.
     * @return the allocated space.
     */
    private Rectangle2D.Float allocateFromBox(BoxNode box, float width, float height) {
        // go for top left initially - 
        float bx = box.getX();
        float by = box.getY();
        float bh = box.getHeight();
        float bw = box.getWidth();
        
        Rectangle2D.Float r = new Rectangle2D.Float(bx,by, width, height);
        
        if(bw > width && bh > height) {
            // should have an L shaped (in some orientation) residual.  Split
            // this into 2 Rectangle2D.Floats.  Want to avoid long skinny Rectangle2D.Floats so
            // pick the split that minimises both aspect ratios - use the product
            // of the 2 ratios to select.

            // Option a:
            // XXXXXX11
            // XXXXXX11
            // 00000011
            Rectangle2D.Float r0a = new Rectangle2D.Float(bx, by + height, width, bh - height);
            Rectangle2D.Float r1a = new Rectangle2D.Float(bx + width, by, bw - width, bh);

            // Option b:
            // XXXXXX11
            // XXXXXX11
            // 00000000
            Rectangle2D.Float r0b = new Rectangle2D.Float(bx, by + height, bw, bh - height);
            Rectangle2D.Float r1b = new Rectangle2D.Float(bx + width, by, bw - width, height);

            float aspect0a = Math.max(r0a.width,r0a.height) / Math.min(r0a.width,r0a.height);
            float aspect1a = Math.max(r1a.width,r1a.height) / Math.min(r1a.width,r1a.height);
            float aspect0b = Math.max(r0b.width,r0b.height) / Math.min(r0b.width,r0b.height);
            float aspect1b = Math.max(r1b.width,r1b.height) / Math.min(r1b.width,r1b.height);

            // choose set with minimum (nearest square) aspect ratio...
            if( aspect0a * aspect1a < aspect0b * aspect1b) { // then choose set a.
                freeSpace.add( new BoxNode(r0a));
                freeSpace.add( new BoxNode(r1a));
            } else { // choose set b.
                freeSpace.add( new BoxNode(r0b));
                freeSpace.add( new BoxNode(r1b));
            }
        } else { // one of the rectangles is missing so just look for a strip on right or bottom....
            if(bw > width) { // box is wider so have a vertical strip on right
                freeSpace.add( new BoxNode(bx + width, by, bw - width, bh));
            }
            
            if(bh > height) { // box is higher so horizontal strip on bottom
                freeSpace.add( new BoxPacker.BoxNode(bx, by + height, bw, bh - height));
            }
        }
        
        // and finally, need to remove the original box from the free list....
        freeSpace.remove(box);
        return r;
     }


    /**
     * Tracks screen space.
     * @author bruce.porteous
     *
     */
    private class BoxNode {

        private Rectangle2D.Float position;

        public float getX() { return position.x; }
        public float getY() { return position.y; }
        public float getWidth() {return position.width; }
        public float getHeight() {return position.height; }

        public BoxNode(float x, float y, float width, float height) {
            position = new Rectangle2D.Float(x,y,width,height);
        }
        
        public BoxNode(Rectangle2D.Float r) {
            position = r;
        }
        
        public boolean canContain(float width, float height) {
            return position.width >= width && position.height >= height;
        }
        
        public float getArea() {
            return  position.width *  position.height;
        }
        
        
    };
    
    /**
     * Comparator to compare 2 islands by area.
     * @author bruce.porteous
     *
     */
    private class IslandAreaComparator implements java.util.Comparator<Island> {
        
        public int compare(Island obj, Island obj1) {
            if (! (obj instanceof Island) || ! (obj1 instanceof Island))
                throw new IllegalArgumentException("IslandAreaComparator trying to compare non-rectangles");
            
            Rectangle2D.Float r = ((Island)obj).getBounds();
            Rectangle2D.Float r1 = ((Island)obj1).getBounds();
            
            float size = r.width * r.height;
            float size1 = r1.width * r1.height;
            
            if(size == size1)
                return 0;
            else if (size < size1)
                return -1;
            else
                return 1;
        }
        
    }

}
