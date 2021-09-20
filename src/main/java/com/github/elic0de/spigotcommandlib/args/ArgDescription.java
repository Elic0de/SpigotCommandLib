
package com.github.elic0de.spigotcommandlib.args;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provide an argument description for an argument to use
 * rather than the default.
 * <p>
 * Note: Leaving the default value
 * will result in the command manager deciding on the values
 * itself.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ArgDescription {

    /**
     * @return a description of this argument
     */
    String[] description() default {};

    /**
     * @return the name of this argument
     */
    String name() default "";

    /**
     * @return true if this arg is optional, false if it is required
     */
    boolean optional() default false;
}
