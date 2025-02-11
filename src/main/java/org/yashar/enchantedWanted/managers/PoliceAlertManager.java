package org.yashar.enchantedWanted.managers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PoliceAlertManager {

    private final Set<UUID> policeAlertEnabledPlayers = new HashSet<>();

    public boolean togglePoliceAlert(UUID playerId) {
        if (policeAlertEnabledPlayers.contains(playerId)) {
            policeAlertEnabledPlayers.remove(playerId);
            return false;
        } else {
            policeAlertEnabledPlayers.add(playerId);
            return true;
        }
    }

    public boolean isPoliceAlertEnabled(UUID playerId) {
        return policeAlertEnabledPlayers.contains(playerId);
    }
}