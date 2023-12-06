package com.github.chrisgenti.vpl.velocity.listeners.message;

import com.github.chrisgenti.vpl.common.MessageType;
import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.configurations.config.ConfigFile;
import com.github.chrisgenti.vpl.velocity.configurations.language.LanguageFile;
import com.github.chrisgenti.vpl.velocity.data.DataProvider;
import com.github.chrisgenti.vpl.velocity.listeners.Listener;
import com.github.chrisgenti.vpl.velocity.players.PlayerManager;
import com.github.chrisgenti.vpl.velocity.servers.ServerManager;
import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.Index;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Locale;
import java.util.UUID;

public class PluginMessageListener implements Listener<PluginMessageEvent> {
    private static final Index<String, MessageType> TYPES = Index.create(MessageType.class, Enum::toString);

    private final VPLPlugin plugin;
    private final ProxyServer proxy;
    private final EventManager eventManager;
    private final ConfigFile configFile;
    private final LanguageFile languageFile;
    private final DataProvider provider;
    private final PlayerManager playerManager;
    private final ServerManager serverManager;

    public PluginMessageListener(VPLPlugin plugin) {
        this.plugin = plugin; this.proxy = plugin.getProxy(); this.eventManager = plugin.getEventManager();
        this.configFile = plugin.getConfigFile(); this.languageFile = plugin.getLanguageFile(); this.provider = plugin.getProvider(); this.playerManager = plugin.getPlayerManager(); this.serverManager = plugin.getServerManager();
    }

    @Override
    public void register() {
        this.eventManager.register(plugin, PluginMessageEvent.class, this);
    }

    @Override
    public @Nullable EventTask executeAsync(PluginMessageEvent event) {
        return EventTask.async(() -> {
            boolean cancelled = !event.getResult().isAllowed() || !(event.getSource() instanceof ServerConnection) || !(event.getIdentifier().equals(VPLPlugin.MODERN_CHANNEL) || event.getIdentifier().equals(VPLPlugin.LEGACY_CHANNEL));
            if (cancelled)
                return;
            event.setResult(PluginMessageEvent.ForwardResult.handled());

            ByteArrayDataInput input = event.dataAsDataStream();
            MessageType messageType = TYPES.valueOrThrow(input.readUTF().toUpperCase(Locale.ROOT)); String username = input.readUTF(); Player player = proxy.getPlayer(username).orElse(null);

            if (player == null)
                return;

            UUID uniqueID = player.getUniqueId();
            switch (messageType) {
                case LOGIN -> {
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
                    playerManager.addLogin(uniqueID); this.createConnection(player);
                }
                case LOGOUT -> {
                    playerManager.removeLogin(uniqueID);
                }
                case REGISTER -> {
                    provider.addUser(username, uniqueID, false);
                }
                case UNREGISTER -> {
                    playerManager.removeLogin(uniqueID); provider.deleteUser(uniqueID);
                }
                case FORCE_UNREGISTER -> {
                    playerManager.removeLogin(uniqueID); provider.deleteUser(uniqueID);
                    player.disconnect(MiniMessage.miniMessage().deserialize(languageFile.FORCE_UNREGISTERED));
                }
            }
        });
    }

    private void createConnection(Player player) {
        RegisteredServer server = serverManager.lobbyServer(configFile.LOBBY_SEND_MODE);
        if (server == null)
            return;

        player.createConnectionRequest(server)
                .connect();
    }
}
