package com.github.chrisgenti.vpl.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;

public interface PluginCommand extends SimpleCommand {
    String name();

    default String usage() { return "/" + this.name(); };
}
