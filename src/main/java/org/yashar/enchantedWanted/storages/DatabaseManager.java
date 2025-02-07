package org.yashar.enchantedWanted.storages;

import java.util.UUID;

public interface DatabaseManager {
    void connect();

    void disconnect();

    void createTable();

    boolean isConnected();

    void addWanted(UUID uuid,  int amount);

    int getWanted(UUID uuid);

    void removeWanted(UUID uuid, int amount);

    void setWanted(UUID uuid, int level);
}

