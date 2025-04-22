package org.yashar.enchantedWanted.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class WantedAddEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Setter
    private boolean cancelled;

    private final UUID uuid;


    public Player getPlayer() {
        Player p = Bukkit.getPlayer(uuid);
        return (p != null && p.isOnline()) ? p : null;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
