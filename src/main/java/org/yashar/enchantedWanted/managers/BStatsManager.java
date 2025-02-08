package org.yashar.enchantedWanted.managers;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.plugin.java.JavaPlugin;



public class BStatsManager {
    public static void setup(JavaPlugin plugin) {
        int pluginId = 24710;
        Metrics metrics = new Metrics(plugin, pluginId);

        metrics.addCustomChart(new SingleLineChart("active_players", () -> plugin.getServer().getOnlinePlayers().size()));

        metrics.addCustomChart(new SimplePie("spigot_version", () -> plugin.getServer().getVersion()));
    }

}
