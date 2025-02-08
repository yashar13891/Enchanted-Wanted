package org.yashar.enchantedWanted.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
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
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return null;

        int wantedLevel = Math.max(database.getWanted(player.getUniqueId()), 0);

        return switch (identifier) {
            case "ew_wanted" -> String.valueOf(wantedLevel);
            case "ew_formatted_wanted" -> "★".repeat(wantedLevel) + "✩".repeat(6 - wantedLevel);
            default -> null;
        };
    }
}
