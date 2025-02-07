package org.yashar.enchantedWanted.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.yashar.enchantedWanted.storages.DatabaseManager;

import static org.yashar.enchantedWanted.utils.MessageUtils.sendMessage;

public class DamageListener implements Listener {

    private DatabaseManager database;

    public DamageListener(DatabaseManager database) {
        this.database = database;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onDmg(EntityDamageEvent e) {

        ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);

        if (e.isCancelled() || !(e.getEntity() instanceof Player victim) ||
                victim.getInventory().getItemInMainHand().equals(totem) ||
                victim.getInventory().getItemInOffHand().equals(totem)) {

            return;
        }

        if (victim.getHealth() - e.getFinalDamage() <= 0) {
            if (victim.getKiller() == null) {
                return;
            }

            Player killer = victim.getKiller();

            database.addWanted(killer.getUniqueId(),1);
            sendMessage(killer, "&cHey!, &7Kiri Wanted Gerfti Va Alan &e%Wanted%&7 Dari".replace("%Wanted%",String.valueOf(database.getWanted(killer.getUniqueId()))));
            sendMessage(victim,"Salam Ye Koni Toro Kosht Mikhai Azsh Shekait Kni ???");
        }
    }
}
