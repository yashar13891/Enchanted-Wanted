package org.yashar.enchantedWanted.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
            sendMessage(sender, Component.text("This command can only be executed by players!", NamedTextColor.RED));
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
        // Implementation for top wanted players
        WantedGUI.openWantedMenu(player);
    }

    private void handleClear(Player player, String[] args) {
        if (checkPermission(player, "enchantedwanted.clear")) return;

        if (validateArgs(player, args, 2, "Usage: /wanted clear <player>")) return;

        Player target = getTargetPlayer(player, args[1]);
        if (target == null) return;

        database.setWanted(target.getUniqueId(), 0);
        sendMessage(player, (Component) Component.text()
                .content("Cleared wanted points for ")
                .append(Component.text(target.getName(), NamedTextColor.GREEN))
                .color(NamedTextColor.GOLD));
    }

    private void handleSet(Player player, String[] args) {
        if (checkPermission(player, "enchantedwanted.set")) return;

        if (validateArgs(player, args, 3, "Usage: /wanted set <player> <value>")) return;

        try {
            int value = Integer.parseInt(args[2]);
            Player target = getTargetPlayer(player, args[1]);
            if (target == null) return;

            database.setWanted(target.getUniqueId(), value);
            sendMessage(player, (Component) Component.text()
                    .content("Set ")
                    .append(Component.text(target.getName(), NamedTextColor.GREEN))
                    .append(Component.text("'s wanted points to " + value))
                    .color(NamedTextColor.GOLD));
        } catch (NumberFormatException e) {
            sendMessage(player, Component.text("Invalid number format!", NamedTextColor.RED));
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
            sendMessage(player, (Component) Component.text()
                    .content("Added ")
                    .append(Component.text(value + " ", NamedTextColor.GREEN))
                    .append(Component.text("wanted points to "))
                    .append(Component.text(target.getName()))
                    .color(NamedTextColor.GOLD));
        } catch (NumberFormatException e) {
            sendMessage(player, Component.text("Invalid number format!", NamedTextColor.RED));
        }
    }
    private void handleArrest(Player player) {
        Utils.arrestPlayer(player.getUniqueId());
    }

    private void handleFind(Player player, String[] args) {
        if (checkPermission(player, "enchantedwanted.find")) return;

        if (validateArgs(player, args, 2, "Usage: /wanted find <player>")) return;

        Player target = getTargetPlayer(player, args[1]);
        if (target == null) return;

        int wantedPoints = database.getWanted(target.getUniqueId());
        sendMessage(player, (Component) Component.text()
                .content(target.getName() + "'s wanted points: ")
                .append(Component.text(wantedPoints, NamedTextColor.GREEN))
                .color(NamedTextColor.GOLD));
    }
    private void handleGPS(Player player) {
        Utils.startGPS(player.getUniqueId());
    }

    private void sendHelpMessage(Player player) {
        Component help = Component.text()
                .append(Component.text("■ ", NamedTextColor.DARK_GRAY))
                .append(Component.text("Wanted Commands: \n", NamedTextColor.GOLD))
                .append(Component.text("/wanted top - Show top wanted players\n", NamedTextColor.YELLOW))
                .append(Component.text("/wanted clear <player> - Clear wanted points\n", NamedTextColor.YELLOW))
                .append(Component.text("/wanted set <player> <value> - Set wanted points\n", NamedTextColor.YELLOW))
                .append(Component.text("/wanted add <player> <value> - Add wanted points\n", NamedTextColor.YELLOW))
                .append(Component.text("/wanted find <player> - Find player's wanted status", NamedTextColor.YELLOW))
                .build();
        sendMessage(player, help);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            Collections.addAll(suggestions, "top", "clear", "set", "add", "find");
        }
        else if (args.length == 2) {
            if (List.of("clear", "set", "add", "find").contains(args[0].toLowerCase())) {
                suggestions.addAll(Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .toList());
            }
        }
        else if (args.length == 3) {
            if (List.of("set", "add").contains(args[0].toLowerCase())) {
                suggestions.add("<value>");
            }
        }

        return filterSuggestions(args, suggestions);
    }

    // Helper Methods
    private boolean checkPermission(Player player, String permission) {
        if (!player.hasPermission(permission)) {
            sendMessage(player, Component.text("You don't have permission!", NamedTextColor.RED));
            return true;
        }
        return false;
    }

    private boolean validateArgs(Player player, String[] args, int required, String usage) {
        if (args.length < required) {
            sendMessage(player, Component.text(usage, NamedTextColor.RED));
            return true;
        }
        return false;
    }

    private Player getTargetPlayer(Player player, String name) {
        Player target = Bukkit.getPlayerExact(name);
        if (target == null) {
            sendMessage(player, Component.text("Player not found!", NamedTextColor.RED));
        }
        return target;
    }

    private void sendMessage(CommandSender sender, Component component) {
        if (sender instanceof Player) {
            sender.sendMessage(String.valueOf(component));
        } else {
            sender.sendMessage(String.valueOf(Component.text()
                    .append(Component.text("[EW] ", NamedTextColor.DARK_RED))
                    .append(component)
                    .build()));
        }
    }

    private List<String> filterSuggestions(String[] args, List<String> suggestions) {
        String current = args.length > 0 ? args[args.length-1].toLowerCase() : "";
        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(current))
                .collect(Collectors.toList());
    }
}