package org.yashar.enchantedWanted.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class PluginCheckUtil {

    public boolean isPluginInstalled(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    public void checkPlugin(String pluginName, Logger logger) {
        if (!isPluginInstalled(pluginName)) {
            logger.warning(pluginName + "is Not Installed!");
            logger.warning("Install for smooth using...");
        } else {
            logger.info(pluginName + "Is Installed! Have Fun.");
        }
    }
}
