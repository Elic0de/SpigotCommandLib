
package com.github.elic0de.spigotcommandlib.args;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandParameter<T> {
    private final CommandParameterKind kind;
    private final ArgumentFormatter<T> formatter;
    private final Class type;

    private final String name;
    private final List<String> desc;

    public CommandParameter(CommandParameterKind kind, ArgumentFormatter<T> formatter, Class argClass, String name, List<String> desc) {
        this.kind = kind;
        this.formatter = formatter;
        this.type = argClass;
        this.name = name;

        if (desc == null) {
            this.desc = new ArrayList<>(formatter.getTypeDesc().length + 1);
            this.desc.add(ChatColor.YELLOW + formatter.getTypeName());
            Arrays.stream(formatter.getTypeDesc())
                    .map(s -> ChatColor.GRAY + s)
                    .forEachOrdered(this.desc::add);
        } else {
            this.desc = desc.stream()
                    .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                    .collect(Collectors.toList());
        }
    }

    public CommandParameter(CommandParameterKind kind, ArgumentFormatter<T> formatter, Class argClass, String name) {
        this(kind, formatter, argClass, name, null);
    }

    /**
     * The name of the argument. Each argument should have a unique name.
     *
     * @return the name of this argument.
     */
    public String getName() {
        return this.name;
    }

    /**
     * A descriptive name is a name that implies additional
     * information about the argument. For example [argname] vs &lt;argname&gt;.
     *
     * @return the descriptive name for this argument
     */
    public String getDescriptiveName() {
        return this.kind.formatNameInArgPattern(this.getName());
    }

    /**
     * Get a description of the use of the parameter. This
     * explains the value for the argument.
     *
     * @return the description of the parameter.
     */
    public List<String> getDescription() {
        return this.desc;
    }

    /**
     * Get the {@link ArgumentFormatter} for this parameter. It can
     * be used to check if an argument can be parsed for this
     * parameter as well as actually doing the parsing.
     *
     * @return the argument formatter for this parameter.
     */
    public ArgumentFormatter<T> getFormatter() {
        return this.formatter;
    }

    /**
     * Get the type that this argument is declared as.
     *
     * @return the type this argument is declared as
     */
    public Class getArgumentType() {
        return this.type;
    }

    /**
     * Check if this parameter is an argument of varying length.
     *
     * @return true iff this parameter is of varying length, false otherwise.
     */
    public boolean isVarArgs() {
        return this.kind.isVarArgs();
    }

    /**
     * Check if this parameter is optional or not.
     * if {@link #isVarArgs()} returns true, isOptional() will also return true
     *
     * @return true iff this parameter is optional, false otherwise.
     */
    public boolean isOptional() {
        return this.kind.isOptional();
    }

    public CommandParameterKind getKind() {
        return this.kind;
    }

    @Override
    public String toString() {
        return "CommandParameter{" +
                "formatter=" + this.formatter +
                ", name='" + this.name + '\'' +
                ", desc=" + this.desc +
                ", kind=" + this.kind +
                '}';
    }
}
