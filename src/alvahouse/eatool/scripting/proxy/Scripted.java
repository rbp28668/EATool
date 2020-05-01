/**
 * 
 */
package alvahouse.eatool.scripting.proxy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mark a proxy object as available for scripting.  Doesn't 
 * impact behaviour except in the scripting object browser display.
 * Name property is the name to report in the documentation.
 * Optional description provides further documentation.
 * @author bruce_porteous
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Scripted {
	 public String name() default "";
	 public String description() default "";
}
