package com.github.chrisgenti.vpl.velocity.configurations.language;

import com.github.chrisgenti.vpl.velocity.configurations.TomlFile;
import com.github.chrisgenti.vpl.velocity.configurations.language.enums.LanguageType;

import java.io.File;

public class LanguageFile extends TomlFile {
    public final String NO_CORRECT_USAGE, BLOCKED_COMMAND, NO_SUB_COMMAND, NO_PERMISSION, NO_CONSOLE, FIND_ERROR;

    public final String PREMIUM_NOTIFICATION, NO_PREMIUM_USERNAME, FORCE_UNREGISTERED, PREMIUM_RECONNECT, ALREADY_PREMIUM, PREMIUM_CONFIRM, PREMIUM_LOGIN;

    public final String ADDRESS_NOT_IN_DATABASE, PLAYER_NOT_IN_DATABASE, PLAYER_NOT_PREMIUM, PREMIUM_DISABLED, PLAYER_ACCOUNTS, PREMIUM_CHECK, PREMIUM_STATS, MAIN_INFO;

    public final String DISABLED, ENABLED, OFFLINE, ONLINE;

    public LanguageFile(File directory, LanguageType languageType) {
        super(directory, languageType.getValue());

        this.NO_CORRECT_USAGE = tomlRoot.getString("no_correct_usage");
        this.BLOCKED_COMMAND = tomlRoot.getString("blocked_command");
        this.NO_SUB_COMMAND = tomlRoot.getString("no_sub_command");
        this.NO_PERMISSION = tomlRoot.getString("no_permission");
        this.NO_CONSOLE = tomlRoot.getString("no_console");
        this.FIND_ERROR = tomlRoot.getString("find_error");

        this.PREMIUM_NOTIFICATION = tomlRoot.getString("premium_notification");
        this.NO_PREMIUM_USERNAME = tomlRoot.getString("no_premium_username");
        this.FORCE_UNREGISTERED = tomlRoot.getString("force_unregistered");
        this.PREMIUM_RECONNECT = tomlRoot.getString("premium_reconnect");
        this.ALREADY_PREMIUM = tomlRoot.getString("already_premium");
        this.PREMIUM_CONFIRM = tomlRoot.getString("premium_confirm");
        this.PREMIUM_LOGIN = tomlRoot.getString("premium_login");

        this.ADDRESS_NOT_IN_DATABASE = tomlRoot.getString("address_not_in_database");
        this.PLAYER_NOT_IN_DATABASE = tomlRoot.getString("player_not_in_database");
        this.PLAYER_NOT_PREMIUM = tomlRoot.getString("player_not_premium");
        this.PREMIUM_DISABLED = tomlRoot.getString("premium_disabled");
        this.PLAYER_ACCOUNTS = tomlRoot.getString("player_accounts");
        this.PREMIUM_CHECK = tomlRoot.getString("premium_check");
        this.PREMIUM_STATS = tomlRoot.getString("premium_stats");
        this.MAIN_INFO = tomlRoot.getString("main_info");

        this.DISABLED = tomlRoot.getString("premium.disabled");
        this.ENABLED = tomlRoot.getString("premium.enabled");
        this.OFFLINE = tomlRoot.getString("account.offline");
        this.ONLINE = tomlRoot.getString("account.online");
    }
}
