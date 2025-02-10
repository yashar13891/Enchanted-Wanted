package org.yashar.enchantedWanted.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yashar.enchantedWanted.EnchantedWanted;
import org.yashar.enchantedWanted.storages.DatabaseManager;
import org.yashar.enchantedWanted.utils.MessageUtils;

public class PlaceHolderManager extends PlaceholderExpansion {

    private final DatabaseManager database;
    private final FileConfiguration config;

    public PlaceHolderManager(@NotNull DatabaseManager database, @NotNull EnchantedWanted plugin) {
        this.database = database;
        this.config = plugin.getConfig();
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

        String filledStar = config.getString("wanted.filled", "&c★");
        String emptyStar = config.getString("wanted.empty", "&7✩");
        String wantedColor = config.getString("wanted.number", "&e");

        String formattedWanted = MessageUtils.convertLegacyCodesToMiniMessage(filledStar).repeat(wantedLevel)
                + MessageUtils.convertLegacyCodesToMiniMessage(emptyStar).repeat(6 - wantedLevel);
        String wantedNumber = MessageUtils.convertLegacyCodesToMiniMessage(wantedColor) + wantedLevel;

        return switch (identifier) {
            case "wanted" -> wantedNumber;
            case "formatted_wanted" -> formattedWanted;
            default -> null;
        };
    }
}
