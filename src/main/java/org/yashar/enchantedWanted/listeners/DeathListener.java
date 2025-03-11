package org.yashar.enchantedWanted.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.yashar.enchantedWanted.EnchantedWanted;
import org.yashar.enchantedWanted.storages.DatabaseManager;

import java.util.List;
import java.util.UUID;

import static org.yashar.enchantedWanted.utils.MessageUtils.sendMessage;

public class DeathListener implements Listener {

    private final DatabaseManager database;

    public DeathListener(DatabaseManager database) {
        this.database = database;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Player killer = victim.getKiller();

        if (isHoldingTotem(victim) || killer == null || killer.equals(victim)) {
            return;
        }

        int wanted = database.getWanted(killer.getUniqueId());
        database.addWanted(killer.getUniqueId(), 1);
        executeWantedAddCommand(killer.getUniqueId());
        if (wanted != database.getWanted(killer.getUniqueId())) {
            sendMessage(killer, "<#ff5733>Hey! You've been added to the wanted list. Current Wanted: %Wanted%</#ff5733>"
                    .replace("%Wanted%", String.valueOf(database.getWanted(killer.getUniqueId()))));
            sendMessage(victim, "<#ff5733>You were killed by a wanted player!</#ff5733>");

            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.hasPermission("enchantedwanted.police.alerts"))
                    .forEach(player -> sendMessage(player, "&8[&1PoliceRadio&8] &fAll police officers, player " + player.getName() + " has received one wanted level")
                    );
        } else {
            sendMessage(killer, "<#ff5733>You didn't receive anything you wanted.");
        }

        executeKillCommands(killer, victim);
    }

    private boolean isHoldingTotem(Player player) {
        ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);
        return player.getInventory().getItemInOffHand().equals(totem) ||
                player.getInventory().getItemInMainHand().equals(totem);
    }

    private void executeKillCommands(Player killer, Player victem) {
        List<String> commands = EnchantedWanted.getInstance().getConfig().getStringList("kill-commands");
        for (String command : commands) {
            if (command.startsWith("[KILLER]")) {
                String playerCommand = command.replace("[KILLER] ", "");
                killer.performCommand(playerCommand);

            } else if (command.startsWith("[VICTIM]")) {
                String victimCommand = command.replace("[VICTIM]", "");
                victem.performCommand(victimCommand);
            } else if (command.startsWith("[CONSOLE]")) {
                String consoleCommand = command.replace("[CONSOLE] ", "").replace("%player%", killer.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand);
            }
        }
    }

    private void executeWantedAddCommand(UUID receiver) {
        var player = Bukkit.getPlayer(receiver);
        if (player == null) return;
        List<String> commands = EnchantedWanted.getInstance().getConfig().getStringList("wanted-add-commands");
        for (String command : commands) {
            if (command.startsWith("[RECEIVER]")) {
                String receiverCommand = command.replace("[RECEIVER] ", "");
                player.performCommand(receiverCommand);
            } else if (command.startsWith("[CONSOLE]")) {
                String consoleCommand = command.replace("[CONSOLE] ", "");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand);
            }
        }
    }
}
