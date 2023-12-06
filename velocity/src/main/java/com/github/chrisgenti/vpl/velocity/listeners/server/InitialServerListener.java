package com.github.chrisgenti.vpl.velocity.listeners.server;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.configurations.config.ConfigFile;
import com.github.chrisgenti.vpl.velocity.listeners.Listener;
import com.github.chrisgenti.vpl.velocity.players.PlayerManager;
import com.github.chrisgenti.vpl.velocity.servers.ServerManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class InitialServerListener implements Listener<PlayerChooseInitialServerEvent> {
    private final VPLPlugin plugin;
    private final EventManager eventManager;
    private final ConfigFile configFile;
    private final PlayerManager playerManager;
    private final ServerManager serverManager;

    public InitialServerListener(VPLPlugin plugin) {
        this.plugin = plugin; this.eventManager = plugin.getEventManager();
        this.configFile = plugin.getConfigFile(); this.playerManager = plugin.getPlayerManager(); this.serverManager = plugin.getServerManager();
    }

    @Override
    public void register() {
        this.eventManager.register(plugin, PlayerChooseInitialServerEvent.class, PostOrder.LATE, this);
    }

    @Override
    public @Nullable EventTask executeAsync(PlayerChooseInitialServerEvent event) {
        return EventTask.withContinuation(continuation -> {
            UUID uniqueID = event.getPlayer().getUniqueId();

            RegisteredServer server = playerManager.presentInPremium(uniqueID) ? serverManager.lobbyServer(configFile.LOBBY_SEND_MODE) : serverManager.authServer(configFile.AUTH_SEND_MODE);
            event.setInitialServer(server); continuation.resume();
        });
    }
}
