/**
 * 
 */
package alvahouse.eatool.gui.graphical.layout;

/**
 * @author bruce.porteous
 *
 */
public class LayoutUtils {


	/** This calculates the length of the arc assuming the
	 * arc is a straight line.
	 * @param a is the arc to check.
	 * @return the arc length.
	 */
	public static float getLength(Arc a) {
		Node start = a.getStartEnd();
		Node finish = a.getFinishEnd();
		float dx = start.getX() - finish.getX();
		float dy = start.getY() - finish.getY();
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	/** Determines whether 2 arcs, modelled as straight lines, intersect.
	 * @param first is the first arc to check for intersection
	 * @param second is the second arc to check for intersection
	 * @return true if the arcs intersect, false otherwise.
	 */
	public static boolean intersects(Arc first, Arc second){
        // First line (x0 + u.dx0, y0 + u.dy0)
        float x0 = first.getStartEnd().getX();
        float y0 = first.getStartEnd().getY();
        float dx0 = first.getFinishEnd().getX() - x0;
        float dy0 = first.getFinishEnd().getY() - y0;
        
        // second line
        float x1 = second.getStartEnd().getX();
        float y1 = second.getStartEnd().getY();
        float dx1 = second.getFinishEnd().getX() - x1;
        float dy1 = second.getFinishEnd().getY() - y1;
        
        // This must be satisfied at point of intersection.
        // x0 + u.dx0 = x1 + v.dx1
        // y0 + u.dy0 = y1 + v.dy1
        // Hence solve simultaneous equations:
        // u.dx0 = x1 + v.dx1 - x0
        // u.dy0 = y1 + v.dy1 - y0
        // ==>
        // u = (x1 + v.dx1 - x0)/dx0
        // u = (y1 + v.dy1 - y0)/dy0
        // ==> (x1 + v.dx1 - x0)/dx0 = (y1 + v.dy1 - y0)/dy0
        // ==> (x1 + v.dx1 - x0).dy0 = (y1 + v.dy1 - y0).dx0
        // ==> x1.dy0 + v.dx1.dy0 - x0.dy0 = y1.dx0 + v.dy1.dx0 - y0.dx0
        // ==> v.dx1.dy0 - v.dy1.dx0 = y1.dx0 - y0.dx0 - x1.dy0 + x0.dy0
        // ==> v.(dx1.dy0 - dy1.dx0) = dx0.(y1 - y0) + dy0.(x0 - x1)
        // ==> v = (dx0.(y1 - y0) + dy0.(x0 - x1))/(dx1.dy0 - dy1.dx0)
        
        float denom = (dx1*dy0 - dy1*dx0);
        if(Math.abs(denom) < 0.000001) // arbitrarily small....
            return false;   // (effectively) parallel.
        
        // find one of the parameters.  Has to be within range 0..1 to
        // make lines cross within their lengths.
        float v = (dx0*(y1 - y0) + dy0*(x0 - x1))/denom;
        return v >= 0 && v <= 1.0;
    }


}
