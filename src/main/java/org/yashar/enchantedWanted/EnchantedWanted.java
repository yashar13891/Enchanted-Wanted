package org.yashar.enchantedWanted;

import org.bukkit.plugin.java.JavaPlugin;
import org.yashar.enchantedWanted.API.WantedPlayer;
import org.yashar.enchantedWanted.managers.*;
import org.yashar.enchantedWanted.storages.*;
import org.yashar.enchantedWanted.utils.ConfigUtil;
import org.yashar.enchantedWanted.utils.MainUtil;
import org.yashar.enchantedWanted.utils.PluginCheckUtil;

import java.util.logging.Logger;

public final class EnchantedWanted extends JavaPlugin {

    private static EnchantedWanted plugin;
    private static Logger logger;
    public static DatabaseManager database;
    private WantedPlayer wantedPlayer;



    @Override
    public void onEnable() {
        PluginCheckUtil pluginCheckUtil = new PluginCheckUtil();
        plugin = this;
        logger = getLogger();
        MainUtil mainUtil = new MainUtil(database);
        this.wantedPlayer = new WantedPlayer(database);
        saveDefaultConfig();
        BStatsManager.setup();
        pluginCheckUtil.checkPlugin("PlaceholderAPI", getLogger());
        mainUtil.setupDatabase();
        mainUtil.checkDependency();
        mainUtil.registerListeners();
        mainUtil.registerCommands();

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
}
