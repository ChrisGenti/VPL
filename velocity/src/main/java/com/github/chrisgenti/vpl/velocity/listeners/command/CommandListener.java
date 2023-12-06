package com.github.chrisgenti.vpl.velocity.listeners.command;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.configurations.config.ConfigFile;
import com.github.chrisgenti.vpl.velocity.configurations.language.LanguageFile;
import com.github.chrisgenti.vpl.velocity.listeners.Listener;
import com.github.chrisgenti.vpl.velocity.players.PlayerManager;
import com.github.chrisgenti.vpl.velocity.servers.ServerManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class CommandListener implements Listener<CommandExecuteEvent> {
    private final VPLPlugin plugin;
    private final EventManager eventManager;
    private final ConfigFile configFile;
    private final LanguageFile languageFile;
    private final PlayerManager playerManager;
    private final ServerManager serverManager;

    public CommandListener(VPLPlugin plugin) {
        this.plugin = plugin; this.eventManager = plugin.getEventManager();
        this.configFile = plugin.getConfigFile(); this.languageFile = plugin.getLanguageFile(); this.playerManager = plugin.getPlayerManager(); this.serverManager = plugin.getServerManager();
    }

    @Override
    public void register() {
        this.eventManager.register(plugin, CommandExecuteEvent.class, PostOrder.FIRST, this);
    }

    @Override
    public @Nullable EventTask executeAsync(CommandExecuteEvent event) {
        return EventTask.withContinuation(continuation -> {
            CommandSource source = event.getCommandSource();
            if (source instanceof ConsoleCommandSource) {
                continuation.resume(); return;
            }

            Player player = (Player) source;
            if (playerManager.presentInLogin(player.getUniqueId())) {
                continuation.resume(); return;
            }

            if (player.getCurrentServer().map(connection -> serverManager.isAuthServer(connection.getServer())).orElse(false)) {
                if (!configFile.ALLOWED_COMMANDS.contains(this.firstArgument(event.getCommand()))) {
                    plugin.debug("CommandExecuteEvent | " + player.getUsername() + " attempted to execute a blocked command.");
                    this.continuation(source, event);
                }
            } else {
                plugin.debug("CommandExecuteEvent | " + player.getUsername() + " attempted to execute a command but is not logged in.");
                this.continuation(source, event);
            }
            continuation.resume();
        });
    }

    private @NotNull String firstArgument(@NotNull String string){
        int value = Objects.requireNonNull(string).indexOf(' ');
        return value == -1 ? string : string.substring(0, value);
    }

    private void continuation(CommandSource source, CommandExecuteEvent event) {
        plugin.sendMessage(source, languageFile.BLOCKED_COMMAND); event.setResult(CommandExecuteEvent.CommandResult.denied());
    }
}
