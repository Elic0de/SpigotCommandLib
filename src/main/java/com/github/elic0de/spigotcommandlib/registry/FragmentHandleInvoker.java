
package com.github.elic0de.spigotcommandlib.registry;

import com.github.elic0de.spigotcommandlib.FragmentExecutionContext;
import com.github.elic0de.spigotcommandlib.args.CommandParameter;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.List;

public class FragmentHandleInvoker extends HandleInvoker {

    FragmentHandleInvoker(SubCommand subCmd, String cmdDesc, Object invocationTarget, Method cmdHandler, Class<?> senderType, List<CommandParameter<?>> commandParameters) {
        super(subCmd, cmdDesc, invocationTarget, cmdHandler, senderType, commandParameters);
    }

    /**
     * Invoke this handler with the given arguments. The args are all String args that follow the sub command.<br>
     * Ex: /baseCmd sub1 sub2 arg0 arg1 arg2
     *
     * @param fragmentContext the context in which this command was executed.
     * @param sender          the command sender. If this type doesn't match the sender type it will inform the sender.
     * @param args            the args in which to invoke the handler with.
     *
     * @throws Exception if the method invocation fails for a non user based error, user based errors will directly be
     *                   messaged to the player.
     */
    public void invoke(FragmentExecutionContext fragmentContext, CommandSender sender, String[] args) throws Exception {
        if (!senderType.isInstance(sender)) {
            //Wrong sender type, cannot invoke
            sendIncorrectSenderMessage(sender);
            return;
        }

        List<Object> params = buildMethodParams(sender, args);
        if (params == null) return;

        int i = 0;
        Object[] callParams = new Object[params.size() + 2];
        callParams[i++] = fragmentContext;
        callParams[i++] = sender;
        for (Object param : params) {
            callParams[i++] = param;
        }
        method.invoke(invocationTarget, callParams);
    }

    /**
     * INVALID:
     * Must use {@link #invoke(FragmentExecutionContext, CommandSender, String[])}
     */
    @Deprecated
    @Override
    public boolean invoke(SubCommand command, CommandSender sender, String[] args) throws Exception {
        throw new UnsupportedOperationException("This method is invalid. Please use FragmentHandleInvoker#invoke(FragmentExecutionContext, CommandSender, String[])");
    }

    protected SubCommand getSubCommand() {
        return this.subCommand;
    }
}
