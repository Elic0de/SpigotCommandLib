
package com.github.elic0de.spigotcommandlib.registry;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A bridge from a {@link Command} to our custom registry.
 */
public class BukkitInterceptorCommand extends Command {
    private static final Pattern ARG_PATTERN = Pattern.compile("(?:(['\"])(.*?)(?<!\\\\)(?>\\\\\\\\)*\\1|([^\\s]+))");

    private CommandLib lib;

    BukkitInterceptorCommand(CommandLib lib, String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
        this.lib = lib;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        //Put the command back together.
        StringBuilder cmd = new StringBuilder(super.getName());
        for (String arg : args) {
            cmd.append(" ");
            cmd.append(arg);
        }
        try {
            String[] cmdGiven = parseCommandString(cmd.toString(), false);
            if (!lib.execute(sender, cmdGiven)) {
                lib.sendHelpMessage(sender, cmdGiven);
                return false;
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.BLUE + "An internal error has occurred. Please contact a server administrator.");
            lib.getHook().getLogger().log(Level.SEVERE, "Error executing " + cmd.toString(), e);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        List<String> results = super.tabComplete(sender, alias, args);
        //Put the command back together.
        StringBuilder cmd = new StringBuilder(alias);
        for (String arg : args) {
            cmd.append(" ");
            cmd.append(arg);
        }
        boolean appendSpace = args.length > 0 && args[args.length - 1].length() == 0;
        List<String> possibleSubs = lib.tabComplete(sender, parseCommandString(cmd.toString(), appendSpace));
        if (possibleSubs.isEmpty()) {
            return results;
        } else {
            return possibleSubs;
        }
    }

    /**
     * Parse the command with our custom splitter to support
     * wrapping args in quotation marks for args with spaces.
     *
     * @param command          the entire submitted command.
     * @param appendExtraSpace true if an extra space should be appended to the end of the
     *                         split
     *
     * @return the properly split command
     */
    private String[] parseCommandString(String command, boolean appendExtraSpace) {
        // TODO this isn't true whitespace concat as we dont intercept the command early enough
        // TODO it has already been split on whitespace meaning all whitespace is contracted to a single space.
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        List<String> matches = new ArrayList<>();
        Matcher m = ARG_PATTERN.matcher(command);
        while (m.find()) {
            if (m.group(2) != null) {
                matches.add(m.group(2).replace("\\\"", "\""));
            } else if (m.group(3) != null) {
                matches.add(m.group(3).replace("\\\"", "\""));
            }
        }
        String[] matchesArr = matches.toArray(new String[matches.size() + (appendExtraSpace ? 1 : 0)]);
        if (appendExtraSpace) matchesArr[matchesArr.length - 1] = "";
        return matchesArr;
    }
}
