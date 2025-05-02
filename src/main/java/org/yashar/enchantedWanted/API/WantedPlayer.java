package org.yashar.enchantedWanted.API;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.yashar.enchantedWanted.storages.DatabaseManager;

import java.util.UUID;

@RequiredArgsConstructor
public class WantedPlayer {
    private static final String PREFIX = "&8[&eEW&8]";

    private final DatabaseManager databaseManager;

    public int getWantedLevel(@NonNull UUID uuid) {
        return databaseManager.getWanted(uuid);
    }

    public void setWantedLevel(@NonNull UUID uuid, int level) {
        validateLevel(level);
        databaseManager.setWanted(uuid, level);
    }

    public void addWantedLevel(@NonNull UUID uuid, int level) {
        validateLevel(level);
        databaseManager.addWanted(uuid, level);
    }

    public void removeWantedLevel(@NonNull UUID uuid, int level) {
        validateLevel(level);
        databaseManager.removeWanted(uuid, level);
    }

    public void clearWantedLevels(@NonNull UUID uuid) {
        databaseManager.setWanted(uuid, 0);
    }

    private void validateLevel(int level) {
        if (level < 0) {
            throw new IllegalArgumentException(PREFIX + "[WantedAPI] Level cannot be negative: " + level);
        }
    }
}
