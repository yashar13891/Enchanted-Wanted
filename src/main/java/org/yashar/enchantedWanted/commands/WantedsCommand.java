package org.yashar.enchantedWanted.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yashar.enchantedWanted.storages.DatabaseManager;
import org.yashar.enchantedWanted.utils.MessageUtils;

public class WantedsCommand implements CommandExecutor {
    private final DatabaseManager database;

    public WantedsCommand(DatabaseManager database) {
        this.database = database;
    }

    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command cmd,@NotNull String label, String[] args) {
        if (args.length == 0) {
            Player player = (Player) sender;
            if (player.hasPermission("enchantedwanted.command.wanteds")) {
                int wanted = database.getWanted(player.getUniqueId());
                if (wanted > 0) {
                    MessageUtils.sendMessage(player,"&bYour wanted is " + database.getWanted(player.getUniqueId()));
                } else {
                    MessageUtils.sendMessage(player,"&cYou do not have enough wanted!");
                }
            }
        }
        return true;
    }
}
