package org.yashar.enchantedWanted.utils;


import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class MessageUtils {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static void send(Player player, String message) {
        Component component = miniMessage.deserialize(message);
        player.sendMessage(String.valueOf(component));
    }

    public static Component format(String message) {
        return miniMessage.deserialize(message);
    }
}

