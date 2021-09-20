
package com.github.elic0de.spigotcommandlib.registry;

import com.github.elic0de.spigotcommandlib.*;
import com.github.elic0de.spigotcommandlib.invocation.CommandMethodHandle;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CommandLib {
    public static final String NO_PERMISSION = "";
    public static final int NO_TIMEOUT = 0;

    private CommandRegistry registry;
    private Plugin hook;

    public CommandLib(Plugin hook) throws IllegalStateException {
        this.hook = hook;
        try {
            this.registry = new CommandRegistry(this);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Could not retrieve the bukkit command map. It is likely that this instance is being constructed before a server is available.");
        }
    }

    /**
     * Register a new handler. Methods that handle specific commands must
     * be flagged with the {@code @CommandHandle} annotation.
     *
     * @param handler the command handler
     *
     * @see CommandMethodHandle
     */
    public void registerCommandHandler(CommandHandler handler) throws HandlerCompilationException {
        registry.register(handler);
    }

    /**
     * Register a new handler. Methods that handle specific commands must
     * be flagged with the {@code @SubCommandHandle} annotation.
     *
     * @param handler   the command handler
     * @param cmdPrefix the sub required in addition to the sub command for the handle
     *
     * @see SubCommandHandle
     */
    public void registerSubCommandHandler(SubCommandHandler handler, String[] cmdPrefix) throws HandlerCompilationException {
        registry.register(handler, cmdPrefix);
    }

    /**
     * Register a new handler. Methods that handle specific commands must
     * be flagged with the {@code @SubCommandHandle} annotation.
     *
     * @param handler    the command handler
     * @param permission the permission required to execute all methods in this sub handler if
     *                   not overridden in the annotation
     * @param cmdPrefix  the sub required in addition to the sub command for the handle
     *
     * @see SubCommandHandle
     */
    public void registerSubCommandHandler(SubCommandHandler handler, String permission, String[] cmdPrefix) throws HandlerCompilationException {
        registry.register(handler, permission, cmdPrefix);
    }

    /**
     * Register a new handler. Methods handling specific commands must
     * be flagged with the {@code #FragmentCommandHandle} annotation.
     *
     * @param handler    the command handler
     * @param permission the permission required to execute the command. See: {@link #NO_PERMISSION}
     * @param timeout    the time to keep any context instances loaded. See: {@link #NO_TIMEOUT}
     * @param supplier   a supplier to generate new context's when needed
     * @param cmdPrefix  the sub required in addition to the sub command for the handle
     * @param <T>        the type of the context
     *
     * @see FragmentedCommandHandle
     */
    public <T extends FragmentExecutionContext> void registerFragmentedCommandHandler(FragmentedCommandHandler<T> handler, String permission, long timeout, FragmentedCommandContextSupplier<T> supplier, String... cmdPrefix) throws HandlerCompilationException {
        registry.register(handler, permission, timeout, supplier, cmdPrefix);
    }

    /**
     * Register a new handler. Methods handling specific commands must
     * be flagged with the {@code #FragmentCommandHandle} annotation.
     * This uses the default command context.
     *
     * @param handler    the command handler
     * @param permission the permission required to execute the command. See: {@link #NO_PERMISSION}
     * @param timeout    the time to keep any context instances loaded.. See: {@link #NO_TIMEOUT}
     * @param cmdPrefix  the sub required in addition to the sub command for the handle
     *
     * @see FragmentedCommandHandle
     */
    public void registerFragmentedCommandHandler(FragmentedCommandHandler<FragmentExecutionContext> handler, String permission, long timeout, String... cmdPrefix) throws HandlerCompilationException {
        registry.register(handler, permission, timeout, FragmentExecutionContext::new, cmdPrefix);
    }

    /**
     * @return the plugin using this instance of the lib.
     */
    public Plugin getHook() {
        return this.hook;
    }

    protected boolean execute(CommandSender sender, String[] command) throws CommandException {
        return registry.handleCommand(sender, command);
    }

    protected List<String> tabComplete(CommandSender sender, String[] command) {
        return registry.getPossibleSubCommands(command);
    }

    public void sendHelpMessage(CommandSender sender, String... searchQuery) {
        this.registry.displayHelp(sender, searchQuery);
    }
}
