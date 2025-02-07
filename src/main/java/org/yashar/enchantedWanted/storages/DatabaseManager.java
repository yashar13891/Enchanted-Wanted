package org.yashar.enchantedWanted.storages;

import java.util.UUID;

public interface DatabaseManager {
    void connect();
    void disconnect();
    void createTable();
    boolean isConnected();
    void addWanted(UUID uuid);
    int getWanted(UUID uuid);
}
