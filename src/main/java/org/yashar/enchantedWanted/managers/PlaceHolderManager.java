package org.yashar.enchantedWanted.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yashar.enchantedWanted.storages.DatabaseManager;

public class PlaceHolderManager extends PlaceholderExpansion {

    private final DatabaseManager database;

    public PlaceHolderManager(DatabaseManager database) {
        this.database = database;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ew";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Yashar, Misagh, Vesder";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        switch (identifier) {
            case "%ew_wanted%":
                return String.valueOf(database.getWanted(player.getUniqueId()));
            case "%ew_formatted_wanted%":
                if (player == null) return "";

                int wantedLevel = database.getWanted(player.getUniqueId());
                wantedLevel = Math.max(wantedLevel, 0);
                return "★".repeat(wantedLevel);
        }

        return null;
    }
}