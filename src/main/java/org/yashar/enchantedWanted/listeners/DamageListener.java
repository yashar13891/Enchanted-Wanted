package org.yashar.enchantedWanted.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.yashar.enchantedWanted.storages.DatabaseManager;

import static org.bukkit.Bukkit.broadcastMessage;
import static org.yashar.enchantedWanted.utils.MessageUtils.sendMessage;

public class DamageListener implements Listener {

    private final DatabaseManager database;

    public DamageListener(DatabaseManager database) {
        this.database = database;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDmg(EntityDamageEvent e) {

        ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);

        if (e.isCancelled() || !(e.getEntity() instanceof Player victim) ||
                victim.getInventory().getItemInMainHand().equals(totem) ||
                victim.getInventory().getItemInOffHand().equals(totem)) {

            broadcastMessage(ChatColor.RED + "Return Shod Chon Rquerment Nadasht !!!");

            return;
        }

        if (victim.getHealth() - e.getFinalDamage() <= 0) {
            if (victim.getKiller() == null) {
                broadcastMessage(ChatColor.RED + "Return Shod Killer Pida Nashod !!!");
                return;
            }

            Player killer = victim.getKiller();

            database.addWanted(killer.getUniqueId(),1);
            sendMessage(killer, "<#ff5733>Hey!, Kiri Wanted Gerfti Va Alan %Wanted% Dari</#ff5733>".replace("%Wanted%",String.valueOf(database.getWanted(killer.getUniqueId()))));
            sendMessage(victim,"<#ff5733>Salam Ye Koni Toro Kosht Mikhai Azsh Shekait Kni ???</#ff5733>");
        }

        broadcastMessage(ChatColor.RED + "Dmg Listener Ended !!!");
    }
}
