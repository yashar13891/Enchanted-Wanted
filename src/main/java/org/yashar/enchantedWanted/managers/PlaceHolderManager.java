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
    private int getplayerwanted;
    public PlaceHolderManager(EnchantedWanted plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "enchantedwanted";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Yashar, Misagh, Vesder";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        switch (identifier) {
            case "%enchantedwanted_profile_wanted%":
                return String.valueOf(database.getWanted(player.getUniqueId()));
            case "%enchantedwanted_profile_wanted_formatted%":
                return
        }

        return null;
    }
}
