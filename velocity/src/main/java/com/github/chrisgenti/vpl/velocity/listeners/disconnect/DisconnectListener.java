package com.github.chrisgenti.vpl.velocity.listeners.disconnect;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.listeners.Listener;
import com.github.chrisgenti.vpl.velocity.players.PlayerManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class DisconnectListener implements Listener<DisconnectEvent> {
    private final VPLPlugin plugin;
    private final EventManager eventManager;
    private final PlayerManager playerManager;

    public DisconnectListener(VPLPlugin plugin) {
        this.plugin = plugin; this.eventManager = plugin.getEventManager();
        this.playerManager = plugin.getPlayerManager();
    }

    @Override
    public void register() {
        this.eventManager.register(plugin, DisconnectEvent.class, this);
    }

    @Override
    public @Nullable EventTask executeAsync(DisconnectEvent event) {
        if (event.getLoginStatus() == DisconnectEvent.LoginStatus.CONFLICTING_LOGIN)
            return null;
        return EventTask.async(() -> {
            UUID uniqueID = event.getPlayer().getUniqueId();

            if (playerManager.presentInConfirmation(uniqueID))
                playerManager.removeConfirmation(uniqueID);

            if (playerManager.presentInLogin(uniqueID))
                playerManager.removeAwaiting(uniqueID);

            if (playerManager.presentInPremium(uniqueID))
                playerManager.removePremium(uniqueID);
        });
    }
}
