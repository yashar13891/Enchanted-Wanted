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

        Location loc = BukkitAdapter.adapt(player.getLocation());
        System.out.println("[DEBUG] Checking wanted status for player: " + player.getName() + " at location: " + loc);

        RegionContainer container = getContainer();
        if (container == null) {
            System.out.println("[DEBUG] RegionContainer is null!");
            return false;
        }

        ApplicableRegionSet regions = container.createQuery().getApplicableRegions(loc);
        if (regions == null) {
            System.out.println("[DEBUG] No regions found for location: " + loc);
            return false;
        }

        boolean foundAllow = true;

        for (ProtectedRegion region : regions) {
            StateFlag.State flagValue = region.getFlag(WGWantedFlag.WANTED_ALLOWED);
            System.out.println("[DEBUG] Region: " + region.getId() + ", WantedAllowed Flag: " + flagValue);

            if (flagValue == StateFlag.State.ALLOW) {
                foundAllow = true;
            } else if (flagValue == StateFlag.State.DENY) {
                System.out.println("[DEBUG] Wanted is explicitly denied in region: " + region.getId());
                return false;
            }
        }

        System.out.println("[DEBUG] Wanted status result for " + player.getName() + ": " + foundAllow);
        return foundAllow;
    }

}
