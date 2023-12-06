package com.github.chrisgenti.vpl.spigot.listeners;

import com.github.chrisgenti.vpl.common.MessageType;
import com.github.chrisgenti.vpl.spigot.VPLPlugin;
import fr.xephi.authme.events.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AuthMeListener implements Listener {
    private final VPLPlugin plugin;

    public AuthMeListener(VPLPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(LoginEvent event) {
        plugin.sendMessage(event.getPlayer(), MessageType.LOGIN);
    }

    @EventHandler
    public void onLogout(LogoutEvent event) {
        plugin.sendMessage(event.getPlayer(), MessageType.LOGOUT);
    }

    @EventHandler
    public void onRegister(RegisterEvent event) {
        plugin.sendMessage(event.getPlayer(), MessageType.REGISTER);
    }

    @EventHandler
    public void onUnRegister(UnregisterByPlayerEvent event) {
        plugin.sendMessage(event.getPlayer(), MessageType.UNREGISTER);
    }

    @EventHandler
    public void onAdminUnRegister(UnregisterByAdminEvent event) {
        plugin.sendMessage(event.getPlayer(), event.getPlayerName(), MessageType.FORCE_UNREGISTER);
    }
}
