package app.myhtl.baedwars.loaders;

import app.myhtl.baedwars.Server;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.System.exit;

public class ConfigLoader {
    public static Properties loadConfigData() {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Path.of("server.properties"))) {
            props.load(in);
        } catch (IOException e) {
            Server.logger.warn("Generated new {}, please configure before starting the server!", "server.properties");
            setupConfigFile("server.properties");
            exit(1);
            return null;
        }
        return props;
    }
    public static Map<UUID, Integer> loadPermissionData() {
        Path path = Path.of("permissions.yml");
        if (!Files.exists(path)) {
            setupConfigFile("permissions.yml");
        }
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(path) // Set where we will load and save to
                .build();
        CommentedConfigurationNode root;

        try {
            root = loader.load();
        } catch (IOException e) {
            return null;
        }

        Map<UUID, Integer> permissions = new HashMap<>();

        for (CommentedConfigurationNode permNode : root.childrenList()) {
            permissions.put(UUID.fromString(Objects.requireNonNull(permNode.key()).toString()), permNode.getInt());
        }
        return permissions;
    }
    public static void setupConfigFile(String filename) {
        try (InputStream s = Server.class.getResourceAsStream("/" +filename)) {
            assert s != null;
            Files.copy(s, Path.of(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
