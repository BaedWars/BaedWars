package app.myhtl.baedwars.loaders;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfigLoader {
    public static Map<UUID, Integer> loadPermissionData() {
        Map<UUID, Integer> permissions = new HashMap<>();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream("permissions.yml");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Yaml yaml = new Yaml();
        Map<String, Object> rawData = yaml.load(inputStream);
        for (String key : rawData.keySet()) {
            permissions.put(UUID.fromString(key), (Integer) rawData.get(key));
        }
        return permissions;
    }
}
