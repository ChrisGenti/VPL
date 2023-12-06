package com.github.chrisgenti.vpl.velocity.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class Mojang {
    @SuppressWarnings("CallToPrintStackTrace")
    public static boolean premiumUsername(String username) {
        CompletableFuture<Boolean> completable = CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username); BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

                String var; StringBuilder builder = new StringBuilder();
                while ((var = reader.readLine()) != null)
                    builder.append(var);
                return !builder.toString().isEmpty();
            } catch (IOException exception) {
                return false;
            }
        });

        return completable.whenComplete((unused, throwable) -> {
            if (throwable != null)
                throwable.printStackTrace();
        }).join();
    }
}
