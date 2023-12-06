package com.github.chrisgenti.vpl.velocity.commands.admin.subs.disable;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.commands.admin.subs.PluginSubCommand;
import com.github.chrisgenti.vpl.velocity.configurations.language.LanguageFile;
import com.github.chrisgenti.vpl.velocity.data.DataProvider;
import com.github.chrisgenti.vpl.velocity.players.PlayerManager;
import com.github.games647.craftapi.UUIDAdapter;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import java.util.UUID;

public class DisableSubCommand implements PluginSubCommand {
    private final VPLPlugin plugin;
    private final LanguageFile languageFile;
    private final DataProvider provider;
    private final PlayerManager playerManager;

    public DisableSubCommand(VPLPlugin plugin) {
        this.plugin = plugin;
        this.languageFile = plugin.getLanguageFile(); this.provider = plugin.getProvider(); this.playerManager = plugin.getPlayerManager();
    }

    @Override
    public String name() {
        return "disable";
    }

    @Override
    public String usage() {
        return "/vpl disable <player>";
    }

    @Override
    public void execute(CommandSource source, String[] arguments) {
        if (arguments.length != 1) {
            plugin.sendMessage(source, languageFile.NO_CORRECT_USAGE.replace("%command%", this.usage()));
            return;
        }
        String username = arguments[0]; UUID uniqueID = UUIDAdapter.generateOfflineId(username);

        if (playerManager.presentInPremium(uniqueID)) {
            playerManager.removePremium(uniqueID); this.continuation(source, username, uniqueID);
        } else {
            provider.presentInUsers(uniqueID).thenAccept(checkPresent -> {
                if (checkPresent) {
                    provider.getUserPremium(uniqueID).thenAccept(premium -> {
                        if (premium) {
                            this.continuation(source, username, uniqueID);
                        } else {
                            plugin.sendMessage(source, languageFile.PLAYER_NOT_PREMIUM.replace("%player%", username));
                        }
                    });
                } else {
                    plugin.sendMessage(source, languageFile.PLAYER_NOT_IN_DATABASE.replace("%player%", username));
                }
            });
        }
    }

    @Override
    public boolean hasPermission(SimpleCommand.Invocation invocation) {
        return invocation.source().hasPermission("vpl.disable");
    }

    private void continuation(CommandSource source, String username, UUID uniqueID) {
        provider.editUser(uniqueID, false).thenAccept(success -> {
            if (success) {
                plugin.sendMessage(source, languageFile.PREMIUM_DISABLED.replace("%player%", username));
            } else {
                plugin.sendMessage(source, languageFile.FIND_ERROR);
            }
        });
    }
}
