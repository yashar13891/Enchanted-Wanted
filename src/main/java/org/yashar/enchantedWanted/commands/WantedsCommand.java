package org.yashar.enchantedWanted.commands;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yashar.enchantedWanted.storages.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class WantedsCommand implements TabExecutor {
    private final DatabaseManager database;

    public WantedsCommand(DatabaseManager database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                sendhelpmessage(player);
            } else {
                switch (args[0]) {
                        case "top":
                            break;
                        case "clear":
                            if (args.length == 1) {

                            }
                            break;
                    case "set":
                        if (args.length == 1) {
                            player.getUniqueId();
                        }
                        break;
                        default:
                            sender.sendMessage("&cUnknown subcommand. Use /lunamcskyblock help for a list of commands.");

                    }
                }
            }
        }
        return true;
    }
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> arguments = new ArrayList<>();



        return null;
    }
    private void sendhelpmessage(Player player) {
        player.spigot().sendMessage((BaseComponent) Component.text("Usage: /wanted [top,clear,set,add,find] [player]"));
    }
}
