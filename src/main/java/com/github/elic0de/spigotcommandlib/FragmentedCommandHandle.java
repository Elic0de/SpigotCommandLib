package com.github.elic0de.spigotcommandlib;

import java.lang.annotation.*;

/**
 * Marks this method as a fragment handler for a command that is executed in
 * multiple parts. These marked methods should belong to a class that
 * implements {@link FragmentedCommandHandler}
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FragmentedCommandHandle {

    /**
     * The state in which the user must be in to invoke this
     * command.
     *
     * @return the state required to use this fragment.
     */
    int state() default 0;

    /**
     * Specifies the sub command that this method handles.
     * Ex: /baseCommand subCmd1 subCmd2 = <code>new String[] {"baseCommand|baseAlias1", "subCmd1|alias1|alias2",
     * "subCmd2"}</code>
     *
     * @return a string array containing the full sub command that this method handles
     */
    String[] command() default {};

    /**
     * Specifies the permission required by the executer to successfully
     * execute this sub command.
     * Ex: my.subcommands.permission
     *
     * @return the String representation of the required permission
     */
    String permission() default "";

    /**
     * A short description about what this command does.
     *
     * @return the description
     */
    String description();
}
