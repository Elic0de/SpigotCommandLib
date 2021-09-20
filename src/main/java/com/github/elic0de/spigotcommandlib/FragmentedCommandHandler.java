package com.github.elic0de.spigotcommandlib;

import java.util.UUID;

/**
 * Marks the class as a class that handles fragmented commands annotated
 * with the {@link FragmentedCommandHandle} annotation.
 */
public interface FragmentedCommandHandler<T extends FragmentExecutionContext> {

    /**
     * This method is invoked when the context for the sender
     * with the given UUID is being removed from the context
     * handler. Do not count on this method executing immediately
     * on timeout, the cleanup time is arbitrary but this hook allows
     * you to be notified of the context's removal from memory.
     *
     * @param id      the UUID of the sender
     * @param context their execution context
     */
    void onCleanup(UUID id, T context);
}
