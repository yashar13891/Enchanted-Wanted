package org.yashar.enchantedWanted.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yashar.enchantedWanted.menus.WantedGUI;
import org.yashar.enchantedWanted.storages.DatabaseManager;
import org.yashar.enchantedWanted.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WantedsCommand implements TabExecutor {
    private final DatabaseManager database;

    public WantedsCommand(DatabaseManager database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players!");
            return true;
        }
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "top" -> handleTop(player);
            case "clear" -> handleClear(player, args);
            case "set" -> handleSet(player, args);
            case "add" -> handleAdd(player, args);
            case "find" -> handleFind(player, args);
            case "gps" -> handleGPS(player);
            case "arrest" -> handleArrest(player);
            default -> sendHelpMessage(player);
        }
        return true;
    }

    private void handleTop(Player player) {
        if (checkPermission(player, "enchantedwanted.top")) return;
        WantedGUI.openWantedMenu(player, 0);
    }

    private void handleClear(Player player, String[] args) {
        if (checkPermission(player, "enchantedwanted.clear")) return;
        if (validateArgs(player, args, 2, "Usage: /wanted clear <player>")) return;
        Player target = getTargetPlayer(player, args[1]);
        if (target == null) return;
        database.setWanted(target.getUniqueId(), 0);
        player.sendMessage(ChatColor.YELLOW + "Cleared " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + "'s wanted points!");
    }

    private void handleSet(Player player, String[] args) {
        if (checkPermission(player, "enchantedwanted.set")) return;
        if (validateArgs(player, args, 3, "Usage: /wanted set <player> <value>")) return;
        try {
            int value = Integer.parseInt(args[2]);
            Player target = getTargetPlayer(player, args[1]);
            if (target == null) return;
            database.setWanted(target.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + "'s wanted points to " + ChatColor.RED + value);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid number format!");
        }
    }

    private void handleAdd(Player player, String[] args) {
        if (checkPermission(player, "enchantedwanted.add")) return;
        if (validateArgs(player, args, 3, "Usage: /wanted add <player> <value>")) return;
        try {
            int value = Integer.parseInt(args[2]);
            Player target = getTargetPlayer(player, args[1]);
            if (target == null) return;
            database.addWanted(target.getUniqueId(), value);
            player.sendMessage(ChatColor.YELLOW + "Added " + ChatColor.RED + value + ChatColor.YELLOW + " wanted points to " + ChatColor.GOLD + target.getName());
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid number format!");
        }
    }

    private void handleArrest(Player player) {
        if (checkPermission(player, "enchantedwanted.arrest")) return;
        Utils.arrestPlayer(player.getUniqueId());
    }

    private void handleFind(Player player, String[] args) {
        if (checkPermission(player, "enchantedwanted.find")) return;
        if (validateArgs(player, args, 2, "Usage: /wanted find <player>")) return;
        Player target = getTargetPlayer(player, args[1]);
        if (target == null) return;
        int wantedPoints = database.getWanted(target.getUniqueId());
        player.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.YELLOW + " has " + ChatColor.RED + wantedPoints + ChatColor.YELLOW + " wanted points");
    }

    private void handleGPS(Player player) {
        if (checkPermission(player, "enchantedwanted.gps")) return;
        Utils.startGPS(player.getUniqueId());
    }

    private void sendHelpMessage(Player player) {
        String help = ChatColor.DARK_GRAY + "▼ Wanted Commands ▼\n" +
                ChatColor.GOLD + "/wanted top " + ChatColor.YELLOW + "- Show top wanted players\n" +
                ChatColor.GOLD + "/wanted clear <player> " + ChatColor.YELLOW + "- Clear wanted points\n" +
                ChatColor.GOLD + "/wanted set <player> <value> " + ChatColor.YELLOW + "- Set wanted points\n" +
                ChatColor.GOLD + "/wanted add <player> <value> " + ChatColor.YELLOW + "- Add wanted points\n" +
                ChatColor.GOLD + "/wanted find <player> " + ChatColor.YELLOW + "- Check wanted status\n" +
                ChatColor.GOLD + "/wanted gps " + ChatColor.YELLOW + "- Track nearest wanted\n" +
                ChatColor.GOLD + "/wanted arrest " + ChatColor.YELLOW + "- Arrest a wanted player";
        player.sendMessage(help);
    }

    private boolean checkPermission(Player player, String permission) {
        if (!player.hasPermission(permission)) {
            player.sendMessage(ChatColor.RED + "You don't have permission!");
            return true;
        }
        return false;
    }

    private boolean validateArgs(Player player, String[] args, int required, String usage) {
        if (args.length < required) {
            player.sendMessage(ChatColor.RED + usage);
            return true;
        }
        return false;
    }

    private Player getTargetPlayer(Player player, String name) {
        Player target = Bukkit.getPlayerExact(name);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
        }
        return target;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            Collections.addAll(suggestions, "top", "clear", "set", "add", "find", "gps", "arrest");
        } else if (args.length == 2) {
            if (List.of("clear", "set", "add", "find").contains(args[0].toLowerCase())) {
                suggestions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            }
        } else if (args.length == 3) {
            if (List.of("set", "add").contains(args[0].toLowerCase())) {
                suggestions.add("<value>");
            } else if (List.of("clear", "gps", "find", "arrest").contains(args[0].toLowerCase())) {
                suggestions.add("<player>");
            }
        }
        return filterSuggestions(args, suggestions);
    }

    private List<String> filterSuggestions(String[] args, List<String> suggestions) {
        String current = args.length > 0 ? args[args.length - 1].toLowerCase() : "";
        return suggestions.stream().filter(s -> s.toLowerCase().startsWith(current)).collect(Collectors.toList());
    }
}
