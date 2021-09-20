package com.github.elic0de.spigotcommandlib;

import com.github.elic0de.spigotcommandlib.registry.CommandLib;

import java.lang.annotation.*;

/**
 * Marks the annotated method as a method that should be registered as a
 * command. Methods marked with this method should belong to a class
 * implementing {@link CommandHandler}.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandHandle {

    /**
     * Specifies the sub command that this method handles.
     * Ex: /baseCommand subCmd1 subCmd2 = <code>new String[] {"baseCommand|baseAlias1", "subCmd1|alias1|alias2",
     * "subCmd2"}</code>
     *
     * @return a string array containing the full sub command that this method handles
     */
    String[] command();

    /**
     * Specifies the permission required by the executer to successfully
     * execute this sub command.
     * Ex: my.subcommands.permission
     *
     * @return the String representation of the required permission
     */
    String permission() default CommandLib.NO_PERMISSION;

    /**
     * A short description about what this command does.
     *
     * @return the description
     */
    String description();
}
