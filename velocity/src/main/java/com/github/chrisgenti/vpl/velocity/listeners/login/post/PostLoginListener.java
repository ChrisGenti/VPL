package com.github.chrisgenti.vpl.velocity.listeners.login.post;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.configurations.language.LanguageFile;
import com.github.chrisgenti.vpl.velocity.data.DataProvider;
import com.github.chrisgenti.vpl.velocity.listeners.Listener;
import com.github.chrisgenti.vpl.velocity.players.PlayerManager;
import com.github.chrisgenti.vpl.velocity.utils.Mojang;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class PostLoginListener implements Listener<PostLoginEvent> {
    private final VPLPlugin plugin;
    private final EventManager eventManager;
    private final LanguageFile languageFile;
    private final DataProvider provider;
    private final PlayerManager playerManager;

    public PostLoginListener(VPLPlugin plugin) {
        this.plugin = plugin; this.eventManager = plugin.getEventManager();
        this.languageFile = plugin.getLanguageFile(); this.provider = plugin.getProvider(); this.playerManager = plugin.getPlayerManager();
    }

    @Override
    public void register() {
        this.eventManager.register(plugin, PostLoginEvent.class, this);
    }

    @Override
    public @Nullable EventTask executeAsync(PostLoginEvent event) {
        return EventTask.async(() -> {
            Player player = event.getPlayer(); String username = player.getUsername(); UUID uniqueID = player.getUniqueId();

            if (!Mojang.premiumUsername(username))
                return;

            if (playerManager.presentInPremium(uniqueID)) {
                plugin.sendMessage(player, languageFile.PREMIUM_LOGIN);

                String address = player.getRemoteAddress().getAddress().getHostAddress();
                provider.presentInIP(uniqueID).thenAccept(checkPresent -> {
                    if (checkPresent) {
                        provider.getUserIP(uniqueID).thenAccept(value -> {
                            if (!value.equals(address))
                                provider.editUser(uniqueID, address);
                        });
                    } else {
                        provider.addUser(uniqueID, address);
                    }
                });
                return;
            }
            plugin.sendMessage(player, languageFile.PREMIUM_NOTIFICATION);
        });
    }
}
