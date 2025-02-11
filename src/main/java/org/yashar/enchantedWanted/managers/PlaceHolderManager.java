package org.yashar.enchantedWanted.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yashar.enchantedWanted.EnchantedWanted;
import org.yashar.enchantedWanted.storages.DatabaseManager;
import org.yashar.enchantedWanted.utils.MessageUtils;

public class PlaceHolderManager extends PlaceholderExpansion {

    private final DatabaseManager database;

    public PlaceHolderManager(@NotNull DatabaseManager database, @NotNull EnchantedWanted plugin) {
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

        int maxWanted = EnchantedWanted.getInstance().getConfig().getInt("wanted.max", 6);
        int wantedLevel = Math.min(maxWanted, Math.max(database.getWanted(player.getUniqueId()), 0));

        String filledStar = EnchantedWanted.getInstance().getConfig().getString("wanted.filled", "&c★");
        String emptyStar = EnchantedWanted.getInstance().getConfig().getString("wanted.empty", "&7✩");
        String wantedColor = EnchantedWanted.getInstance().getConfig().getString("wanted.number", "&e");

        String formattedWanted = MessageUtils.colorize(filledStar.repeat(wantedLevel))
                + MessageUtils.colorize(emptyStar.repeat(maxWanted - wantedLevel));
        String wantedNumber = MessageUtils.colorize(wantedColor + wantedLevel);

        return switch (identifier) {
            case "wanted" -> wantedNumber;
            case "formatted_wanted" -> formattedWanted;
            default -> null;
        };
    }
}
