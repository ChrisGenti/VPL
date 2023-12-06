package com.github.chrisgenti.vpl.velocity.listeners.profile;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.data.DataProvider;
import com.github.chrisgenti.vpl.velocity.listeners.Listener;
import com.github.chrisgenti.vpl.velocity.players.PlayerManager;
import com.github.games647.craftapi.UUIDAdapter;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProfileRequestListener implements Listener<GameProfileRequestEvent> {
    private final VPLPlugin plugin;
    private final EventManager eventManager;
    private final DataProvider provider;
    private final PlayerManager playerManager;

    public ProfileRequestListener(VPLPlugin plugin) {
        this.plugin = plugin; this.eventManager = plugin.getEventManager();
        this.provider = plugin.getProvider(); this.playerManager = plugin.getPlayerManager();
    }

    @Override
    public void register() {
        this.eventManager.register(plugin, GameProfileRequestEvent.class, this);
    }

    @Override
    public @Nullable EventTask executeAsync(GameProfileRequestEvent event) {
        return EventTask.async(() -> {
            if (!event.isOnlineMode())
                return;

            String username = event.getUsername(); UUID offlineUUID = UUIDAdapter.generateOfflineId(username);
            if (playerManager.presentInAwaiting(offlineUUID)) {
                provider.editUser(offlineUUID, true).thenAccept(success -> {
                    if (success)
                        playerManager.removeAwaiting(offlineUUID);
                });
            }
            playerManager.addLogin(offlineUUID); playerManager.addPremium(offlineUUID);

            event.setGameProfile(event.getGameProfile().withId(offlineUUID));
            plugin.debug("GameProfileRequestEvent | I set the UUID Offline for "+ username + ".");
        });
    }
}
