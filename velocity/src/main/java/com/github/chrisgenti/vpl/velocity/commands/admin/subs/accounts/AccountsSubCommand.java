package com.github.chrisgenti.vpl.velocity.commands.admin.subs.accounts;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.commands.admin.subs.PluginSubCommand;
import com.github.chrisgenti.vpl.velocity.configurations.language.LanguageFile;
import com.github.chrisgenti.vpl.velocity.data.DataProvider;
import com.github.games647.craftapi.UUIDAdapter;
import com.google.common.net.InetAddresses;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AccountsSubCommand implements PluginSubCommand {
    private final VPLPlugin plugin;
    private final ProxyServer proxy;
    private final LanguageFile languageFile;
    private final DataProvider provider;

    public AccountsSubCommand(VPLPlugin plugin) {
        this.plugin = plugin; this.proxy = plugin.getProxy();
        this.languageFile = plugin.getLanguageFile(); this.provider = plugin.getProvider();
    }

    @Override
    public String name() {
        return "accounts";
    }

    @Override
    public String usage() {
        return "/vpl accounts <username/address>";
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public void execute(CommandSource source, String[] arguments) {
        if (arguments.length != 1) {
            plugin.sendMessage(source, languageFile.NO_CORRECT_USAGE.replace("%command%", this.usage()));
            return;
        }
        String value = arguments[0];

        if (InetAddresses.isInetAddress(value)) {
            this.continuation(source, null, value); return;
        }

        UUID uniqueID = UUIDAdapter.generateOfflineId(value);
        provider.presentInIP(uniqueID).thenAccept(checkPresent -> {
            if (checkPresent) {
                provider.getUserIP(uniqueID).thenAccept(address -> this.continuation(source, value, address));
            } else {
                plugin.sendMessage(source, languageFile.PLAYER_NOT_IN_DATABASE.replace("%player%", value));
            }
        });
    }

    @Override
    public boolean hasPermission(SimpleCommand.Invocation invocation) {
        return invocation.source().hasPermission("vpl.accounts");
    }

    private void continuation(CommandSource source, @Nullable String username, String address) {
        provider.getUsersByIP(address).thenAccept(users -> {
            if (users.isEmpty()) {
                plugin.sendMessage(source, languageFile.ADDRESS_NOT_IN_DATABASE.replace("%address%", address));
            } else {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < users.size(); i++) {
                    String value = users.get(i);
                    builder.append(proxy.getPlayer(value).isPresent() ? languageFile.ONLINE : languageFile.OFFLINE).append(value);

                    if (i < users.size() - 1)
                        builder.append(", ");
                }
                plugin.sendMessage(source, languageFile.PLAYER_ACCOUNTS.replace("%value%", username != null ? username : address).replace("%users%", builder.toString()));
            }
        });
    }
}
