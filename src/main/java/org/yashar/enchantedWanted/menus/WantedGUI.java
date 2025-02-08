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
import org.bukkit.inventory.meta.ItemMeta;

import org.yashar.enchantedWanted.storages.DatabaseManager;

import java.util.*;

public class WantedGUI implements Listener {
    private static DatabaseManager database;
    private static final Map<UUID, Integer> playerPages = new HashMap<>();

    public WantedGUI(DatabaseManager database) {
        WantedGUI.database = database;
    }

    public static void openWantedMenu(Player player, int page) {
        int size = 54;
        Inventory gui = Bukkit.createInventory(null, size, "§eᴡᴀɴᴛᴇᴅs - Page " + (page + 1));

        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < size; i++) {
            if (i < 9 || i >= size - 9 || i % 9 == 0 || (i + 1) % 9 == 0) {
                gui.setItem(i, glass);
            }
        }

        List<Integer> emptySlots = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (gui.getItem(i) == null) {
                emptySlots.add(i);
            }
        }

        List<Player> wantedPlayers = new ArrayList<>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (database.getWanted(target.getUniqueId()) > 0) {
                wantedPlayers.add(target);
            }
        }

        int startIndex = page * emptySlots.size();
        int endIndex = Math.min(startIndex + emptySlots.size(), wantedPlayers.size());
        int index = 0;

        for (int i = startIndex; i < endIndex; i++) {
            Player target = wantedPlayers.get(i);
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            assert meta != null;
            meta.setOwningPlayer(target);
            meta.setDisplayName("§c " + target.getName());
            meta.setLore(List.of("§bᴡᴀɴᴛᴇᴅ ᴄᴏᴜɴᴛ§7: §4" + database.getWanted(target.getUniqueId())));
            skull.setItemMeta(meta);
            gui.setItem(emptySlots.get(index), skull);
            index++;
        }

        if (endIndex < wantedPlayers.size()) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta meta = nextPage.getItemMeta();
            assert meta != null;
            meta.setDisplayName("§aɴᴇxᴛ »");
            nextPage.setItemMeta(meta);
            gui.setItem(size - 5, nextPage);
        }

        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta meta = prevPage.getItemMeta();
            assert meta != null;
            meta.setDisplayName("« §cᴘʀᴇᴠɪᴏᴜs");
            prevPage.setItemMeta(meta);
            gui.setItem(size - 9, prevPage);
        }

        playerPages.put(player.getUniqueId(), page);
        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("§eᴡᴀɴᴛᴇᴅs")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            UUID uuid = player.getUniqueId();
            int page = playerPages.getOrDefault(uuid, 0);

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ARROW) {
                String name = Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName();
                if (name.contains("ɴᴇxᴛ")) {
                    openWantedMenu(player, page + 1);
                } else if (name.contains("ᴘʀᴇᴠɪᴏᴜs")) {
                    openWantedMenu(player, page - 1);
                }
            }
        }
    }
}
