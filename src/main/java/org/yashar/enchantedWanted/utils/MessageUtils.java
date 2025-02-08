package org.yashar.enchantedWanted.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class MessageUtils {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static void sendMessage(Player player, String message) {
        Component component = miniMessage.deserialize(message);
        String legacyMessage = LegacyComponentSerializer.legacySection().serialize(component);
        player.sendMessage(legacyMessage);
    }
}


