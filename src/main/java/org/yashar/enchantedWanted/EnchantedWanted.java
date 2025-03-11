package org.yashar.enchantedWanted;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import org.yashar.enchantedWanted.API.WantedPlayer;
import org.yashar.enchantedWanted.commands.*;
import org.yashar.enchantedWanted.listeners.*;
import org.yashar.enchantedWanted.managers.*;
import org.yashar.enchantedWanted.menus.*;
import org.yashar.enchantedWanted.storages.*;

import java.util.logging.Logger;

import static org.yashar.enchantedWanted.utils.PluginCheckUtil.checkPlugin;

public final class EnchantedWanted extends JavaPlugin {

    private static EnchantedWanted plugin;
    private static Logger logger;
    private static DatabaseManager database;
    private WantedPlayer wantedPlayer;

    public static EnchantedWanted getInstance() {
        return plugin;
    }

    public static Logger getPluginLogger() {
        return logger;
    }

    @Override
    public void onEnable() {
        this.wantedPlayer = new WantedPlayer(database);
        saveDefaultConfig();
        plugin = this;
        logger = getLogger();
        BStatsManager.setup(this);

        setupDatabase();
        checkDependencies();
        registerListeners();
        registerCommands();
        logger.info("Enchanted Wanted Enabled! Thanks For Using (:");
    }
    @Override
    public void onDisable() {
        database.saveCacheToDatabase();
        database.disconnect();
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

    private void registerCommands() {
        registerCommand("wanted",new WantedCommand(database), String.valueOf(Permission.PLAYER));
        registerCommand("wanteds", new WantedsCommand(database), String.valueOf(Permission.PLAYER));
    }

    private void checkDependencies() {
        checkPlugin("PlaceholderAPI", logger);
        checkPlugin("GPS", logger);
        checkPlugin("CuffEm", logger);
        checkPlugin("BetterJails", logger);
    }

    private void registerListeners() {
        WantedGUI wantedGUI = new WantedGUI(database);
        Bukkit.getPluginManager().registerEvents(new DeathListener(database), this);
        Bukkit.getPluginManager().registerEvents(wantedGUI, this);
        new PlaceHolderManager(database, this).register();
    }

    private void registerCommand(String name, CommandExecutor executor, String permission) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            getLogger().warning("Command '" + name + "' not found in plugin.yml!");
            return;
        }

        command.setExecutor(executor);
        if (permission != null && !permission.isEmpty()) {
            command.setPermission(permission);
        }
    }
    public WantedPlayer getWantedPlayerAPI() {
        return wantedPlayer;
    }

}
