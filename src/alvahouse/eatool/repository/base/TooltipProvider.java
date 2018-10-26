/*
 * TooltipProvider.java
 * Created on 13-Jun-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.base;

/**
 * TooltipProvider provides an interface to allow objects that
 * will be displayed graphically to provide a tooltip distinct
 * from their toString method.  Implemented by domain objects 
 * that will potentially be displayed in trees, diagrams etc.
 * @author Bruce.Porteous
 *
 */
public interface TooltipProvider {
	/**
	 * Gets the tooltip text for the implementing object.
	 * @return tooltip String.
	 */
	public abstract String getTooltip();
}
