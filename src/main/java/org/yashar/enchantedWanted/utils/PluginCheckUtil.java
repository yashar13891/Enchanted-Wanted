package org.yashar.enchantedWanted.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class PluginCheckUtil {

    public static boolean isPluginInstalled(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    public static void checkPlugin(String pluginName, Logger logger) {
        if (!isPluginInstalled(pluginName)) {
            logger.warning("[WARNING] " + pluginName + "is Not Installed!");
            logger.warning("[WARNING] Install for smooth using...");
        } else {
            logger.info("[CHECKER] " + pluginName + "Is Installed! Have Fun.");
        }
    }
}
