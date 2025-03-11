package org.yashar.enchantedWanted.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WantedRemoveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final UUID uuid;
    public WantedRemoveEvent(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    public boolean isCancelled() {
        return cancelled;
    }
    public Player getPlayer() {
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) {
            return null;
        }
        if (p.isOnline()) {
            return p;
        }
        return null;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
