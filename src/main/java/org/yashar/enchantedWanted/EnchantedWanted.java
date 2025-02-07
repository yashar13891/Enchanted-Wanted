package org.yashar.enchantedWanted;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.yashar.enchantedWanted.managers.ConfigManager;

import static org.yashar.enchantedWanted.managers.ConfigManager.setupConfig;

import org.yashar.enchantedWanted.storages.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class EnchantedWanted extends JavaPlugin {
    public static Plugin instance;
    private static Logger logger;
    private DatabaseManager database;


    @Override
    public void onEnable() {
        instance = this;

        setupConfig();

        this.registerEvents();
        this.registerCommands();

        logger = getLogger();
        logger.info("Enchanted Wanted Enabled! Thanks For Using (:");

        String databaseType = ConfigManager.getConfig().getString("database.type", "sqlite");
        if (databaseType.equalsIgnoreCase("mysql")) {
            database = new MySQLManager();
        }
        if (databaseType.equalsIgnoreCase("sqlite")) {
            database = new SQLiteManager();
        }
        database.connect();
        if (database.isConnected()) {
            database.createTable();
            getLogger().info("[Database] Database connected and ready!");
        } else {
            getLogger().severe("[Database] Failed to connect to database!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        saveConfig();
        database.disconnect();
        getLogger().severe("[Database] DataBase Disconnected!");
    }

    public static Logger getPluginLogger() {
        return logger;
    }

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
        return (JavaPlugin) instance;
    }

    public void registerCommands() {

    }

    public void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();
    }
}
