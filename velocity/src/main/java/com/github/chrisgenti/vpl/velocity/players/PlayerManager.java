package com.github.chrisgenti.vpl.velocity.players;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.Currency;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PlayerManager {
    private final Set<UUID> loggedPlayers = ConcurrentHashMap.newKeySet();
    private final Set<UUID> premiumPlayers = ConcurrentHashMap.newKeySet();
    private final Set<UUID> confirmationPlayers = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Long> awaitingPlayers = Maps.newHashMap();

    public boolean presentInLogin(UUID uniqueID) {
        return this.loggedPlayers.contains(uniqueID);
    }

    public void addLogin(UUID uniqueID) {
        this.loggedPlayers.add(uniqueID);
    }

    public void removeLogin(UUID uniqueID) {
        this.loggedPlayers.remove(uniqueID);
    }

    public boolean presentInPremium(UUID uniqueID) {
        return this.premiumPlayers.contains(uniqueID);
    }

    public void addPremium(UUID uniqueID) {
        this.premiumPlayers.add(uniqueID);
    }

    public void removePremium(UUID uniqueID) {
        this.premiumPlayers.remove(uniqueID);
    }

    public boolean presentInConfirmation(UUID uniqueID) {
        return this.confirmationPlayers.contains(uniqueID);
    }

    public void addConfirmation(UUID uniqueID) {
        this.confirmationPlayers.add(uniqueID);
    }

    public void removeConfirmation(UUID uniqueID) {
        this.confirmationPlayers.remove(uniqueID);
    }

    public boolean presentInAwaiting(UUID uniqueID) {
        return this.awaitingPlayers.containsKey(uniqueID);
    }

    public void addAwaiting(UUID uniqueID) {
        this.awaitingPlayers.put(uniqueID, System.currentTimeMillis());
    }

    public void removeAwaiting(UUID uniqueID) {
        this.awaitingPlayers.remove(uniqueID);
    }
}
