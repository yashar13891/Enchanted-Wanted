package org.yashar.enchantedWanted.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.yashar.enchantedWanted.EnchantedWanted;

import java.util.HashMap;
import java.util.Map;

public class MessageUtils {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacySection();

    private static final Map<Character, String> LEGACY_MAP = new HashMap<>();

    static {
        LEGACY_MAP.put('0', "black");
        LEGACY_MAP.put('1', "dark_blue");
        LEGACY_MAP.put('2', "dark_green");
        LEGACY_MAP.put('3', "dark_aqua");
        LEGACY_MAP.put('4', "dark_red");
        LEGACY_MAP.put('5', "dark_purple");
        LEGACY_MAP.put('6', "gold");
        LEGACY_MAP.put('7', "gray");
        LEGACY_MAP.put('8', "dark_gray");
        LEGACY_MAP.put('9', "blue");
        LEGACY_MAP.put('a', "green");
        LEGACY_MAP.put('b', "aqua");
        LEGACY_MAP.put('c', "red");
        LEGACY_MAP.put('d', "light_purple");
        LEGACY_MAP.put('e', "yellow");
        LEGACY_MAP.put('f', "white");
        LEGACY_MAP.put('k', "obfuscated");
        LEGACY_MAP.put('l', "bold");
        LEGACY_MAP.put('m', "strikethrough");
        LEGACY_MAP.put('n', "underlined");
        LEGACY_MAP.put('o', "italic");
    }
    private static String convertLegacyCodesToMiniMessage(String message) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char current = message.charAt(i);
            if (current == '&' && i + 1 < message.length()) {
                char next = Character.toLowerCase(message.charAt(i + 1));
                if (LEGACY_MAP.containsKey(next) || next == 'r') {
                    if (next == 'r') {
                        builder.append("<reset>");
                    } else {
                        builder.append("<").append(LEGACY_MAP.get(next)).append(">");
                    }
                    i++;
                    continue;
                }
            }
            builder.append(current);
        }
        return builder.toString();
    }
    public static String sendMessage(Player player, String message) {
        String processed = convertLegacyCodesToMiniMessage(message);
        Component component = miniMessage.deserialize(processed);
        String legacyMessage = legacySerializer.serialize(component);
        if (EnchantedWanted.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            player.sendMessage(PlaceholderAPI.setPlaceholders(player, legacyMessage));
        } else {
            player.sendMessage(legacyMessage);
        }
        return processed;
    }
    public static String colorize(String message) {
        String processed = convertLegacyCodesToMiniMessage(message);
        Component component = miniMessage.deserialize(processed);
        return legacySerializer.serialize(component);
    }
    public static void sendClickableCommand(Player player, String message, String command) {
        String processed = convertLegacyCodesToMiniMessage(message);
        Component component = miniMessage.deserialize(processed);
        String legacyMessage = legacySerializer.serialize(component);
        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(legacyMessage));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
        player.spigot().sendMessage(textComponent);
    }
}
