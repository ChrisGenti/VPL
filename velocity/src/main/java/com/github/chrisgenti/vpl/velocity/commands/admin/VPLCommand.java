package com.github.chrisgenti.vpl.velocity.commands.admin;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.commands.PluginCommand;
import com.github.chrisgenti.vpl.velocity.commands.admin.subs.PluginSubCommand;
import com.github.chrisgenti.vpl.velocity.commands.admin.subs.accounts.AccountsSubCommand;
import com.github.chrisgenti.vpl.velocity.commands.admin.subs.check.CheckSubCommand;
import com.github.chrisgenti.vpl.velocity.commands.admin.subs.disable.DisableSubCommand;
import com.github.chrisgenti.vpl.velocity.commands.admin.subs.stats.StatsSubCommand;
import com.github.chrisgenti.vpl.velocity.configurations.language.LanguageFile;
import com.velocitypowered.api.command.CommandSource;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VPLCommand implements PluginCommand {
    private final Set<PluginSubCommand> subCommands = ConcurrentHashMap.newKeySet();
    private final VPLPlugin plugin;
    private final LanguageFile languageFile;

    public VPLCommand(VPLPlugin plugin) {
        this.plugin = plugin; this.languageFile = plugin.getLanguageFile();

        this.subCommands.add(new StatsSubCommand(plugin));
        this.subCommands.add(new CheckSubCommand(plugin));
        this.subCommands.add(new DisableSubCommand(plugin));
        this.subCommands.add(new AccountsSubCommand(plugin));
    }

    @Override
    public String name() {
        return "vpl";
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (!hasPermission(invocation)) {
            plugin.sendMessage(source, languageFile.NO_PERMISSION);
            return;
        }

        if (invocation.arguments().length == 0) {
            plugin.sendMessage(source, languageFile.MAIN_INFO);
            return;
        }
        String value = invocation.arguments()[0];

        PluginSubCommand subCommand = this.subCommand(value);
        if (subCommand == null) {
            plugin.sendMessage(source, languageFile.NO_SUB_COMMAND);
            return;
        }

        if (!subCommand.hasPermission(invocation)) {
            plugin.sendMessage(source, languageFile.NO_PERMISSION);
            return;
        }
        subCommand.execute(source, this.arguments(invocation.arguments()));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("vpl.cmd");
    }

    private PluginSubCommand subCommand(String name) {
        return this.subCommands.stream().filter(var -> var.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    private String[] arguments(String[] args) {
        String[] arguments = new String[args.length - 1];
        System.arraycopy(args,1, arguments, 0, arguments.length);
        return arguments;
    }
}
