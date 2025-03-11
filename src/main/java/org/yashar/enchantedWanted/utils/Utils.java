package org.yashar.enchantedWanted.utils;

import me.ford.cuffem.CuffEmPlugin;
import me.ford.cuffem.Dragger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.yashar.enchantedWanted.EnchantedWanted;
import org.yashar.enchantedWanted.storages.DatabaseManager;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Utils {
    private final DatabaseManager database;
    private final EnchantedWanted plugin;
    private final Map<UUID, GPSTrackerTask> gpsTrackingTasks = new ConcurrentHashMap<>();

    public Utils(DatabaseManager database, EnchantedWanted plugin) {
        this.database = database;
        this.plugin = plugin;
    }

    public void startGPS(UUID policeUUID, UUID wantedUUID) {
        Player police = Bukkit.getPlayer(policeUUID);
        if (police == null) return;

        GPSTrackerTask existingTask = gpsTrackingTasks.get(policeUUID);
        if (existingTask != null) {
            existingTask.cancel();
            gpsTrackingTasks.remove(policeUUID);
        }

        Player wanted = Bukkit.getPlayer(wantedUUID);
        if (wanted == null) {
            police.sendMessage("§cهدف آفلاین است.");
            return;
        }

        GPSTrackerTask newTask = new GPSTrackerTask(police, wanted);
        newTask.runTaskTimer(plugin, 0L, 10L);
        gpsTrackingTasks.put(policeUUID, newTask);
    }

    public void stopGPS(UUID policeUUID) {
        GPSTrackerTask task = gpsTrackingTasks.remove(policeUUID);
        if (task != null) {
            task.cancel();
            task.removeBossBar();
            Player police = Bukkit.getPlayer(policeUUID);
            if (police != null) police.sendMessage("§cجیپیاس متوقف شد.");
        }
    }

    public void unCuff(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        CuffEmPlugin cuffem = Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(CuffEmPlugin.class)).getProvider();
        try {
            cuffem.getDragger().stopDragging(player);
        } catch (Dragger.NotBeingDraggedException ignored) {
        }
    }

    public void arrestPlayer(UUID uuid) {
        if (uuid == null) {
            plugin.getLogger().warning("UUID is null in arrestPlayer");
            return;
        }

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            plugin.getLogger().warning("Player not found for UUID: " + uuid);
            return;
        }

        unCuff(uuid);
        int wantedLevel = database.getWanted(uuid);
        if (wantedLevel < 0) wantedLevel = 0;

        String command = String.format("jail %s %d", player.getName(), wantedLevel * 5);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        database.setWanted(uuid, 0);
    }

    private class GPSTrackerTask extends BukkitRunnable {
        private final Player police;
        private Player target;
        private final BossBar bossBar;

        GPSTrackerTask(Player police, Player target) {
            this.police = police;
            this.target = target;
            this.bossBar = Bukkit.createBossBar("§6§lردیابی هدف", BarColor.BLUE, BarStyle.SOLID);
            this.bossBar.addPlayer(police);
        }

        @Override
        public void run() {
            if (!police.isOnline() || !target.isOnline()) {
                cleanup("§cهدف آفلاین شد.");
                return;
            }

            target = Bukkit.getPlayer(target.getUniqueId());
            if (target == null || !target.isValid()) {
                cleanup("§cهدف غیرمجاز شد.");
                return;
            }

            updateBossBar();
        }

        private void updateBossBar() {
            Location policeLoc = police.getLocation();
            Location targetLoc = target.getLocation();

            if (policeLoc.getWorld() != targetLoc.getWorld()) {
                cleanup("§cهدف در دنیای دیگری است.");
                return;
            }

            Vector toTarget = targetLoc.toVector().subtract(policeLoc.toVector());
            double distance = toTarget.length();
            double angleToTarget = Math.toDegrees(Math.atan2(-toTarget.getZ(), toTarget.getX())); // Fix for Minecraft's coordinate system
            angleToTarget = (angleToTarget + 360) % 360;

            float policeYaw = policeLoc.getYaw();
            double adjustedYaw = (policeYaw + 360) % 360;
            double relativeAngle = (angleToTarget - (adjustedYaw + 90) + 360) % 360; // Adjusted for Minecraft's yaw

            String arrow = getArrowFromAngle(relativeAngle);
            bossBar.setTitle(String.format("§a%s §f§l%.1f بلوک", arrow, distance));
            bossBar.setProgress(Math.min(1.0, distance / 100.0));
        }

        private String getArrowFromAngle(double angle) {
            angle = (angle + 360) % 360;
            if (angle < 22.5) return "⬆";
            if (angle < 67.5) return "↗";
            if (angle < 112.5) return "➡";
            if (angle < 157.5) return "↘";
            if (angle < 202.5) return "⬇";
            if (angle < 247.5) return "↙";
            if (angle < 292.5) return "⬅";
            if (angle < 337.5) return "↖";
            return "⬆";
        }

        private void cleanup(String message) {
            removeBossBar();
            cancel();
            gpsTrackingTasks.remove(police.getUniqueId());
            if (police.isOnline()) police.sendMessage(message);
        }

        void removeBossBar() {
            bossBar.removeAll();
        }
    }
}