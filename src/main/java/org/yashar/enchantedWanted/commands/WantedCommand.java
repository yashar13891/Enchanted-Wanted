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
    private final Map<String, String> commandPermissions = new HashMap<>();

    public WantedCommand(DatabaseManager database) {
        this.database = database;
        initializeCommandPermissions();
    }

    private void initializeCommandPermissions() {
        commandPermissions.put("top", "enchantedwanted.top");
        commandPermissions.put("clear", "enchantedwanted.clear");
        commandPermissions.put("set", "enchantedwanted.set");
        commandPermissions.put("add", "enchantedwanted.add");
        commandPermissions.put("find", "enchantedwanted.find");
        commandPermissions.put("gps", "enchantedwanted.gps");
        commandPermissions.put("gpsstop", "enchantedwanted.gps");
        commandPermissions.put("arrest", "enchantedwanted.arrest");
        commandPermissions.put("reload", "enchantedwanted.command.reload");
        commandPermissions.put("policealert", "enchantedwanted.policealert");
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
            case "clear" -> handleClear(player, player, args);
            case "set" -> handleSet(player, player, args);
            case "add" -> handleAdd(player, player, args);
            case "find" -> handleFind(player, args);
            case "gps" -> handleGPS(player);
            case "gpsstop" -> handleStopGPS(player);
            case "arrest" -> handleArrest(player);
            case "reload" ->  handleAdminReload();
            case "policealert" -> handlePoliceAlert(player);
            default -> sendHelpMessage(player);
        }
        return true;
    }

    private void handleTop(Player player) {
        if (checkPermission(player, "enchantedwanted.top")) return;
        WantedGUI.openWantedMenu(player, 0);
    }

    private void handleClear(Player player, Player sender, String[] args) {
        if (checkPermission(player, "enchantedwanted.clear")) return;
        if (validateArgs(player, args, 2, "Usage: /wanted clear <player>")) return;
        Player target = getTargetPlayer(player, args[1]);
        if (target == null) return;
        if (database.getWanted(target.getUniqueId()) == 0) {
            MessageUtils.sendMessage(player, "<#ff9b00>" + target.getName() + " <#ffd100>doesn't have any wanted points!");
        } else {
            database.setWanted(target.getUniqueId(), 0);
            MessageUtils.sendMessage(player, "<#ffd100>Cleared <#ff9b00>" + target.getName() + "'s <#ffd100>wanted points!");
            Bukkit.getOnlinePlayers().forEach(playerpolice -> {
                if (playerpolice.hasPermission("enchantedwanted.police.alerts")) {
                    MessageUtils.sendMessage(playerpolice, "&8[&1PoliceRadio&8] &fAll police, wanted player " + target.getName() + " has been cleared by " + sender.getName() + "!");
                }
            });
        }
    }

    private void handleAdminReload() {
        EnchantedWanted.getInstance().saveConfig();
        EnchantedWanted.getInstance().reloadConfig();
        database.saveCacheToDatabase();
        database.disconnect();
        EnchantedWanted.getInstance().setupDatabase();
    }

    private void handleSet(Player player, Player sender, String[] args) {
        if (checkPermission(player, "enchantedwanted.set")) return;
        if (validateArgs(player, args, 3, "Usage: /wanted set <player> <value>")) return;
        try {
            int value = Integer.parseInt(args[2]);
            Player target = getTargetPlayer(player, args[1]);
            if (target == null) return;
            database.setWanted(target.getUniqueId(), value);
            MessageUtils.sendMessage(player, "<#ffd100>Set <#ff9b00>" + target.getName() + "'s <#ffd100>wanted points to " + value);
            Bukkit.getOnlinePlayers().forEach(policeplayer -> {
                if (policeplayer.hasPermission("enchantedwanted.police.alerts")) {
                    MessageUtils.sendMessage(policeplayer, "&8[&1PoliceRadio&8] &fAll police, wanted player " + target.getName() + " has been set to " + value + " by " + sender.getName() + "!");
                }
            });
        } catch (NumberFormatException e) {
            MessageUtils.sendMessage(player, "<#e01400>Invalid number format!");
        }
    }

    private void handleAdd(Player player, Player sender, String[] args) {
        if (checkPermission(player, "enchantedwanted.add")) return;
        if (validateArgs(player, args, 3, "Usage: /wanted add <player> <value>")) return;
        try {
            int value = Integer.parseInt(args[2]);
            Player target = getTargetPlayer(player, args[1]);
            if (target == null) return;
            database.addWanted(target.getUniqueId(), value);
            MessageUtils.sendMessage(player, "<#ffd100>Added <#ff9b00>" + value + " <#ffd100>wanted points to " + target.getName());
            Bukkit.getOnlinePlayers().forEach(playerpolice -> {
                if (playerpolice.hasPermission("enchantedwanted.police.alerts")) {
                    MessageUtils.sendMessage(playerpolice, "&8[&1PoliceRadio&8] &fAll police, wanted player " + target.getName() + " has been increased by " + value + " wanted points by " + sender.getName() + "!");
                }
            });
        } catch (NumberFormatException e) {
            MessageUtils.sendMessage(player, "&cInvalid number format!");
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
        MessageUtils.sendMessage(player, "<#ff9b00>" + target.getName() + " <#ffd100>has " + wantedPoints + " wanted points");
    }

    private void handleGPS(Player player) {
        if (checkPermission(player, "enchantedwanted.gps")) return;
        Optional<Player> nearestWantedOpt = (Optional<Player>) Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.equals(player) && database.getWanted(p.getUniqueId()) > 0)
                .min(Comparator.comparingDouble(p -> p.getLocation().distance(player.getLocation())));
        if (nearestWantedOpt.isEmpty()) {
            MessageUtils.sendMessage(player, "<#e01400>No wanted players found online!");
            return;
        }
        Player target = nearestWantedOpt.get();
        if (database.getWanted(target.getUniqueId()) <= 0) {
            MessageUtils.sendMessage(player, "<#e01400>Target " + target.getName() + " has no wanted points!");
            return;
        }
        Utils.startGPS(player.getUniqueId(), target.getUniqueId());
        MessageUtils.sendMessage(player, "<#ffd100>Started GPS tracking for wanted player " + target.getName());
    }

    private void handleStopGPS(Player player) {
        if (checkPermission(player, "enchantedwanted.gps")) return;
        Utils.stopGPS(player.getUniqueId());
        MessageUtils.sendMessage(player, "<#ffd100>GPS tracking stopped.");
    }

    private void handlePoliceAlert(Player player) {
        final PoliceAlertManager policeAlertManager = new PoliceAlertManager();
        policeAlertManager.togglePoliceAlert(player.getUniqueId());
    }

    private static final String HELP_MESSAGE = String.join(System.lineSeparator(),
            "<#555555>▼ Wanted Commands ▼",
            "<#ffd100>/wanted top <#ff9b00>- Show top wanted players",
            "<#ffd100>/wanted clear <player> <#ff9b00>- Clear wanted points",
            "<#ffd100>/wanted set <player> <value> <#ff9b00>- Set wanted points",
            "<#ffd100>/wanted add <player> <value> <#ff9b00>- Add wanted points",
            "<#ffd100>/wanted find <player> <#ff9b00>- Check wanted status",
            "<#ffd100>/wanted gps <#ff9b00>- Track nearest wanted player",
            "<#ffd100>/wanted gpsstop <#ff9b00>- Stop GPS tracking",
            "<#ffd100>/wanted arrest <#ff9b00>- Arrest a wanted player"
    );

    private void sendHelpMessage(Player player) {
        MessageUtils.sendMessage(player, HELP_MESSAGE);
    }

    private boolean checkPermission(Player player, String permission) {
        if (!player.hasPermission(permission)) {
            MessageUtils.sendMessage(player, "&cYou don't have permission!");
            return true;
        }
        return false;
    }

    private boolean validateArgs(Player player, String[] args, int required, String usage) {
        if (args.length < required) {
            MessageUtils.sendMessage(player, "<#e01400>" + usage);
            return true;
        }
        return false;
    }

    private Player getTargetPlayer(Player player, String name) {
        Player target = Bukkit.getPlayerExact(name);
        if (target == null) {
            MessageUtils.sendMessage(player, "&cPlayer not found!");
        }
        return target;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            commandPermissions.forEach((cmd, perm) -> {
                if (sender.hasPermission(perm)) suggestions.add(cmd);
            });
        } else if (args.length == 2) {
            String subCmd = args[0].toLowerCase();
            List<String> playerArgs = List.of("clear", "set", "add", "find");
            if (playerArgs.contains(subCmd) && sender.hasPermission(commandPermissions.get(subCmd))) {
                suggestions.addAll(Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .toList());
            }
        } else if (args.length == 3) {
            String subCmd = args[0].toLowerCase();
            if (List.of("set", "add").contains(subCmd) && sender.hasPermission(commandPermissions.get(subCmd))) {
                suggestions.add("<value>");
            }
        }

        return filterSuggestions(args, suggestions);
    }

    private List<String> filterSuggestions(String[] args, List<String> suggestions) {
        String currentArg = args[args.length - 1].toLowerCase();
        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(currentArg))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }
}
