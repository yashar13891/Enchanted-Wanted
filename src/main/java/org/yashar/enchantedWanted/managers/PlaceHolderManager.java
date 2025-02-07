package org.yashar.enchantedWanted.managers;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yashar.enchantedWanted.EnchantedWanted;
import org.yashar.enchantedWanted.storages.DatabaseManager;

import java.util.List;

public class PlaceHolderManager extends PlaceholderExpansion {

    private final EnchantedWanted plugin;
    private DatabaseManager database;
    public PlaceHolderManager(EnchantedWanted plugin) {
        this.plugin = plugin;
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
                StringBuilder stars = new StringBuilder();
                for (int i = 0; i < wantedLevel; i++) {
                    stars.append("★");
                }
                return stars.toString();
        }

        return null;
    }
}

