
package com.github.elic0de.spigotcommandlib.args;

public enum CommandParameterKind {
    REQUIRED(false, false, "<%s>"),
    OPTIONAL(true, false, "[%s]"),
    VAR_ARGS(true, true, "[%s]...");

    private final boolean optional;
    private final boolean varArgs;
    private final String argFormatString;

    CommandParameterKind(boolean optional, boolean varArgs, String argFormatString) {
        this.optional = optional;
        this.varArgs = varArgs;
        this.argFormatString = argFormatString;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public boolean isVarArgs() {
        return this.varArgs;
    }

    public String formatNameInArgPattern(String name) {
        return String.format(this.argFormatString, name);
    }
}
