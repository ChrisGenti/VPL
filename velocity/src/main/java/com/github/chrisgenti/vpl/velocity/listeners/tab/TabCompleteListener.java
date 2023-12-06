package com.github.chrisgenti.vpl.velocity.listeners.tab;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.configurations.config.ConfigFile;
import com.github.chrisgenti.vpl.velocity.listeners.Listener;
import com.github.chrisgenti.vpl.velocity.players.PlayerManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import com.velocitypowered.api.proxy.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TabCompleteListener implements Listener<TabCompleteEvent> {
    private final VPLPlugin plugin;
    private final EventManager eventManager;
    private final ConfigFile configFile;
    private final PlayerManager playerManager;

    public TabCompleteListener(VPLPlugin plugin) {
        this.plugin = plugin; this.eventManager = plugin.getEventManager();
        this.configFile = plugin.getConfigFile(); this.playerManager = plugin.getPlayerManager();
    }

    @Override
    public void register() {
        this.eventManager.register(plugin, TabCompleteEvent.class, PostOrder.FIRST, this);
    }

    @Override
    public @Nullable EventTask executeAsync(TabCompleteEvent event) {
        return EventTask.async(() -> {
            Player player = event.getPlayer();
            if (playerManager.presentInLogin(player.getUniqueId())) {
                // DEBUG
                return;
            }

            String command = event.getPartialMessage();
            for (String allowed : configFile.ALLOWED_COMMANDS) {
                if (allowed.startsWith(command)) {
                    return;
                }
            }

            plugin.debug("TabCompleteEvent | " + player.getUsername() + " tried to use TabComplete but was blocked.");
            event.getSuggestions().clear();
        });
    }
}
