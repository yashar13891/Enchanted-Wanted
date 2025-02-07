package org.yashar.enchantedWanted.utils;

import com.live.bemmamin.gps.api.GPSAPI;
import me.ford.cuffem.CuffEmPlugin;
import me.ford.cuffem.Dragger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.yashar.enchantedWanted.EnchantedWanted;
import org.yashar.enchantedWanted.storages.DatabaseManager;

import java.util.Objects;
import java.util.UUID;

public class Utils {
    private static DatabaseManager database;

    public Utils(DatabaseManager database) {
        Utils.database = database;
    }

    public static void startGPS(UUID uuid) {
        GPSAPI gpsapi = new GPSAPI(EnchantedWanted.getInstance());
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            gpsapi.addPoint("wantedplayer", player.getLocation());
            gpsapi.startGPS(player, "wantedplayer");
        }
    }

    public static void unCuff(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
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
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            unCuff(player.getUniqueId());
            database.setWanted(player.getUniqueId(),0);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"/jail " + player.getName() + " " + database.getWanted(player.getUniqueId()) * 5);

        }
    }
}

