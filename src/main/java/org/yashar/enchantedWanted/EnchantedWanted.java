package org.yashar.enchantedWanted;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class EnchantedWanted extends JavaPlugin {
    public static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        this.registerEvents();
        this.registerCommands();

    }

    @Override
    public void onDisable() {}
    List<PluginCommand> commands = new ArrayList<>();
    public void registerCommand(String name, CommandExecutor executor, Permission permission) {
        PluginCommand command = getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
            command.setPermission(permission.toString());
            commands.add(command);
        }
    }
    public static JavaPlugin getInstance() {
        return (JavaPlugin)instance;
    }
    public void registerCommands() {

    }
    public void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();
    }
}
