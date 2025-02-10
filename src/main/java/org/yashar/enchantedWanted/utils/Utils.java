package org.yashar.enchantedWanted.utils;

import me.ford.cuffem.CuffEmPlugin;
import me.ford.cuffem.Dragger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.yashar.enchantedWanted.storages.DatabaseManager;

import java.util.UUID;


public class Utils {
    private static DatabaseManager database;

    public Utils(DatabaseManager database) {
        Utils.database = database;
    }

    public static void startGPS(UUID uuidplayer, UUID uuidwanted) {
    }

    public static void unCuff(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        CuffEmPlugin cuffem = Bukkit.getServicesManager().getRegistration(CuffEmPlugin.class).getProvider();
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
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"/jail " + player.getName() + " " + database.getWanted(player.getUniqueId()) * 5);
            database.setWanted(player.getUniqueId(),0);

        } else {
            System.out.println("Player is Null");
        }
    }
}