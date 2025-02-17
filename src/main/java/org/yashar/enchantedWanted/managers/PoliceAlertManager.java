package org.yashar.enchantedWanted.managers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PoliceAlertManager {

    private final Set<UUID> policeAlertEnabledPlayers = new HashSet<>();

    public void togglePoliceAlert(UUID playerId) {
        if (policeAlertEnabledPlayers.contains(playerId)) {
            policeAlertEnabledPlayers.remove(playerId);
        } else {
            policeAlertEnabledPlayers.add(playerId);
        }
    }

    public boolean isPoliceAlertEnabled(UUID playerId) {
        return policeAlertEnabledPlayers.contains(playerId);
    }
}