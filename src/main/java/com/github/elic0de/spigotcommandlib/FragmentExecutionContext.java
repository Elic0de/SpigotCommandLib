package com.github.elic0de.spigotcommandlib;

/**
 * Used to store state information that will be carried through
 * fragment handlers. (See: {@link FragmentedCommandHandle}).
 * The default implementation just handles state but registration
 * may be on a class that extends this base.
 */
public class FragmentExecutionContext {
    public static final int DEFAULT_STATE = 0;

    private int state;

    public FragmentExecutionContext() {
        this.state = 0;
    }

    /**
     * Get the current state that the executor bound to
     * this context is in.
     * <p>
     * The executor must be in the correct state in order for
     * execution of a fragment command to occur.
     *
     * @return the state of the executor bound to this context.
     */
    public final int getState() {
        return this.state;
    }

    /**
     * Set the current state that the executor bound to this context
     * is in.
     *
     * @param state the new state for this executor's context.
     */
    public final void setState(int state) {
        this.state = state;
    }
}
