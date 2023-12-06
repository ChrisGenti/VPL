package com.github.chrisgenti.vpl.velocity.data;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface DataProvider {
    void init();

    CompletableFuture<Boolean> presentInIP(UUID uniqueID);

    CompletableFuture<Boolean> presentInUsers(UUID uniqueID);

    CompletableFuture<String> getUserIP(UUID uniqueID);

    CompletableFuture<Boolean> getUserPremium(UUID uniqueID);

    CompletableFuture<List<String>> getUsers();

    CompletableFuture<List<String>> getUsersByIP(String address);

    CompletableFuture<Boolean> addUser(UUID uniqueID, String address);

    CompletableFuture<Boolean> addUser(String username, UUID uniqueID, boolean premium);

    CompletableFuture<Boolean> editUser(UUID uniqueID, String address);

    CompletableFuture<Boolean> editUser(UUID uniqueID, boolean premium);

    CompletableFuture<Boolean> deleteUser(UUID uniqueID);
}
