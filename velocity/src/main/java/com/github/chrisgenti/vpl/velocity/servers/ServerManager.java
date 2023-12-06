package com.github.chrisgenti.vpl.velocity.servers;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.servers.enums.SendMode;
import com.google.common.collect.Lists;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager {
    private final VPLPlugin plugin;
    private final Random random = new Random();
    private final Set<String> authServers = ConcurrentHashMap.newKeySet();
    private final Set<String> lobbyServers = ConcurrentHashMap.newKeySet();

    public ServerManager(VPLPlugin plugin) {
        this.plugin = plugin;

        this.authServers.addAll(plugin.getConfigFile().AUTH_SERVERS);
        this.lobbyServers.addAll(plugin.getConfigFile().LOBBY_SERVERS);

        // * SETUP MESSAGE
        plugin.sendMessage(
                "<reset>", "<bold><dark_green>SERVERS</bold>", "<white>The auth lobbies are: " + authServers, "<white>The auth lobbies are: " + lobbyServers
        );
    }

    public RegisteredServer authServer(SendMode sendMode) {
        return this.server(authServers, sendMode);
    }

    public RegisteredServer lobbyServer(SendMode sendMode) {
        return this.server(lobbyServers, sendMode);
    }

    public boolean isAuthServer(RegisteredServer server) {
        return authServers.contains(server.getServerInfo().getName());
    }

    public boolean isLobbyServer(RegisteredServer server) {
        return lobbyServers.contains(server.getServerInfo().getName());
    }

    private RegisteredServer server(Set<String> servers, SendMode sendMode) {
        List<RegisteredServer> onlineServers = Lists.newArrayList();

        for (String value : servers) {
            Optional<RegisteredServer> optional = plugin.getProxy().getServer(value);
            if (optional.isEmpty())
                continue;
            RegisteredServer server = optional.get();

            ServerPing serverPing = server.ping().join();
            if (serverPing != null)
                onlineServers.add(server);
        }

        onlineServers.sort(Comparator.comparing(server -> server.getPlayersConnected().size()));
        if (onlineServers.isEmpty())
            return null;

        switch (sendMode) {
            case TO_EMPTIEST_SERVER -> {
                return onlineServers.get(0);
            }
            case RANDOM -> {
                return onlineServers.get(random.nextInt(onlineServers.size()));
            }
        }
        return onlineServers.get(0);
    }
}
