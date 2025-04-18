package org.yashar.enchantedWanted;


import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.yashar.enchantedWanted.API.WantedPlayer;
import org.yashar.enchantedWanted.commands.WantedCommand;
import org.yashar.enchantedWanted.commands.WantedsCommand;
import org.yashar.enchantedWanted.listeners.DeathListener;
import org.yashar.enchantedWanted.managers.BStatsManager;
import org.yashar.enchantedWanted.managers.PlaceHolderManager;
import org.yashar.enchantedWanted.menus.WantedGUI;
import org.yashar.enchantedWanted.storages.DatabaseManager;
import org.yashar.enchantedWanted.storages.MySQLManager;
import org.yashar.enchantedWanted.storages.SQLiteManager;
import org.yashar.enchantedWanted.utils.ConfigUtil;
import org.yashar.enchantedWanted.utils.PluginCheckUtil;

import java.util.logging.Logger;

public final class EnchantedWanted extends JavaPlugin {

    private static EnchantedWanted plugin;
    private static Logger logger;
    public static DatabaseManager database;
    private WantedPlayer wantedPlayer;


    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();
        this.wantedPlayer = new WantedPlayer(database);
        getConfig().options().copyDefaults(true);
        saveConfig();
        saveDefaultConfig();
        BStatsManager.setup();

        setupDatabase();
        checkDependency();
        registerListeners();
        registerCommands();
        logger.info("Enchanted Wanted Enabled! Thanks For Using (:");
    }
    @Override
    public void onDisable() {
        database.saveCacheToDatabase();
        database.disconnect();
    }

    public WantedPlayer getWantedPlayerAPI() {
        return wantedPlayer;
    }
    public static EnchantedWanted getInstance() {
        return plugin;
    }

    public static Logger getPluginLogger() {
        return logger;
    }
    public void setupDatabase() {
        String databaseType = getConfig().getString("database.type", "sqlite").toLowerCase();

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

    public void registerCommands() {
        getCommand("wanted").setExecutor(new WantedCommand(database));
        getCommand("wanteds").setExecutor(new WantedsCommand(database));
    }

    public void checkDependency() {
        PluginCheckUtil pluginCheckUtil = new PluginCheckUtil();
        pluginCheckUtil.checkPlugin("PlaceholderAPI", logger);
    }

    public void registerListeners() {
        getServer().getPluginManager().registerEvents(new WantedGUI(database), this);
        getServer().getPluginManager().registerEvents(new DeathListener(database), this);
        if (isPlaceHolderAPIEnabled()) {
            new PlaceHolderManager(database).register();
        }
    }

    public boolean isPlaceHolderAPIEnabled() {
        return getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
}
