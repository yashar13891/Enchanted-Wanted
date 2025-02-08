package org.yashar.enchantedWanted.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtils {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static void sendMessage(Player player, String message) {
        Component component = miniMessage.deserialize(message);
        String legacyMessage = LegacyComponentSerializer.legacySection().serialize(component);
        player.sendMessage(legacyMessage);
    }
    public static void sendClickableCommand(Player player, String message, String command) {
        Component component = miniMessage.deserialize(message);
        String legacyMessage = LegacyComponentSerializer.legacySection().serialize(component);
        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(legacyMessage));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
        player.spigot().sendMessage(textComponent);
    }

}


