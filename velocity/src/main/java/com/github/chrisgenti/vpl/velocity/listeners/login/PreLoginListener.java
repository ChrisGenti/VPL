package com.github.chrisgenti.vpl.velocity.listeners.login;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.data.DataProvider;
import com.github.chrisgenti.vpl.velocity.listeners.Listener;
import com.github.chrisgenti.vpl.velocity.players.PlayerManager;
import com.github.games647.craftapi.UUIDAdapter;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class PreLoginListener implements Listener<PreLoginEvent> {
    private final VPLPlugin plugin;
    private final EventManager eventManager;
    private final DataProvider provider;
    private final PlayerManager playerManager;

    public PreLoginListener(VPLPlugin plugin) {
        this.plugin = plugin; this.eventManager = plugin.getEventManager();
        this.provider = plugin.getProvider(); this.playerManager = plugin.getPlayerManager();
    }

    @Override
    public void register() {
        this.eventManager.register(plugin, PreLoginEvent.class, this);
    }

    @Override
    public @Nullable EventTask executeAsync(PreLoginEvent event) {
        return EventTask.async(() -> {
            String username = event.getUsername(); UUID offlineUUID = UUIDAdapter.generateOfflineId(username);

            if (!playerManager.presentInPremium(offlineUUID)) {
                provider.getUserPremium(offlineUUID).thenAccept(premium -> {
                    if (premium)
                        playerManager.addPremium(offlineUUID);
                });
            }

            if (playerManager.presentInPremium(offlineUUID) || playerManager.presentInAwaiting(offlineUUID)) {
                plugin.debug("PreLoginEvent | Forcing online mode for " + username + ".");
                event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
            }
        });
    }
}
