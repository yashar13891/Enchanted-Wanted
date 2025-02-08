package org.yashar.enchantedWanted;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import org.yashar.enchantedWanted.commands.*;
import org.yashar.enchantedWanted.listeners.*;
import org.yashar.enchantedWanted.managers.*;
import org.yashar.enchantedWanted.menus.*;
import org.yashar.enchantedWanted.storages.*;

import java.util.logging.Logger;

import static org.bukkit.Bukkit.getPluginManager;
import static org.yashar.enchantedWanted.utils.PluginCheckUtil.checkPlugin;

public final class EnchantedWanted extends JavaPlugin {

    private static EnchantedWanted plugin;
    private static Logger logger;
    private static DatabaseManager database;

    public static EnchantedWanted getInstance() {
        return plugin;
    }

    public static Logger getPluginLogger() {
        return logger;
    }

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();

        ConfigManager.init(this);
        ConfigManager.loadConfig();

        setupDatabase();
        registerCommands();
        checkDependencies();
        registerListeners();

        logger.info("Enchanted Wanted Enabled! Thanks For Using (:");
    }

    @Override
    public void onDisable() {
        if (database != null) {
            database.disconnect();
            getLogger().severe("[Database] Database Disconnected!");
        }
        saveConfig();
        database.saveCacheToDatabase();
    }


    private void setupDatabase() {
        String databaseType = ConfigManager.getConfig().getString("database.type", "sqlite").toLowerCase();

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
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void registerCommands() {
        registerCommand(new WantedsCommand(database));
    }

    private void checkDependencies() {
        checkPlugin("PlaceholderAPI", logger);
        checkPlugin("GPS", logger);
        checkPlugin("CuffEm", logger);
    }

    private void registerListeners() {
        WantedGUI wantedGUI = new WantedGUI(database);
        getPluginManager().registerEvents(new DeathListener(database), this);
        getPluginManager().registerEvents(wantedGUI, this);
        new PlaceHolderManager(database).register();
    }

    private void registerCommand(CommandExecutor executor) {
        PluginCommand command = getCommand("wanteds");
        if (command != null) {
            command.setExecutor(executor);
            command.setPermission(Permission.ADMIN.toString());
        } else {
            logger.warning("[Command] Command '" + "wanteds" + "' not found in plugin.yml!");
        }
    }
}
