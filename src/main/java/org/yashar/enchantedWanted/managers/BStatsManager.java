package org.yashar.enchantedWanted.managers;


import org.bukkit.Bukkit;
import org.yashar.enchantedWanted.EnchantedWanted;
import org.yashar.enchantedWanted.managers.Metrics.*;

import java.util.HashMap;
import java.util.Map;


public class BStatsManager {
    public static void setup() {
        EnchantedWanted plugin = EnchantedWanted.getInstance();
        int pluginId = 24710;
        Metrics metrics = new Metrics(EnchantedWanted.getInstance(), pluginId);

        metrics.addCustomChart(new SingleLineChart("active_players", () -> Bukkit.getOnlinePlayers().size()));

        metrics.addCustomChart(new SimplePie("spigot_version", Bukkit::getVersion));

        metrics.addCustomChart(new SimplePie("plugin_version", () -> plugin.getDescription().getVersion()));

        metrics.addCustomChart(new SimplePie("server_version", Bukkit::getVersion));

        metrics.addCustomChart(new AdvancedPie("server_size", () -> {
            Map<String, Integer> map = new HashMap<>();
            int playerCount = Bukkit.getOnlinePlayers().size();

            if (playerCount == 0) map.put("Empty", 1);
            else if (playerCount < 10) map.put("1-9 Players", 1);
            else if (playerCount < 50) map.put("10-49 Players", 1);
            else map.put("50+ Players", 1);

            return map;
        }));
        metrics.addCustomChart(new MultiLineChart("player_trends", () -> {
            Map<String, Integer> data = new HashMap<>();
            data.put("Online Players", Bukkit.getOnlinePlayers().size());
            return data;
        }));

    }

}
