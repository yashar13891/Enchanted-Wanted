package org.yashar.enchantedWanted.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class PluginCheckUtil {
    String prefix = "&8[&eEW&8]";

    public boolean isPluginInstalled(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    public void checkPlugin(String pluginName, Logger logger) {
        if (!isPluginInstalled(pluginName)) {
            logger.warning(prefix + pluginName + "is Not Installed!");
            logger.warning(prefix + "Will Be Some Options Not Work...");
        } else {
            logger.info(prefix + pluginName + "Is Installed! Check Completed.");
        }
    }
}
