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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Utils {
    private static DatabaseManager database;

    public Utils(DatabaseManager database) {
        Utils.database = database;
    }

    private static final Map<UUID, GPSTrackerTask> gpsTrackingTasks = new HashMap<>();


    public static void startGPS(UUID policeUUID, UUID wantedUUID) {
        Player police = Bukkit.getPlayer(policeUUID);
        if (police == null) {
            return;
        }
        if (gpsTrackingTasks.containsKey(policeUUID)) {
            gpsTrackingTasks.get(policeUUID).cancel();
            gpsTrackingTasks.remove(policeUUID);
        }
        Player wanted = Bukkit.getPlayer(wantedUUID);
        if (wanted == null) {
            police.sendMessage("Target player is not online.");
            return;
        }
        GPSTrackerTask task = new GPSTrackerTask(police, wanted);
        task.runTaskTimer(EnchantedWanted.getInstance(), 0L, 10L);
        gpsTrackingTasks.put(policeUUID, task);
    }
    public static void stopGPS(UUID policeUUID) {
        GPSTrackerTask task = gpsTrackingTasks.remove(policeUUID);
        if (task != null) {
            task.cancel();
            task.removeBossBar();
            Player police = Bukkit.getPlayer(policeUUID);
            if (police != null) {
                police.sendMessage("GPS tracking stopped.");
            }
        }
    }

    public static void unCuff(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        CuffEmPlugin cuffem = Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(CuffEmPlugin.class)).getProvider();
        if (cuffem.getDragger().isBeingDragged(player)) {
            try {
                cuffem.getDragger().stopDragging(player);
            } catch (Dragger.NotBeingDraggedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void arrestPlayer(UUID uuid) {
        if (uuid == null) {
            System.out.println("UUID is Null");
            return;
        }
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            unCuff(player.getUniqueId());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/jail " + player.getName() + " " + database.getWanted(player.getUniqueId()) * 5);
            database.setWanted(player.getUniqueId(), 0);
        } else {
            System.out.println("Player is Null");
        }
    }
    
    private static class GPSTrackerTask extends BukkitRunnable {
        private final Player police;
        private Player target;
        private final BossBar bossBar;

        public GPSTrackerTask(Player police, Player target) {
            this.police = police;
            this.target = target;
            bossBar = Bukkit.createBossBar("GPS Tracking", BarColor.BLUE, BarStyle.SOLID);
            bossBar.addPlayer(police);
        }

        @Override
        public void run() {
            if (!police.isOnline() || target == null || !target.isOnline()) {
                removeBossBar();
                cancel();
                gpsTrackingTasks.remove(police.getUniqueId());
                if (police.isOnline()) {
                    police.sendMessage("GPS tracking cancelled because target went offline.");
                }
                return;
            }
            target = Bukkit.getPlayer(target.getUniqueId());
            if (target == null) {
                removeBossBar();
                cancel();
                gpsTrackingTasks.remove(police.getUniqueId());
                police.sendMessage("GPS tracking cancelled because target went offline.");
                return;
            }
            Location policeLoc = police.getLocation();
            Location targetLoc = target.getLocation();
            Vector toTarget = targetLoc.toVector().subtract(policeLoc.toVector());
            double angleToTarget = Math.toDegrees(Math.atan2(toTarget.getZ(), toTarget.getX()));
            float policeYaw = policeLoc.getYaw();
            policeYaw = (policeYaw % 360 + 360) % 360;
            double relativeAngle = angleToTarget - policeYaw;
            relativeAngle = (relativeAngle + 360) % 360;
            String arrow = getArrowFromAngle(relativeAngle);
            bossBar.setTitle("جهت هدف: " + arrow);
        }

        private String getArrowFromAngle(double angle) {
            if (angle >= 337.5 || angle < 22.5) {
                return "➤";
            } else if (angle < 67.5) {
                return "↗";
            } else if (angle < 112.5) {
                return "⮝";
            } else if (angle < 157.5) {
                return "↖";
            } else if (angle < 202.5) {
                return "⮜";
            } else if (angle < 247.5) {
                return "↙";
            } else if (angle < 292.5) {
                return "⮟";
            } else if (angle < 337.5) {
                return "↘";
            }
            return "➤";
        }

        public void removeBossBar() {
            bossBar.removeAll();
        }
    }
}
