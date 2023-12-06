package com.github.chrisgenti.vpl.velocity.configurations.config;

import com.github.chrisgenti.vpl.velocity.configurations.TomlFile;
import com.github.chrisgenti.vpl.velocity.configurations.language.enums.LanguageType;
import com.github.chrisgenti.vpl.velocity.data.mysql.credentials.Credentials;
import com.github.chrisgenti.vpl.velocity.servers.enums.SendMode;

import java.io.File;
import java.util.List;

public class ConfigFile extends TomlFile {
    public final LanguageType LANGUAGE;
    public final List<String> ALLOWED_COMMANDS;
    public final int CONFIRMATION_TIME;
    public final boolean DEBUG;

    public final SendMode AUTH_SEND_MODE;
    public final List<String> AUTH_SERVERS;

    public final SendMode LOBBY_SEND_MODE;
    public final List<String> LOBBY_SERVERS;

    public final Credentials CREDENTIALS;

    public ConfigFile(File directory, String name) {
        super(directory, name);

        this.LANGUAGE = LanguageType.valueOf(tomlRoot.getString("settings.language"));
        this.ALLOWED_COMMANDS = tomlRoot.getList("settings.allowed_commands");
        this.CONFIRMATION_TIME = tomlRoot.getLong("settings.confirmation_time").intValue();
        this.DEBUG = tomlRoot.getBoolean("settings.debug");

        this.AUTH_SEND_MODE = SendMode.valueOf(tomlRoot.getString("auth_lobbies.send_mode"));
        this.AUTH_SERVERS = tomlRoot.getList("auth_lobbies.servers");

        this.LOBBY_SEND_MODE = SendMode.valueOf(tomlRoot.getString("lobbies.send_mode"));
        this.LOBBY_SERVERS = tomlRoot.getList("lobbies.servers");

        this.CREDENTIALS = new Credentials(
                tomlRoot.getString("database.hostname"), tomlRoot.getLong("database.port").intValue(), tomlRoot.getString("database.database"), tomlRoot.getString("database.username"), tomlRoot.getString("database.password")
        );
    }
}

