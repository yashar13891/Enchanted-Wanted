package org.yashar.enchantedWanted.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.entity.Player;
import org.yashar.enchantedWanted.flags.WGWantedFlag;

public class WorldGuardUtils {

    public static RegionContainer getContainer() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    public static boolean isWantedAllowed(Player player) {
        if (player == null) {
            System.out.println("[DEBUG] Player is null!");
            return false;
        }

        Location loc;
        try {
            loc = BukkitAdapter.adapt(player.getLocation());
        } catch (Exception e) {
            System.out.println("[DEBUG] Error adapting location: " + e.getMessage());
            return false;
        }

        System.out.println("[DEBUG] Checking wanted status for player: " + player.getName() + " at location: " + loc);

        RegionContainer container = getContainer();
        if (container == null) {
            System.out.println("[DEBUG] RegionContainer is null!");
            return false;
        }

        ApplicableRegionSet regions = container.createQuery().getApplicableRegions(loc);
        if (regions == null || regions.size() == 0) {
            System.out.println("[DEBUG] No regions found for location: " + loc);
            return false;
        }


        for (ProtectedRegion region : regions) {
            StateFlag.State flagValue = region.getFlag(WGWantedFlag.WANTED_ALLOWED);
            System.out.println("[DEBUG] Region: " + region.getId() + ", WantedAllowed Flag: " + flagValue);

            if (flagValue == null) {
                System.out.println("[DEBUG] No explicit flag set for region: " + region.getId());
                continue;
            }

            if (flagValue == StateFlag.State.ALLOW) {
            } else if (flagValue == StateFlag.State.DENY) {
                System.out.println("[DEBUG] Wanted is explicitly denied in region: " + region.getId());
                return true;
            }
        }

        System.out.println("[DEBUG] Wanted status result for " + player.getName() + ": ");
        return true;
    }
}
