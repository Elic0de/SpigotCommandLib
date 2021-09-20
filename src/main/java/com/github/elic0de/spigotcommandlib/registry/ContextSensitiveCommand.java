package com.github.elic0de.spigotcommandlib.registry;

import com.github.elic0de.spigotcommandlib.FragmentExecutionContext;
import com.github.elic0de.spigotcommandlib.context.CommandContext;
import com.github.elic0de.spigotcommandlib.context.ContextProvider;
import com.github.elic0de.spigotcommandlib.invocation.ArgumentCountException;
import com.github.elic0de.spigotcommandlib.invocation.BadArgumentException;
import com.github.elic0de.spigotcommandlib.invocation.BadCommandSenderException;
import com.github.elic0de.spigotcommandlib.invocation.CommandMethodHandle;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ContextSensitiveCommand<T extends FragmentExecutionContext> extends BaseCommandExecutor {
    private final Map<Integer, CommandMethodHandle> handles;
    private final ContextProvider<T> contextProvider;

    public ContextSensitiveCommand(SubCommand command, Map<Integer, CommandMethodHandle> handles, ContextProvider<T> contextProvider) {
        super(command);
        this.handles = handles;
        this.contextProvider = contextProvider;
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        if (!super.canExecute(sender))
            return false;

        if (!(sender instanceof Player))
            return false;

        UUID id = ((Player) sender).getUniqueId();
        CommandContext<T> context = this.contextProvider.getContextInstance(id);

        int state = context == null ? FragmentExecutionContext.DEFAULT_STATE : context.getData().getState();
        CommandMethodHandle handle = this.handles.get(state);

        return handle != null;
    }

    @Override
    public void execute(CommandSender sender, List<String> arguments) throws Exception {
        if (!(sender instanceof Player)) {
            super.sendBadCommandSender(sender, Player.class, sender);
            return;
        }

        Player player = (Player) sender;
        UUID id = player.getUniqueId();

        CommandContext<T> context = this.contextProvider.getOrCreateContextInstance(id);

        CommandMethodHandle handle = this.handles.get(context.getData().getState());
        if (handle == null) {
            // Bad state? Reset
            context.destroy();

            throw new IllegalStateException(String.format("Sender %s was in state %d while executing %s.", id.toString(), context.getData().getState(), super.getCommand()));
        }

        try {
            handle.invoke(sender, arguments, Collections.singletonList(context.getData()));
        } catch (BadCommandSenderException e) {
            super.sendBadCommandSenderHelp(sender, handle, e);
        } catch (ArgumentCountException e) {
            super.sendBadArgumentCountHelp(sender, handle, e);
        } catch (BadArgumentException e) {
            super.sendBadArgumentHelp(sender, handle, e);
        } // All others are ,ust be handled by the caller
    }

    @Override
    public void sendDescription(CommandSender sender) {
        if (!(sender instanceof Player)) {
            super.sendBadCommandSender(sender, Player.class, sender);
            sender.sendMessage(ChatColor.RED + "This can only be executed by a Player. You are a(n) " + sender.getClass().getSimpleName() + ".");
            return;
        }

        UUID id = ((Player) sender).getUniqueId();
        CommandContext<T> context = this.contextProvider.getContextInstance(id);

        int state = context == null ? FragmentExecutionContext.DEFAULT_STATE : context.getData().getState();
        CommandMethodHandle handle = this.handles.get(state);
        if (handle == null)
            handle = this.handles.get(FragmentExecutionContext.DEFAULT_STATE);

        if (handle != null)
            super.sendDescription(sender, handle);
    }
}
