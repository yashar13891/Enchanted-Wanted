package org.yashar.enchantedWanted.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import org.yashar.enchantedWanted.storages.DatabaseManager;

import java.util.List;
import java.util.UUID;

public class WantedGUI implements Listener {
    private static DatabaseManager database;

    public WantedGUI(DatabaseManager database) {
        this.database = database;
    }

    public static void openWantedMenu(Player player) {
        int size = 54;
        Inventory gui = Bukkit.createInventory(null, size, "§eᴡᴀɴᴛᴇᴅs");

        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < size; i++) {
            if (i < 9 || i >= size - 9 || i % 9 == 0 || (i + 1) % 9 == 0) {
                gui.setItem(i, glass);
            }
        }

        int slot = 9;

        for (Player target : Bukkit.getOnlinePlayers()) {
            UUID uuid = target.getUniqueId();
            int wantedLevel = database.getWanted(uuid);

            if (wantedLevel > 0) {

                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                assert meta != null;
                meta.setOwningPlayer(target);
                meta.setDisplayName("§c " + target.getName());
                meta.setLore(List.of("§bᴡᴀɴᴛᴇᴅ ᴄᴏᴜɴᴛ§7: §4" + wantedLevel));
                skull.setItemMeta(meta);

                gui.setItem(slot++, skull);
                if (slot >= size - 9) break;
            }
        }

        player.openInventory(gui);
    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§eᴡᴀɴᴛᴇᴅs")) {
            event.setCancelled(true);
        }
    }
}
