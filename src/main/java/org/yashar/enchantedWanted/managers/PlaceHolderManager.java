package org.yashar.enchantedWanted.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yashar.enchantedWanted.storages.DatabaseManager;

public class PlaceHolderManager extends PlaceholderExpansion {

    private final DatabaseManager database;

    public PlaceHolderManager(@NotNull DatabaseManager database) {
        this.database = database;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ew";
    }

    @Override
    public @NotNull String getAuthor() {
        return "IRDevs";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return null;

        Bukkit.getLogger().info("Placeholder Requested: " + identifier + " for player: " + player.getName());

        int wantedLevel = Math.max(database.getWanted(player.getUniqueId()), 0);

        Bukkit.getLogger().info("Wanted Level for " + player.getName() + ": " + wantedLevel);

        return switch (identifier) {
            case "wanted" -> String.valueOf(wantedLevel);
            case "formatted_wanted" -> "★".repeat(wantedLevel) + "✩".repeat(6 - wantedLevel);
            default -> null;
        };
    }
}
