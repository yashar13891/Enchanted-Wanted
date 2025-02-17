package org.yashar.enchantedWanted.managers;

import java.util.*;

public class PoliceAlertManager {
    private final Set<UUID> enabledAlerts = new HashSet<>();

    public boolean togglePoliceAlert(UUID playerId) {
        if (enabledAlerts.contains(playerId)) {
            enabledAlerts.remove(playerId);
            return false;
        } else {
            enabledAlerts.add(playerId);
            return true;
        }
    }

    public boolean isAlertsEnabled(UUID playerId) {
        return enabledAlerts.contains(playerId);
    }
}