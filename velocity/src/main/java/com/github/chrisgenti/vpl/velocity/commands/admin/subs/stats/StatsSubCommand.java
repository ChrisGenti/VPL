package com.github.chrisgenti.vpl.velocity.commands.admin.subs.stats;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.commands.admin.subs.PluginSubCommand;
import com.github.chrisgenti.vpl.velocity.configurations.language.LanguageFile;
import com.github.chrisgenti.vpl.velocity.data.DataProvider;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

public class StatsSubCommand implements PluginSubCommand {
    private final VPLPlugin plugin;
    private final LanguageFile languageFile;
    private final DataProvider provider;

    public StatsSubCommand(VPLPlugin plugin) {
        this.plugin = plugin;
        this.languageFile = plugin.getLanguageFile(); this.provider = plugin.getProvider();
    }

    @Override
    public String name() {
        return "stats";
    }

    @Override
    public String usage() {
        return "/vpl stats";
    }

    @Override
    public void execute(CommandSource source, String[] arguments) {
        if (arguments.length != 0) {
            plugin.sendMessage(source, languageFile.NO_CORRECT_USAGE.replace("%command%", this.usage()));
            return;
        }

        provider.getUsers().thenAccept(users -> {
            plugin.sendMessage(languageFile.PREMIUM_STATS.replace("%count%", String.valueOf(users.size())));
        });
    }

    @Override
    public boolean hasPermission(SimpleCommand.Invocation invocation) {
        return invocation.source().hasPermission("vpl.stats");
    }
}
