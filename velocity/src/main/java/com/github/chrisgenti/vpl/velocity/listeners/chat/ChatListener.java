package com.github.chrisgenti.vpl.velocity.listeners.chat;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.configurations.config.ConfigFile;
import com.github.chrisgenti.vpl.velocity.configurations.language.LanguageFile;
import com.github.chrisgenti.vpl.velocity.listeners.Listener;
import com.github.chrisgenti.vpl.velocity.players.PlayerManager;
import com.github.chrisgenti.vpl.velocity.servers.ServerManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ChatListener implements Listener<PlayerChatEvent> {
    private final VPLPlugin plugin;
    private final EventManager eventManager;
    private final PlayerManager playerManager;

    public ChatListener(VPLPlugin plugin) {
        this.plugin = plugin; this.eventManager = plugin.getEventManager();
        this.playerManager = plugin.getPlayerManager();
    }

    @Override
    public void register() {
        this.eventManager.register(plugin, PlayerChatEvent.class, PostOrder.FIRST, this);
    }

    @Override
    public @Nullable EventTask executeAsync(PlayerChatEvent event) {
        return EventTask.withContinuation(continuation -> {
            Player player = event.getPlayer();
            if (playerManager.presentInLogin(player.getUniqueId())) {
                continuation.resume(); return;
            }

            plugin.debug("PlayerChatEvent | " + player.getUsername() + " tried to send a message but is not logged in.");
            event.setResult(PlayerChatEvent.ChatResult.denied()); continuation.resume();
        });
    }
}
