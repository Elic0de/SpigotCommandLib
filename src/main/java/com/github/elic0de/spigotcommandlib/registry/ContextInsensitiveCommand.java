package com.github.elic0de.spigotcommandlib.registry;

import com.github.elic0de.spigotcommandlib.invocation.*;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ContextInsensitiveCommand extends BaseCommandExecutor {
    private final CommandMethodHandle handle;

    public ContextInsensitiveCommand(SubCommand cmd, CommandMethodHandle handle) {
        super(cmd);
        this.handle = handle;
    }

    @Override
    public void execute(CommandSender sender, List<String> arguments) throws Exception {
        try {
            this.handle.invoke(sender, arguments, Collections.emptyList());
        } catch (BadCommandSenderException e) {
            super.sendBadCommandSenderHelp(sender, this.handle, e);
        } catch (ArgumentCountException e) {
            super.sendBadArgumentCountHelp(sender, this.handle, e);
        } catch (BadArgumentException e) {
            super.sendBadArgumentHelp(sender, this.handle, e);
        } // All others are ,ust be handled by the caller
    }

    @Override
    public void sendDescription(CommandSender sender) {
        super.sendDescription(sender, this.handle);
    }
}
