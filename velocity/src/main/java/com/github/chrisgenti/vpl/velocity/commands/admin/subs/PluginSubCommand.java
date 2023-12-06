package com.github.chrisgenti.vpl.velocity.commands.admin.subs;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

public interface PluginSubCommand {
    String name();

    String usage();

    void execute(CommandSource source, String[] arguments);

    default boolean hasPermission(SimpleCommand.Invocation invocation) {
        return true;
    }
}
