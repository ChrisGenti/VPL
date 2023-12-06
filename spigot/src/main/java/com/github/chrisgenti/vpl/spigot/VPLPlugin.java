package com.github.chrisgenti.vpl.spigot;

import com.github.chrisgenti.vpl.common.MessageType;
import com.github.chrisgenti.vpl.spigot.listeners.AuthMeListener;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class VPLPlugin extends JavaPlugin {
    private static final String CHANNEL = "vpl:main";

    @Override
    public void onEnable() {
        /*
            * CHANNELS
         */
        this.registerPluginChannels();

        /*
         * LISTENERS
         */
        this.registerListeners(new AuthMeListener(this));
    }

    @Override
    public void onDisable() {
        /*
            * CHANNELS
         */
        this.unregisterPluginChannels();
    }

    public void sendMessage(Player player, @NotNull MessageType messageType) {
        this.sendMessage(player, player.getName(), messageType);
    }

    public void sendMessage(Player player, @NotNull String username, @NotNull MessageType messageType) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(messageType.toString()); output.writeUTF(username);

        if (player == null)
            return;
        player.sendPluginMessage(this, CHANNEL, output.toByteArray());
    }

    private void registerListeners(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
    }

    private void registerPluginChannels() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
    }

    private void unregisterPluginChannels() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, CHANNEL);
    }
}
