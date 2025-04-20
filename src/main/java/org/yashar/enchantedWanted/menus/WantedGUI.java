package org.yashar.enchantedWanted.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yashar.enchantedWanted.storages.DatabaseManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WantedGUI implements Listener {
    private final DatabaseManager database;
    private static final int ITEMS_PER_PAGE = 36;
    private static final ItemStack GLASS_PANE = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
    private static final ItemStack NEXT_PAGE = createNavigationItem("§aɴᴇxᴛ »");
    private static final ItemStack PREV_PAGE = createNavigationItem("« §cᴘʀᴇᴠɪᴏᴜs");
    private final Map<UUID, Integer> playerPages = new ConcurrentHashMap<>();

    public WantedGUI(DatabaseManager database) {
        this.database = database;
    }

    public void openWantedMenu(Player player, int page) {
        List<Player> wantedPlayers = getWantedPlayers();
        int totalPages = Math.max(1, (int) Math.ceil((double) wantedPlayers.size() / ITEMS_PER_PAGE));
        page = Math.max(0, Math.min(page, totalPages - 1));

        Inventory gui = Bukkit.createInventory(null, 54, "§eᴡᴀɴᴛᴇᴅs - Page " + (page + 1));
        setupBorder(gui);

        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, wantedPlayers.size());

        for (int i = startIndex; i < endIndex; i++) {
            Player target = wantedPlayers.get(i);
            Optional.of(createPlayerHead(target)).ifPresent(gui::addItem);
        }

        if (page < totalPages - 1) gui.setItem(53, NEXT_PAGE);
        if (page > 0) gui.setItem(45, PREV_PAGE);

        playerPages.put(player.getUniqueId(), page);
        player.openInventory(gui);
    }

    private List<Player> getWantedPlayers() {
        List<Player> wanted = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (database.getWanted(player.getUniqueId()) > 0) wanted.add(player);
        });
        wanted.sort(Comparator.comparingInt(p -> -database.getWanted(p.getUniqueId())));
        return wanted;
    }

    private ItemStack createPlayerHead(Player target) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

        ItemMeta meta = skull.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c " + target.getName());
            meta.setLore(Arrays.asList(
                    "§bᴡᴀɴᴛᴇᴅ ᴄᴏᴜɴᴛ§7: §4" + database.getWanted(target.getUniqueId()),
                    "§7Click for more info"
            ));
            skull.setItemMeta(meta);
        }
        return skull;
    }

    private static void setupBorder(Inventory gui) {
        Arrays.stream(new int[]{0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53})
                .forEach(slot -> gui.setItem(slot, GLASS_PANE));
    }

    private static ItemStack createNavigationItem(String name) {
        return createItem(Material.ARROW, name);
    }

    private static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith("§eᴡᴀɴᴛᴇᴅs")) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();
        int currentPage = playerPages.getOrDefault(player.getUniqueId(), 0);

        if (clicked.isSimilar(NEXT_PAGE)) {
            openWantedMenu(player, currentPage + 1);
        } else if (clicked.isSimilar(PREV_PAGE)) {
            openWantedMenu(player, currentPage - 1);
        } else if (clicked.getType() == Material.PLAYER_HEAD) {
            handlePlayerHeadClick(clicked, player);
        }
    }

    private void handlePlayerHeadClick(ItemStack skull, Player clicker) {
        ItemMeta meta = skull.getItemMeta();
        if (meta == null) return;

        String displayName = meta.getDisplayName();
        if (displayName.startsWith("§c ")) {
            String targetName = displayName.substring(3);
            Player target = Bukkit.getPlayer(targetName);

            if (target != null) {
                clicker.performCommand("wanted gps " + targetName);
            }
        }
    }
}