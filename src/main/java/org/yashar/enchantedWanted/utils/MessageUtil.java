package org.yashar.enchantedWanted.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String colorize(String message) {
        if (message == null || message.isEmpty()) return "";

        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            String hexColor = matcher.group();
            message = message.replace(hexColor, ChatColor.of(hexColor).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendMessage(Player player, String message) {
        if (player != null && message != null && !message.isEmpty()) {
            player.sendMessage(colorize(message));
        }
    }

    public static String applyGradient(String text, Color start, Color end) {
        if (text == null || text.isEmpty()) return "";

        StringBuilder gradientText = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            int r = (int) (start.getRed() * (1 - ratio) + end.getRed() * ratio);
            int g = (int) (start.getGreen() * (1 - ratio) + end.getGreen() * ratio);
            int b = (int) (start.getBlue() * (1 - ratio) + end.getBlue() * ratio);

            Color newColor = new Color(r, g, b);
            gradientText.append(ChatColor.of(newColor)).append(text.charAt(i));
        }

        return gradientText.toString();
    }
}
