package app.myhtl.baedwars.loaders;

import app.myhtl.baedwars.Server;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ConfigLoader {
    public static Properties loadConfigData() {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Path.of("server.properties"))) {
            props.load(in);
        } catch (IOException e) {
            //throw new RuntimeException(e);
        }
        return props;
    }
    public static Map<UUID, Integer> loadPermissionData() {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(Path.of("permissions.yml")) // Set where we will load and save to
                .build();
        CommentedConfigurationNode root;

        try {
            root = loader.load();
        } catch (IOException e) {
            Server.logger.error("An error occurred while loading this configuration: {}", e.getMessage());
            return null;
        }

        Map<UUID, Integer> permissions = new HashMap<>();

        for (CommentedConfigurationNode permNode : root.childrenList()) {
            permissions.put(UUID.fromString(Objects.requireNonNull(permNode.key()).toString()), permNode.getInt());
        }
        return permissions;
    }
}
