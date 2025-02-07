package org.yashar.enchantedWanted.storages;


import java.util.UUID;

public interface DatabaseManager {
    void connect();
    void disconnect();
    void createTable();
    void addWanted(String uuid);
    int getWanted(String uuid);
    boolean isConnected();
}

