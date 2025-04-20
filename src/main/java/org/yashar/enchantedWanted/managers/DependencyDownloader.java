package org.yashar.enchantedWanted.managers;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

public class DependencyDownloader {
    private static final File LIBS_DIR = new File("plugins/EnchantedWanted/libs");

    public static void load(String groupId, String artifactId, String version) throws Exception {
        if (!LIBS_DIR.exists()) LIBS_DIR.mkdirs();

        String baseUrl = "https://repo1.maven.org/maven2";
        String path = groupId.replace(".", "/") + "/" + artifactId + "/" + version;
        String fileName = artifactId + "-" + version + ".jar";
        File jarFile = new File(LIBS_DIR, fileName);

        if (!jarFile.exists()) {
            String fullUrl = baseUrl + "/" + path + "/" + fileName;
            try (InputStream in = new URL(fullUrl).openStream()) {
                Files.copy(in, jarFile.toPath());
            }
        }

        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(classLoader, jarFile.toURI().toURL());
    }
}
