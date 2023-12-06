package com.github.chrisgenti.vpl.velocity;

import com.github.chrisgenti.vpl.velocity.commands.PluginCommand;
import com.github.chrisgenti.vpl.velocity.commands.admin.VPLCommand;
import com.github.chrisgenti.vpl.velocity.commands.user.PremiumCommand;
import com.github.chrisgenti.vpl.velocity.configurations.config.ConfigFile;
import com.github.chrisgenti.vpl.velocity.configurations.language.LanguageFile;
import com.github.chrisgenti.vpl.velocity.data.DataProvider;
import com.github.chrisgenti.vpl.velocity.data.mysql.MySQLProvider;
import com.github.chrisgenti.vpl.velocity.listeners.Listener;
import com.github.chrisgenti.vpl.velocity.listeners.chat.ChatListener;
import com.github.chrisgenti.vpl.velocity.listeners.command.CommandListener;
import com.github.chrisgenti.vpl.velocity.listeners.disconnect.DisconnectListener;
import com.github.chrisgenti.vpl.velocity.listeners.login.PreLoginListener;
import com.github.chrisgenti.vpl.velocity.listeners.login.post.PostLoginListener;
import com.github.chrisgenti.vpl.velocity.listeners.message.PluginMessageListener;
import com.github.chrisgenti.vpl.velocity.listeners.profile.ProfileRequestListener;
import com.github.chrisgenti.vpl.velocity.listeners.server.InitialServerListener;
import com.github.chrisgenti.vpl.velocity.listeners.tab.TabCompleteListener;
import com.github.chrisgenti.vpl.velocity.players.PlayerManager;
import com.github.chrisgenti.vpl.velocity.servers.ServerManager;
import com.github.chrisgenti.vpl.velocity.tasks.PluginTask;
import com.github.chrisgenti.vpl.velocity.tasks.register.RegisterTask;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

@Plugin(
        id = "vpl",
        name = "VPL",
        version = "1.0.0",
        description = "",
        authors = {"ChrisGenti"}
) @Getter
public class VPLPlugin {
    public static final ChannelIdentifier MODERN_CHANNEL = MinecraftChannelIdentifier.create("vpl", "main");
    public static final ChannelIdentifier LEGACY_CHANNEL = new LegacyChannelIdentifier("vpl:main");

    @Inject private ProxyServer proxy;
    @Inject private Logger logger;
    @Inject private EventManager eventManager;
    @Inject @DataDirectory Path directory;

    private ConfigFile configFile;
    private LanguageFile languageFile;

    private PlayerManager playerManager;
    private ServerManager serverManager;
    private DataProvider provider;

    private PluginTask registerTask;

    @Subscribe
    public void onInitialization(ProxyInitializeEvent event) {
        /*
            * FILES
         */
        this.configFile = new ConfigFile(directory.toFile(), "config.toml");
        this.languageFile = new LanguageFile(new File(directory.toFile(), "lang"), configFile.LANGUAGE);

        /*
            * SETUP MESSAGE
         */
        this.sendMessage(
                "<reset>", "<bold><aqua>VPL  |  VELOCITY PREMIUM LOGIN</bold>"
        );

        /*
            * MANAGERS
         */
        this.playerManager = new PlayerManager();
        this.serverManager = new ServerManager(this);

        /*
            * PROVIDERS
         */
        this.provider = new MySQLProvider(this, configFile.CREDENTIALS);
        this.provider.init();

        /*
            * COMMANDS
         */
        this.registerCommands(
                new VPLCommand(this), new PremiumCommand(this)
        );

        /*
         * LISTENERS
         */
        this.registerListeners(
            new PluginMessageListener(this),
            new ChatListener(this), new CommandListener(this), new TabCompleteListener(this),
            new PreLoginListener(this), new ProfileRequestListener(this), new InitialServerListener(this), new PostLoginListener(this), new DisconnectListener(this)
        );

        /*
            * TASKS
         */
        this.registerTask = new RegisterTask(this);
        this.registerTask.run();

        /*
            * CHANNELS
         */
        this.registerPluginChannels();
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        /*
            * TASKS
         */
        this.registerTask.stop();

        /*
            * CHANNELS
         */
        this.unregisterPluginChannels();
    }

    public void debug(String message) {
        if (configFile.DEBUG)
            logger.info("[DEBUG] {}", message);
    }

    public void sendMessage(CommandSource source, String message) {
        if (!message.isEmpty())
            source.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    public void sendMessage(String... messages) {
        CommandSource source = proxy.getConsoleCommandSource();
        Arrays.stream(messages).forEach(message -> this.sendMessage(source, message));
    }

    private void registerCommands(PluginCommand... commands) {
        CommandManager manager = proxy.getCommandManager();

        Arrays.stream(commands).forEach(command -> {
            CommandMeta commandMeta = manager.metaBuilder(command.name())
                    .plugin(this)
                    .build();
            manager.register(commandMeta, command);
        });
    }

    private void registerListeners(Listener<?>... listeners) {
        Arrays.stream(listeners).forEach(Listener::register);
    }

    private void registerPluginChannels() {
        this.proxy.getChannelRegistrar().register(MODERN_CHANNEL, LEGACY_CHANNEL);
    }

    private void unregisterPluginChannels() {
        this.proxy.getChannelRegistrar().unregister(MODERN_CHANNEL, LEGACY_CHANNEL);
    }
}
