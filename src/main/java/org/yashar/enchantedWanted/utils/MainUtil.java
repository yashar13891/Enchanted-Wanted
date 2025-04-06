package org.yashar.enchantedWanted.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.yashar.enchantedWanted.EnchantedWanted;
import org.yashar.enchantedWanted.Permission;
import org.yashar.enchantedWanted.commands.WantedCommand;
import org.yashar.enchantedWanted.commands.WantedsCommand;
import org.yashar.enchantedWanted.listeners.DeathListener;
import org.yashar.enchantedWanted.managers.PlaceHolderManager;
import org.yashar.enchantedWanted.menus.WantedGUI;
import org.yashar.enchantedWanted.storages.DatabaseManager;
import org.yashar.enchantedWanted.storages.MySQLManager;
import org.yashar.enchantedWanted.storages.SQLiteManager;

import java.util.logging.Logger;


public class MainUtil {
    PluginCheckUtil pluginCheckUtil;
    EnchantedWanted enchantedWanted;
    DatabaseManager database;
    Logger logger;
    public MainUtil(DatabaseManager databaseManager) {
        this.enchantedWanted = new EnchantedWanted();
        this.logger = Bukkit.getLogger();
        this.enchantedWanted = new EnchantedWanted();
        this.database = databaseManager;
    }

    public void setupDatabase() {
        String databaseType = enchantedWanted.getConfig().getString("database.type", "sqlite").toLowerCase();

        switch (databaseType) {
            case "mysql":
                database = new MySQLManager();
                break;
            case "sqlite":
            default:
                database = new SQLiteManager();
                break;
        }

        database.connect();
        if (database.isConnected()) {
            database.createTable();
            logger.info("[Database] Database connected and ready!");
        } else {
            logger.severe("[Database] Failed to connect to database!");
            enchantedWanted.getServer().getPluginManager().disablePlugin(enchantedWanted);
        }
    }

    public void registerCommands() {
        registerCommand("wanted",new WantedCommand(database), String.valueOf(Permission.PLAYER));
        registerCommand("wanteds", new WantedsCommand(database), String.valueOf(Permission.PLAYER));
    }

    public void checkDependency() {
        pluginCheckUtil.checkPlugin("PlaceholderAPI", logger);
    }

    public void registerListeners() {
        enchantedWanted.getServer().getPluginManager().registerEvents(new WantedGUI(database), enchantedWanted);
        enchantedWanted.getServer().getPluginManager().registerEvents(new DeathListener(database), enchantedWanted);
        if (isPlaceHolderAPIEnabled()) {
            new PlaceHolderManager(database).register();
        }
    }

    public void registerCommand(String name, CommandExecutor executor, String permission) {
        logger = Bukkit.getLogger();
        PluginCommand command = enchantedWanted.getCommand(name);
        if (command == null) {
            logger.warning("Command '" + name + "' not found in plugin.yml!");
            return;
        }

        command.setExecutor(executor);
        if (permission != null && !permission.isEmpty()) {
            command.setPermission(permission);
        }
    }
    public boolean isPlaceHolderAPIEnabled() {
        return enchantedWanted.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
}
