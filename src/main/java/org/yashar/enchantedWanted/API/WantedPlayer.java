package org.yashar.enchantedWanted.API;

import org.yashar.enchantedWanted.storages.DatabaseManager;

import java.util.UUID;

public class WantedPlayer {
    DatabaseManager databaseManager;
    public WantedPlayer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public int getWantedLevel(UUID uuid) {
        return databaseManager.getWanted(uuid);
    }
    public void setWantedLevel(UUID uuid, int level) {
        if (level < 0) {
            throw new IllegalArgumentException("[WantedAPI] Level cannot be negative!");
        } else if (uuid == null) {
            throw new IllegalArgumentException("[WantedAPI] UUID cannot be null!");
        }
        databaseManager.setWanted(uuid, level);
    }
    public void addWantedLevel(UUID uuid,int level) {
        if (level < 0) {
            throw new IllegalArgumentException("[WantedAPI] Level cannot be negative!");
        } else if (uuid == null) {
            throw new IllegalArgumentException("[WantedAPI] UUID cannot be null!");
        }
        databaseManager.addWanted(uuid,level);
    }
    public void removeWantedLevel(UUID uuid, int level) {
        if (level < 0) {
            throw new IllegalArgumentException("[WantedAPI] Level cannot be negative!");
        } else if (uuid == null) {
            throw new IllegalArgumentException("[WantedAPI] UUID cannot be null!");
        }
        databaseManager.removeWanted(uuid,level);
    }
    public void clearWantedLevels(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("[WantedAPI] UUID cannot be null!");
        }
        databaseManager.setWanted(uuid,0);
    }
}
