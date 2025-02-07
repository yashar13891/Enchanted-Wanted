package org.yashar.enchantedWanted.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import static org.yashar.enchantedWanted.utils.MessageUtils.sendMessage;

public class DamageListener implements Listener {

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
            sendMessage(victim, "Hey KosKesh Wanted Gerfti");
        }
    }
}