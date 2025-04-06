package org.yashar.enchantedWanted.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

@UtilityClass
public class YamlUtil {

    public void update(File oldConfigFile, File newConfigFile) {
        final FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(oldConfigFile);
        final FileConfiguration newConfig = YamlConfiguration.loadConfiguration(newConfigFile);

        updateRecursive(oldConfig, newConfig, "");

        try {
            oldConfig.save(oldConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateRecursive(FileConfiguration oldConfig, FileConfiguration newConfig, String path) {
        final Set<String> keys = newConfig.getConfigurationSection(path.isEmpty() ? "" : path).getKeys(false);

        for (String key : keys) {
            final String fullPath = path.isEmpty() ? key : path + "." + key;

            if (!oldConfig.contains(fullPath)) {
                oldConfig.set(fullPath, newConfig.get(fullPath));
            } else if (newConfig.isConfigurationSection(fullPath)) {
                updateRecursive(oldConfig, newConfig, fullPath);
            }
        }
    }
}