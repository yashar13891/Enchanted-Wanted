package org.yashar.enchantedWanted.flags;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import java.util.logging.Logger;

public class WGWantedFlag {
    public static final StateFlag WANTED_ALLOWED = new StateFlag("wanted-allowed", true);

    public static void registerFlags(Logger logger) {
        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            registry.register(WANTED_ALLOWED);
        } catch (Exception e) {
            logger.warning("[WorldGuard] Error registering flag: " + e.getMessage());
        }
    }
}
