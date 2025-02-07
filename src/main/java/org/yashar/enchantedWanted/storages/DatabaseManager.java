package org.yashar.enchantedWanted.storages;

public interface DatabaseManager {
    void connect();
    void disconnect();
    void createTable();
    void addWanted(String uuid);
    int getWanted(String uuid);
    boolean isConnected();
}
