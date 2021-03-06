
package com.github.elic0de.spigotcommandlib.registry;

import com.google.common.base.Defaults;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.github.elic0de.spigotcommandlib.args.CommandParameter;
import com.github.elic0de.spigotcommandlib.args.ParseException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HandleInvoker implements Invoker {
    protected SubCommand subCommand;
    protected String cmdDesc;

    protected Object invocationTarget;
    protected Method method;
    protected Class<?> senderType;
    protected List<CommandParameter<?>> commandParameters;
    protected int minArgsRequired;

    HandleInvoker(SubCommand subCmd, String cmdDesc, Object invocationTarget, Method cmdHandler, Class<?> senderType, List<CommandParameter<?>> commandParameters) {
        this.subCommand = subCmd;
        this.cmdDesc = cmdDesc;
        this.invocationTarget = invocationTarget;
        this.method = cmdHandler;
        this.method.setAccessible(true);
        this.senderType = senderType;
        this.commandParameters = commandParameters;
        int minArgs = 0;
        for (CommandParameter arg : commandParameters) {
            if (!arg.isOptional()) minArgs++;
        }
        this.minArgsRequired = minArgs;
    }

    /**
     * Invoke this handler with the given arguments. The args are all String args that follow the sub command.<br>
     * Ex: /baseCmd sub1 sub2 arg0 arg1 arg2
     *
     * @param sender the command sender. If this type doesn't match the sender type it will inform the sender.
     * @param args   the args in which to invoke the handler with.
     *
     * @throws Exception if the method invocation fails for a non user based error, user based errors will directly be
     *                   messaged to the player.
     */
    @Override
    public boolean invoke(SubCommand command, CommandSender sender, String[] args) throws Exception {
        if (!this.subCommand.equals(command)) return false;
        if (!senderType.isInstance(sender)) {
            //Wrong sender type, cannot invoke
            sendIncorrectSenderMessage(sender);
            return true;
        }

        List<Object> params = buildMethodParams(sender, args);
        if (params == null) return true;

        int i = 0;
        Object[] callParams = new Object[params.size() + 1];
        callParams[i++] = sender;
        for (Object param : params) {
            callParams[i++] = param;
        }
        method.invoke(invocationTarget, callParams);
        return true;
    }

    protected List<Object> buildMethodParams(CommandSender sender, String[] args) {
        if (args.length < minArgsRequired) {
            // Not enough args, send usage
            sendUsage(sender);
            return null;
        }
        List<Object> params = new ArrayList<>();
        // Parse all required
        for (int i = 0; i < minArgsRequired; i++) {
            CommandParameter<?> cmdParam = this.commandParameters.get(i);
            if (cmdParam.getFormatter().canBeParsedFrom(args[i])) {
                try {
                    params.add(cmdParam.getFormatter().parse(args[i]));
                } catch (ParseException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid argument value " + args[i] + ". " + e.getLocalizedMessage());
                    return null;
                }
            } else {
                // Invalid type param
                sendUsage(sender);
                return null;
            }
        }

        // Parse all optional
        int argIndex;
        for (argIndex = minArgsRequired; argIndex < commandParameters.size(); argIndex++) {
            CommandParameter cmdParam = this.commandParameters.get(argIndex);
            if (cmdParam.isVarArgs()) break;
            if (argIndex >= args.length) {
                //Param was not given so we insert a null reference
                if (cmdParam.getArgumentType().isPrimitive()) {
                    //We can't forward a null primitive, we need to set the default value
                    params.add(Defaults.defaultValue(cmdParam.getArgumentType()));
                } else {
                    params.add(null);
                }
                continue;
            }
            if (cmdParam.getFormatter().canBeParsedFrom(args[argIndex])) {
                try {
                    params.add(cmdParam.getFormatter().parse(args[argIndex]));
                } catch (ParseException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid argument value " + args[argIndex] + ". " + e.getLocalizedMessage());
                    return null;
                }
            } else {
                //Invalid type param
                sendUsage(sender);
                return null;
            }
        }

        if (commandParameters.size() > 0) {
            // We need to handle the last one
            CommandParameter lastArg = commandParameters.get(commandParameters.size() - 1);
            if (!lastArg.isVarArgs()) {
                if (argIndex < args.length) {
                    //They gave too many args
                    sendUsage(sender);
                    return null;
                }
            } else {
                //Build the varargs array
                @SuppressWarnings("unchecked")
                List<Object> varArgs = lastArg.getFormatter().createTypedList();
                //Handle varargs
                for (/*argIndex*/; argIndex < args.length; argIndex++) {
                    if (lastArg.getFormatter().canBeParsedFrom(args[argIndex])) {
                        varArgs.add(lastArg.getFormatter().parse(args[argIndex]));
                    } else {
                        //Invalid type param
                        sendUsage(sender);
                        return null;
                    }
                }
                params.add(varArgs.toArray((Object[]) Array.newInstance(lastArg.getFormatter().getParseType(), varArgs.size())));
            }
        }
        return params;
    }

    protected void sendIncorrectSenderMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "This can only be executed by a(n) " + senderType.getSimpleName() + ". You are a(n) " + sender.getClass().getSimpleName() + ".");
    }

    protected void sendUsage(CommandSender sender) {
        StringBuilder strBuilder = new StringBuilder(this.subCommand.toExecutableString());
        for (CommandParameter arg : this.commandParameters) strBuilder.append(" ").append(arg.getDescriptiveName());
        if (sender instanceof Player) {
            ComponentBuilder message = new ComponentBuilder(this.subCommand.toString());
            message.color(ChatColor.RED);
            message.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, strBuilder.toString()));
            message.event(buildTooltip(ChatColor.YELLOW + this.subCommand.toString(),
                    ChatColor.GRAY + "Click to paste this command's",
                    ChatColor.GRAY + "format in your chat box."));
            for (CommandParameter<?> arg : this.commandParameters) {
                message.append(" " + arg.getDescriptiveName());
                message.event(buildTooltip(arg.getDescription()));
            }
            ((Player) sender).spigot().sendMessage(message.create());
        } else {
            sender.sendMessage(ChatColor.YELLOW + strBuilder.toString());
        }
    }

    @Override
    public void sendDescription(SubCommand command, CommandSender sender) {
        if (!this.subCommand.equals(command)) return;
        sender.sendMessage(ChatColor.AQUA + this.cmdDesc);
        StringBuilder strBuilder = new StringBuilder(this.subCommand.toExecutableString());
        for (CommandParameter arg : this.commandParameters) strBuilder.append(" ").append(arg.getDescriptiveName());
        if (sender instanceof Player) {
            ComponentBuilder message = new ComponentBuilder("    \u27A5" + this.subCommand.toString());
            message.color(ChatColor.RED);
            message.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, strBuilder.toString()));
            message.event(buildTooltip(ChatColor.YELLOW + this.subCommand.toString(),
                    ChatColor.GRAY + "Click to paste this command's",
                    ChatColor.GRAY + "format in your chat box."));
            for (CommandParameter<?> arg : this.commandParameters) {
                message.append(" " + arg.getDescriptiveName());
                message.event(buildTooltip(arg.getDescription()));
            }
            ((Player) sender).spigot().sendMessage(message.create());
        } else {
            sender.sendMessage(ChatColor.YELLOW + strBuilder.toString());
        }
    }

    protected static HoverEvent buildTooltip(String... lines) {
        return buildTooltip(Arrays.asList(lines));
    }

    /**
     * Build the HoverEvent that would result in the display
     * of lines when you hover over the component the event is
     * for.
     *
     * @param lines the lines in the tooltip
     *
     * @return the constructed hover event
     */
    protected static HoverEvent buildTooltip(List<String> lines) {
        JsonObject item = new JsonObject();
        item.addProperty("id", "minecraft:stone");
        item.addProperty("Count", 1);

        if (lines.isEmpty())
            return new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{ new TextComponent(item.toString()) });

        JsonObject tag = new JsonObject();
        item.add("tag", tag);

        JsonObject display = new JsonObject();
        tag.add("display", display);

        display.addProperty("Name", ChatColor.WHITE + lines.get(0));
        if (lines.size() > 1) {
            JsonArray lore = new JsonArray();
            for (int i = 1; i < lines.size(); i++)
                lore.add(new JsonPrimitive(ChatColor.WHITE + lines.get(i)));
            display.add("Lore", lore);
        }
        return new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{ new TextComponent(item.toString()) });
    }
}
