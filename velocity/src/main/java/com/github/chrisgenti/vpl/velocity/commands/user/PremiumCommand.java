package com.github.chrisgenti.vpl.velocity.commands.user;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.commands.PluginCommand;
import com.github.chrisgenti.vpl.velocity.configurations.config.ConfigFile;
import com.github.chrisgenti.vpl.velocity.configurations.language.LanguageFile;
import com.github.chrisgenti.vpl.velocity.players.PlayerManager;
import com.github.chrisgenti.vpl.velocity.utils.Mojang;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.UUID;

public class PremiumCommand implements PluginCommand {
    private final VPLPlugin plugin;
    private final ConfigFile configFile;
    private final LanguageFile languageFile;
    private final PlayerManager playerManager;

    public PremiumCommand(VPLPlugin plugin) {
        this.plugin = plugin;
        this.configFile = plugin.getConfigFile(); this.languageFile = plugin.getLanguageFile(); this.playerManager = plugin.getPlayerManager();
    }

    @Override
    public String name() {
        return "premium";
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (source instanceof ConsoleCommandSource) {
            plugin.sendMessage(source, languageFile.NO_CONSOLE);
            return;
        }
        Player player = (Player) source; String username = player.getUsername(); UUID uniqueID = player.getUniqueId();

        if (!this.hasPermission(invocation)) {
            plugin.sendMessage(source, languageFile.NO_PERMISSION);
            return;
        }

        if (invocation.arguments().length != 0) {
            plugin.sendMessage(languageFile.NO_CORRECT_USAGE.replace("%command%", this.usage()));
            return;
        }

        if (playerManager.presentInPremium(uniqueID)) {
            plugin.sendMessage(source, languageFile.ALREADY_PREMIUM);
            return;
        }

        if (!Mojang.premiumUsername(username)) {
            plugin.sendMessage(source, languageFile.NO_PREMIUM_USERNAME);
            return;
        }

        if (playerManager.presentInConfirmation(uniqueID)) {
            playerManager.removeConfirmation(uniqueID); playerManager.addAwaiting(uniqueID);
            player.disconnect(MiniMessage.miniMessage().deserialize(languageFile.PREMIUM_RECONNECT.replace("%minutes%", String.valueOf(configFile.CONFIRMATION_TIME))));
            return;
        }
        playerManager.addConfirmation(uniqueID); plugin.sendMessage(source, languageFile.PREMIUM_CONFIRM);
    }
}
