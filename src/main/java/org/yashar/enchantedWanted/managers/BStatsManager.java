package org.yashar.enchantedWanted.managers;


import org.yashar.enchantedWanted.managers.Metrics.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;


public class BStatsManager {
    public static void setup(JavaPlugin plugin) {
        int pluginId = 24710;
        Metrics metrics = new Metrics(plugin, pluginId);

        metrics.addCustomChart(new SingleLineChart("active_players", () -> plugin.getServer().getOnlinePlayers().size()));

        metrics.addCustomChart(new SimplePie("spigot_version", () -> plugin.getServer().getVersion()));

        metrics.addCustomChart(new SimplePie("plugin_version", () -> plugin.getDescription().getVersion()));

        metrics.addCustomChart(new SimplePie("server_version", () -> plugin.getServer().getVersion()));

        metrics.addCustomChart(new AdvancedPie("server_size", () -> {
            Map<String, Integer> map = new HashMap<>();
            int playerCount = plugin.getServer().getOnlinePlayers().size();

            if (playerCount == 0) map.put("Empty", 1);
            else if (playerCount < 10) map.put("1-9 Players", 1);
            else if (playerCount < 50) map.put("10-49 Players", 1);
            else map.put("50+ Players", 1);

            return map;
        }));
        metrics.addCustomChart(new MultiLineChart("player_trends", () -> {
            Map<String, Integer> data = new HashMap<>();
            data.put("Online Players", plugin.getServer().getOnlinePlayers().size());
            return data;
        }));

    }

}
