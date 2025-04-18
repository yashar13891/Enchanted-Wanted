package org.yashar.enchantedWanted.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.yashar.enchantedWanted.EnchantedWanted;
import org.yashar.enchantedWanted.storages.DatabaseManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Utils {
    private static final long GPS_UPDATE_INTERVAL = 20L; // 1 second
    private static final double MAX_TRACKING_DISTANCE = 10000.0;

    private final DatabaseManager database;
    private final EnchantedWanted plugin;
    private final Map<UUID, GPSTrackerTask> gpsTrackingTasks = new ConcurrentHashMap<>();

    public Utils(DatabaseManager database, EnchantedWanted plugin) {
        this.database = database;
        this.plugin = plugin;
    }

    public void startGPS(UUID policeUUID, UUID wantedUUID) {
        Player police = Bukkit.getPlayer(policeUUID);
        if (police == null || !police.isOnline()) return;

        stopGPS(policeUUID);

        Player wanted = Bukkit.getPlayer(wantedUUID);
        if (wanted == null || !wanted.isOnline()) {
            police.sendMessage("§cTarget is offline.");
            return;
        }

        GPSTrackerTask newTask = new GPSTrackerTask(police, wanted);
        newTask.runTaskTimer(plugin, 0L, GPS_UPDATE_INTERVAL);
        gpsTrackingTasks.put(policeUUID, newTask);
    }

    public void stopGPS(UUID policeUUID) {
        GPSTrackerTask task = gpsTrackingTasks.remove(policeUUID);
        if (task != null) {
            task.cancel();
        }
    }

    private class GPSTrackerTask extends BukkitRunnable {
        private final Player police;
        private Player target;
        private final BossBar bossBar;
        private final Vector bufferVector = new Vector();

        GPSTrackerTask(Player police, Player target) {
            this.police = police;
            this.target = target;
            this.bossBar = Bukkit.createBossBar("§6§lTracking Target", BarColor.BLUE, BarStyle.SOLID);
            this.bossBar.addPlayer(police);
        }

        @Override
        public void run() {
            if (!validatePlayers()) return;
            updateBossBar();
        }

        private boolean validatePlayers() {
            if (!police.isOnline() || !target.isOnline()) {
                cleanup("§cTarget went offline.");
                return false;
            }

            target = Bukkit.getPlayer(target.getUniqueId());
            return target != null && target.isValid();
        }

        private void updateBossBar() {
            Location policeLoc = police.getLocation();
            Location targetLoc = target.getLocation();

            if (policeLoc.getWorld() != targetLoc.getWorld()) {
                cleanup("§cTarget in another world.");
                return;
            }

            Vector toTarget = targetLoc.toVector().subtract(policeLoc.toVector());
            double distance = Math.min(toTarget.length(), MAX_TRACKING_DISTANCE);

            bossBar.setTitle(String.format("§aDistance: §f§l%.1f blocks", distance));
            bossBar.setProgress(distance / MAX_TRACKING_DISTANCE);
        }

        private void cleanup(String message) {
            bossBar.removeAll();
            cancel();
            if (police.isOnline()) police.sendMessage(message);
        }

        @Override
        public synchronized void cancel() {
            super.cancel();
            bossBar.removeAll();
        }
    }
}