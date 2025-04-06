package org.yashar.enchantedWanted.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.yashar.enchantedWanted.EnchantedWanted;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@UtilityClass
public class ConfigUtil {

    private static final String CONFIG = "config.yml";

    @SneakyThrows
    public void loadConfig() {
        final String path = EnchantedWanted.getInstance().getDataFolder() + File.separator + CONFIG;
        final Path destinationPath = Path.of(path);
        final InputStream inputStream = ConfigUtil.class.getResourceAsStream("/" + CONFIG);
        if (inputStream == null) return;

        Files.copy(inputStream, Path.of(path + ".bak"), StandardCopyOption.REPLACE_EXISTING);

        if (Files.exists(destinationPath)) {
            final File newConfigFile = new File(path + ".bak");
            YamlUtil.update(destinationPath.toFile(), newConfigFile);
            newConfigFile.delete();
            return;
        }

        Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        inputStream.close();
    }
}