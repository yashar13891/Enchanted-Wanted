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
        if (player == null) return false;

        Location loc = BukkitAdapter.adapt(player.getLocation());
        if (loc == null) {
            System.out.println("[ERROR] Player location is NULL!");
            return false;
        }

        RegionContainer container = getContainer();
        if (container == null) {
            System.out.println("[ERROR] RegionContainer is NULL!");
            return false;
        }

        ApplicableRegionSet regions = container.createQuery().getApplicableRegions(loc);

        boolean foundAllow = false;

        for (ProtectedRegion region : regions) {
            StateFlag.State flagValue = region.getFlag(WGWantedFlag.WANTED_ALLOWED);
            System.out.println("[DEBUG] Region: " + region.getId() + ", WantedAllowed: " + flagValue);

            if (flagValue == StateFlag.State.ALLOW) {
                foundAllow = true;
            } else if (flagValue == StateFlag.State.DENY) {
                return false;
            }
        }

        return foundAllow;
    }

}
