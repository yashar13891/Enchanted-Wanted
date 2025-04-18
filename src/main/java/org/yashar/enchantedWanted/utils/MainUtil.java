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


}
