package org.yashar.enchantedWanted.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yashar.enchantedWanted.EnchantedWanted;
import org.yashar.enchantedWanted.managers.PoliceAlertManager;
import org.yashar.enchantedWanted.menus.WantedGUI;
import org.yashar.enchantedWanted.storages.DatabaseManager;
import org.yashar.enchantedWanted.utils.MessageUtils;
import org.yashar.enchantedWanted.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class WantedCommand implements TabExecutor {
    private final DatabaseManager database;
    private final PoliceAlertManager policeAlertManager;
    private final Utils utils;
    String prefix = "&8[&eEW&8]";
    private static final Map<String, String> COMMAND_PERMISSIONS = new HashMap<>();
    private static final String POLICE_ALERT_PERMISSION = "enchantedwanted.police.alerts";
    private static final String HELP_MESSAGE = String.join("\n",
            "<#ff0000>!!! This Is Beta Version if you found a bug Ticket on Discord !!!",
                            "https://discord.gg/dJ8exMjuKe",
                            "Enchanted Wanted Plugin Command Info",
            "<#555555>▼ Wanted Commands ▼",
            "<#ffd100>/wanted top <#ff9b00>- Show top wanted players",
            "<#ffd100>/wanted clear <player> <#ff9b00>- Clear wanted points",
            "<#ffd100>/wanted set <player> <value> <#ff9b00>- Set wanted points",
            "<#ffd100>/wanted add <player> <value> <#ff9b00>- Add wanted points",
            "<#ffd100>/wanted find <player> <#ff9b00>- Check wanted status",
            "<#ffd100>/wanted gps <#ff9b00>- Track nearest wanted player",
            "<#ffd100>/wanted gpsstop <#ff9b00>- Stop GPS tracking",
            "<#ffd100>/wanted arrest <#ff9b00>- Arrest a wanted player",
            "<#ffd100>/wanted remove <player> <value> <#ff9b00>- Remove wanted points",
            "<#ffd100>/wanted reload <#ff9b00>- Reload plugin configuration",
            "<#ffd100>/wanted policealert <#ff9b00>- Toggle police alerts"
    );

    static {
        COMMAND_PERMISSIONS.put("top", "enchantedwanted.top");
        COMMAND_PERMISSIONS.put("clear", "enchantedwanted.clear");
        COMMAND_PERMISSIONS.put("set", "enchantedwanted.set");
        COMMAND_PERMISSIONS.put("add", "enchantedwanted.add");
        COMMAND_PERMISSIONS.put("find", "enchantedwanted.find");
        COMMAND_PERMISSIONS.put("gps", "enchantedwanted.gps");
        COMMAND_PERMISSIONS.put("gpsstop", "enchantedwanted.gps");
        COMMAND_PERMISSIONS.put("remove", "enchantedwanted.remove");
        COMMAND_PERMISSIONS.put("reload", "enchantedwanted.command.reload");
        COMMAND_PERMISSIONS.put("policealert", "enchantedwanted.policealert");
    }

    public WantedCommand(DatabaseManager database) {
        this.database = database;
        this.policeAlertManager = new PoliceAlertManager();
        this.utils = new Utils(database,EnchantedWanted.getInstance());
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

        String subCmd = args[0].toLowerCase();
        if (!hasPermission(player, subCmd)) {
            MessageUtils.sendMessage(player, prefix + "&cYou don't have permission!");
            return true;
        }

        switch (subCmd) {
            case "top" -> handleTop(player);
            case "clear" -> handleClear(player, args);
            case "set" -> handleSet(player, args);
            case "add" -> handleAdd(player, args);
            case "find" -> handleFind(player, args);
            case "gps" -> handleGPS(player,args);
            case "gpsstop" -> handleStopGPS(player);
            case "reload" -> handleAdminReload(player);
            case "remove" -> handleRemove(player, args);
            case "policealert" -> handlePoliceAlert(player);
            default -> sendHelpMessage(player);
        }
        return true;
    }

    private boolean hasPermission(Player player, String subCommand) {
        return player.hasPermission(COMMAND_PERMISSIONS.getOrDefault(subCommand, ""));
    }

    private void handleTop(Player player) {
        WantedGUI wantedGUI = new WantedGUI(database);
        wantedGUI.openWantedMenu(player, 0);
    }
    private void handleRemove(Player player, String[] args) {
        if (validateArgs(player, args, 3, prefix + "&rUsage: /wanted remove <player> <amount>")) return;
        Player target = findPlayer(args[1]);
        if (target == null) {
            MessageUtils.sendMessage(player, prefix + "&cPlayer not found!");
            return;
        }
        try {
            int amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                MessageUtils.sendMessage(player, prefix + "&cAmount must be a positive number!");
            } else {

            }
            database.removeWanted(target.getUniqueId(),amount);
        } catch (NumberFormatException e) {
            MessageUtils.sendMessage(player, prefix + "&cInvalid number!");
        }
    }

    private void handleClear(Player player, String[] args) {
        if (validateArgs(player, args, 2, prefix + "&rUsage: /wanted clear <player>")) return;

        Player target = findPlayer(args[1]);
        if (target == null) {
            MessageUtils.sendMessage(player, prefix + "&cPlayer not found!");
            return;
        }

        int currentPoints = database.getWanted(target.getUniqueId());
        if (currentPoints == 0) {
            MessageUtils.sendMessage(player, prefix + "<#ff9b00>" + target.getName() + " <#ffd100>doesn't have any wanted points!");
            return;
        }

        database.setWanted(target.getUniqueId(), 0);
        MessageUtils.sendMessage(player, prefix + "<#ffd100>Cleared <#ff9b00>" + target.getName() + "'s <#ffd100>wanted points!");
        sendPoliceAlert(String.format(prefix + "<#ffd100>Wanted player <#ff9b00>%s <#ffd100>has been cleared by <#ff9b00>%s",
                target.getName(), player.getName()));
    }

    private void handleSet(Player player, String[] args) {
        if (validateArgs(player, args, 3, prefix + "&rUsage: /wanted set <player> <value>")) return;
        int value = Integer.parseInt(args[2]);
        try {
            if (value < 0) {
                MessageUtils.sendMessage(player, prefix + "<#e01400>Value cannot be negative!");
                return;
            }

            Player target = findPlayer(args[1]);
            if (target == null) {
                MessageUtils.sendMessage(player, prefix + "&cPlayer not found!");
                return;
            }

            database.setWanted(target.getUniqueId(), value);
            MessageUtils.sendMessage(player, prefix + "<#ffd100>Set <#ff9b00>" + target.getName() + "'s <#ffd100>wanted points to " + value);
            sendPoliceAlert(String.format(prefix + "<#ffd100>Wanted player <#ff9b00>%s <#ffd100>has been set to <#ff9b00>%d <#ffd100>by <#ff9b00>%s",
                    target.getName(), value, player.getName()));
        } catch (NumberFormatException e) {
            MessageUtils.sendMessage(player, prefix + "<#e01400>Invalid number format!");
        }
    }

    private void handleAdd(Player player, String[] args) {
        if (validateArgs(player, args, 3, prefix + "&rUsage: /wanted add <player> <value>")) return;
        try {
            int value = Integer.parseInt(args[2]);
            if (value < 0) {
                MessageUtils.sendMessage(player, prefix + "<#e01400>Value cannot be negative!");
                return;
            }

            Player target = findPlayer(args[1]);
            if (target == null) {
                MessageUtils.sendMessage(player, prefix + "&cPlayer not found!");
                return;
            }

            database.addWanted(target.getUniqueId(), value);
            MessageUtils.sendMessage(player, prefix + "<#ffd100>Added <#ff9b00>" + value + " <#ffd100>wanted points to " + target.getName());
            sendPoliceAlert(String.format(prefix + "<#ffd100>Wanted player <#ff9b00>%s <#ffd100>has increased by <#ff9b00>%d <#ffd100>wanted points by <#ff9b00>%s",
                    target.getName(), value, player.getName()));
        } catch (NumberFormatException e) {
            MessageUtils.sendMessage(player, prefix + "<#e01400>Invalid number format!");
        }
    }

    private void handleFind(Player player, String[] args) {
        if (validateArgs(player, args, 2, prefix + "&rUsage: /wanted find <player>")) return;

        Player target = findPlayer(args[1]);
        if (target == null) {
            MessageUtils.sendMessage(player, prefix + "&cPlayer not found!");
            return;
        }

        int wantedPoints = database.getWanted(target.getUniqueId());
        MessageUtils.sendMessage(player, prefix + "<#ff9b00>" + target.getName() + " <#ffd100>has " + wantedPoints + " wanted points");
    }

    private void handleGPS(Player player, String[] args) {
        if (validateArgs(player, args, 2, prefix + "&rUsage: /wanted gps <player>")) return;
        Player target = findPlayer(args[1]);
        if (target == null) {
            MessageUtils.sendMessage(player, prefix + "&cPlayer not found!");
            return;
        } else if (database.getWanted(target.getUniqueId()) == 0) {
            MessageUtils.sendMessage(player, prefix + "&cNo Wanted found for this player!");
            return;
        }


        utils.startGPS(player.getUniqueId(), target.getUniqueId());
        MessageUtils.sendMessage(player, prefix + "<#ffd100>Tracking: " + target.getName());
    }

    private void handleStopGPS(Player player) {
        utils.stopGPS(player.getUniqueId());
        MessageUtils.sendMessage(player, prefix + "<#ffd100>GPS tracking stopped.");
    }

    private void handleArrest(Player player) {
        MessageUtils.sendMessage(player, prefix + "<#ffd100>Arrest process started!");
    }

    private void handleAdminReload(Player player) {
        EnchantedWanted.getInstance().saveConfig();
        EnchantedWanted.getInstance().reloadConfig();
        MessageUtils.sendMessage(player, prefix + "<#ffd100>Plugin reloaded successfully!");
    }

    private void handlePoliceAlert(Player player) {
        boolean newState = policeAlertManager.togglePoliceAlert(player.getUniqueId());
        String state = newState ? prefix + "<#00ff00>ENABLED" : "<#ff0000>DISABLED";
        MessageUtils.sendMessage(player, prefix + "<#ffd100>Police alerts: " + state);
    }

    private Player findPlayer(String name) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private boolean validateArgs(Player player, String[] args, int required, String usage) {
        if (args.length < required) {
            MessageUtils.sendMessage(player, prefix + "<#e01400>" + usage);
            return true;
        }
        return false;
    }

    private void sendPoliceAlert(String message) {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission(POLICE_ALERT_PERMISSION))
                .filter(p -> policeAlertManager.isAlertsEnabled(p.getUniqueId()))
                .forEach(p -> MessageUtils.sendMessage(p, prefix + "<#8a2be2>[PoliceRadio] <#ffd700>" + message));
    }

    private void sendHelpMessage(Player player) {
        MessageUtils.sendMessage(player, HELP_MESSAGE);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            COMMAND_PERMISSIONS.keySet().stream()
                    .filter(cmd -> sender.hasPermission(COMMAND_PERMISSIONS.get(cmd)))
                    .forEach(suggestions::add);
        } else if (args.length == 2) {
            String subCmd = args[0].toLowerCase();
            if (List.of("clear", "set", "add", "find","gps").contains(subCmd)) {
                suggestions.addAll(Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .toList());
            }
        } else if (args.length == 3) {
            String subCmd = args[0].toLowerCase();
            if (List.of("set", "add").contains(subCmd)) {
                suggestions.addAll(List.of("1", "2", "3","4","5","6"));
            }
        }

        return filterSuggestions(suggestions, args[args.length - 1]);
    }

    private List<String> filterSuggestions(List<String> suggestions, String input) {
        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(input.toLowerCase()))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }
}