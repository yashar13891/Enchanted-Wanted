package org.yashar.enchantedWanted.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.yashar.enchantedWanted.EnchantedWanted;
import org.yashar.enchantedWanted.managers.PoliceAlertManager;
import org.yashar.enchantedWanted.storages.DatabaseManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.yashar.enchantedWanted.utils.MessageUtils.sendMessage;

public class DeathListener implements Listener {
    String prefix = "&8[&eEW&8]";
    private static final ItemStack TOTEM = new ItemStack(Material.TOTEM_OF_UNDYING);

    private final DatabaseManager database;

    public DeathListener(DatabaseManager database) {
        this.database = database;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Player killer = victim.getKiller();

        if (isHoldingTotem(victim) || killer == null || killer.equals(victim)) return;

        int initialWanted = database.getWanted(killer.getUniqueId());
        database.addWanted(killer.getUniqueId(), 1);
        int newWanted = database.getWanted(killer.getUniqueId());

        if (newWanted > initialWanted) {
            executeWantedAddCommand(killer.getUniqueId());
            sendMessage(killer, prefix + "<#ff5733>Hey! You've been added to the wanted list. Current Wanted: " + newWanted);
            sendMessage(victim, prefix + "<#ff5733>You were killed by a wanted player!");

            String alertMsg = prefix + "&8[&1PoliceRadio&8] &fAll units, " + killer.getName() + " has increased to wanted level " + newWanted;
            PoliceAlertManager policeAlertManager = new PoliceAlertManager();
            Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.hasPermission("enchantedwanted.police.alerts"))
                    .filter(p -> policeAlertManager.isAlertsEnabled(p.getUniqueId()))
                    .forEach(p -> sendMessage(p, alertMsg));
        }

        executeKillCommands(killer, victim);
    }

    private boolean isHoldingTotem(Player player) {
        return player.getInventory().getItemInOffHand().isSimilar(TOTEM) ||
                player.getInventory().getItemInMainHand().isSimilar(TOTEM);
    }

    private void executeKillCommands(Player killer, Player victim) {
        List<String> commands = EnchantedWanted.getInstance().getConfig().getStringList("kill-commands");
        commands.forEach(command -> processCommand(command, killer, victim));
    }

    private void processCommand(String command, Player killer, Player victim) {
        try {
            String processed = command
                    .replace("%killer%", killer.getName())
                    .replace("%victim%", victim.getName());

            if (command.startsWith("[KILLER]")) {
                killer.performCommand(processed.substring(8).trim());
            } else if (command.startsWith("[VICTIM]")) {
                victim.performCommand(processed.substring(8).trim());
            } else if (command.startsWith("[CONSOLE]")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processed.substring(9).trim());
            }
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Error executing command: " + command);
            ex.printStackTrace();
        }
    }

    private void executeWantedAddCommand(UUID receiver) {
        Optional.ofNullable(Bukkit.getPlayer(receiver)).ifPresent(player -> {
            List<String> commands = EnchantedWanted.getInstance().getConfig().getStringList("wanted-add-commands");
            commands.forEach(command -> processWantedCommand(command, player));
        });
    }

    private void processWantedCommand(String command, Player player) {
        try {
            String processed = command.replace("%player%", player.getName());

            if (command.startsWith("[RECEIVER]")) {
                player.performCommand(processed.substring(10).trim());
            } else if (command.startsWith("[CONSOLE]")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processed.substring(9).trim());
            }
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Error executing wanted command: " + command);
            ex.printStackTrace();
        }
    }
}