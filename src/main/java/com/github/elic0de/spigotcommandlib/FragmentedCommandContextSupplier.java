package com.github.elic0de.spigotcommandlib;

/**
 * Supplies the context manager with new context objects for
 * use in {@link FragmentedCommandHandler}.
 */
public interface FragmentedCommandContextSupplier<T> {
    /**
     * Create a new command context for passing between commands.
     *
     * @return the new context created for a sender in state 0
     *         of a fragmented command.
     */
    T get();
}
