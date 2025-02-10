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
import org.yashar.enchantedWanted.managers.ConfigManager;
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
        commandPermissions.put("arrest", "enchantedwanted.arrest");
        commandPermissions.put("reload", "enchantedwanted.command.reload");
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
            case "reload" ->  handleAdminReload();
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
        if (database.getWanted(target.getUniqueId()) == 0) {
            MessageUtils.sendMessage(player,"<#ff9b00><player><#ffd100> dossen't have wanted!".replace("<player>", player.getName()));
        } else if (database.getWanted(target.getUniqueId()) > 0) {
            database.setWanted(target.getUniqueId(), 0);
            MessageUtils.sendMessage(player, "<#ffd100>Cleared <#ff9b00><player>'s<#ffd100> wanted points!".replace("<player>", player.getName()));
        }
    }
    private void handleAdminReload() {
        ConfigManager configManager = ConfigManager.getInstance(EnchantedWanted.getInstance());
        configManager.saveConfig();
        configManager.reloadConfig();

    }

    private void handleSet(Player player, String[] args) {
        if (checkPermission(player, "enchantedwanted.set")) return;
        if (validateArgs(player, args, 3, "Usage: /wanted set <player> <value>")) return;
        try {
            int value = Integer.parseInt(args[2]);
            Player target = getTargetPlayer(player, args[1]);
            if (target == null) return;
            database.setWanted(target.getUniqueId(), value);
            MessageUtils.sendMessage(player, "<#ffd100>Set <#ff9b00><player>'s <#ffd100>wanted points to <value>".replace("<player>", player.getName()).replace("<value>", String.valueOf(value)));
        } catch (NumberFormatException e) {
            MessageUtils.sendMessage(player,"<#e01400>Invalid number format!");
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
            MessageUtils.sendMessage(player, "<#ffd100>Added <#ff9b00><value> <#ffd100>wanted points to <player>"
                    .replace("<player>", player.getName())
                    .replace("<value>", String.valueOf(value)));
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
        MessageUtils.sendMessage(player,"<#ff9b00><player> <#ffd100>has <wanted> wanted points"
                .replace("<player>",player.getName())
                .replace("<wanted>", String.valueOf(wantedPoints)));
    }

    private void handleGPS(Player player) {
        if (checkPermission(player, "enchantedwanted.gps")) return;
        Utils.startGPS(player.getUniqueId());
    }

    private static final String HELP_MESSAGE = String.join(System.lineSeparator(),
            "<#555555>▼ Wanted Commands ▼",
            "<#ffd100>/wanted top <#ff9b00>- Show top wanted players",
            "<#ffd100>/wanted clear <player> <#ff9b00>- Clear wanted points",
            "<#ffd100>/wanted set <player> <value> <#ff9b00>- Set wanted points",
            "<#ffd100>/wanted add <player> <value> <#ff9b00>- Add wanted points",
            "<#ffd100>/wanted find <player> <#ff9b00>- Check wanted status",
            "<#ffd100>/wanted gps <#ff9b00>- Track nearest wanted",
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
            MessageUtils.sendMessage(player,"<#e01400>" + usage);
            return true;
        }
        return false;
    }

    private Player getTargetPlayer(Player player, String name) {
        Player target = Bukkit.getPlayerExact(name);
        if (target == null) {
            MessageUtils.sendMessage(player,"&cPlayer not found!");
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