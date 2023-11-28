package com.github.chrisgenti.vpl.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;

@Plugin(
        id = "vpl",
        name = "VPL",
        version = "1.0.0",
        description = "",
        authors = {"ChrisGenti"}
)
public class VPLPlugin {
    public VPLPlugin() {

    }

    @Subscribe
    public void onInitialization(ProxyInitializeEvent event) {

    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {

    }
}
