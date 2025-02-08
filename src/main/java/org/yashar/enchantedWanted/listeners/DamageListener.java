package org.yashar.enchantedWanted.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.yashar.enchantedWanted.storages.DatabaseManager;

import static org.yashar.enchantedWanted.utils.MessageUtils.sendMessage;

public class DamageListener implements Listener {

    private final DatabaseManager database;

    public DamageListener(DatabaseManager database) {
        this.database = database;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        if (isHoldingTotem(victim)) {
            return;
        }

        Player killer = victim.getKiller();
        if (killer == null || killer.equals(victim)) {
            return;
        }

        database.addWanted(killer.getUniqueId(), 1);

        sendMessage(killer, "<#ff5733>Hey! You've been added to the wanted list. Current Wanted: %Wanted%</#ff5733>"
                .replace("%Wanted%", String.valueOf(database.getWanted(killer.getUniqueId()))));

        sendMessage(victim, "<#ff5733>You were killed by a wanted player!</#ff5733>");
    }
    private boolean isHoldingTotem(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        return mainHand.getType() == Material.TOTEM_OF_UNDYING ||
                offHand.getType() == Material.TOTEM_OF_UNDYING;
    }
}
