
package com.github.elic0de.spigotcommandlib.registry;

import org.bukkit.command.CommandSender;

public interface Invoker {

    /**
     * Invoke this invoker for the given sender.
     *
     * @param command the command sent.
     * @param sender  the command sender.
     * @param args    the arguments passed to the command
     *
     * @return returns true if an invocation was attempted, false otherwise.
     *
     * @throws Exception if an error occurs during invocation
     */
    boolean invoke(SubCommand command, CommandSender sender, String[] args) throws Exception;

    /**
     * Send a description of the command that will be invoked
     * with this invoker.
     *
     * @param command send the sender a description of this command.
     * @param sender  the sender that could be sending the command.
     */
    void sendDescription(SubCommand command, CommandSender sender);
}
