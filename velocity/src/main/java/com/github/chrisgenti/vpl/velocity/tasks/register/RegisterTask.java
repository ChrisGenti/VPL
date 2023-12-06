package com.github.chrisgenti.vpl.velocity.tasks.register;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.configurations.config.ConfigFile;
import com.github.chrisgenti.vpl.velocity.players.PlayerManager;
import com.github.chrisgenti.vpl.velocity.tasks.PluginTask;
import com.velocitypowered.api.scheduler.ScheduledTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RegisterTask implements PluginTask {
    private final VPLPlugin plugin;
    private final ConfigFile configFile;
    private final PlayerManager playerManager;

    private ScheduledTask scheduledTask;

    public RegisterTask(VPLPlugin plugin) {
        this.plugin = plugin; this.configFile = plugin.getConfigFile(); this.playerManager = plugin.getPlayerManager();
    }

    @Override
    public void run() {
        scheduledTask = plugin.getProxy().getScheduler()
                .buildTask(plugin, () -> {
                    for (Map.Entry<UUID, Long> entry : playerManager.getAwaitingPlayers().entrySet()) {
                        if ((entry.getValue() + TimeUnit.MINUTES.toMillis(configFile.CONFIRMATION_TIME)) <= System.currentTimeMillis())
                            playerManager.removeAwaiting(entry.getKey());
                    }
                })
                .repeat(1L, TimeUnit.SECONDS)
                .schedule();
    }

    @Override
    public void stop() {
        if (scheduledTask != null)
            scheduledTask.cancel();
    }
}
