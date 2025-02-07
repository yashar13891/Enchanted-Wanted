package org.yashar.enchantedWanted;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import org.yashar.enchantedWanted.commands.WantedsCommand;
import org.yashar.enchantedWanted.listeners.DamageListener;
import org.yashar.enchantedWanted.managers.ConfigManager;
import org.yashar.enchantedWanted.menus.WantedGUI;
import org.yashar.enchantedWanted.storages.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getPluginManager;
import static org.yashar.enchantedWanted.utils.PluginCheckUtil.checkPlugin;

public final class EnchantedWanted extends JavaPlugin {

    private DatabaseManager database;
    private static Logger logger;
    private static EnchantedWanted plugin;

    public static Logger getPluginLogger() {
        return logger;
    }

    public static EnchantedWanted getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {

        plugin = this;
        logger = getLogger();

        WantedGUI wantedGUI = new WantedGUI(database);
        registerCommand("wanteds", new WantedsCommand(database), org.yashar.enchantedWanted.Permission.ADMIN);

        // Checker
        checkPlugin("PlaceholderAPI", logger);
        checkPlugin("GPS", logger);
        checkPlugin("CuffEm", logger);

        //Register Events
        getPluginManager().registerEvents(new DamageListener(database), this);
        getPluginManager().registerEvents(wantedGUI, this);

        //Database SetUp
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

        logger.info("Enchanted Wanted Enabled! Thanks For Using (:");
    }

    @Override
    public void onDisable() {
        database.disconnect();
        getLogger().severe("[Database] DataBase Disconnected!");
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
}