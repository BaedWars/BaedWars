package app.myhtl.baedwars.loaders;

import app.myhtl.baedwars.Server;
import app.myhtl.baedwars.game.World;
import net.minestom.server.coordinate.Pos;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class WorldLoader {
    public static World load(Path path) {
        if (!Files.exists(path)) {
            setupExampleWorld();
        }
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(path) // Set where we will load and save to
                .build();
        CommentedConfigurationNode root;

        try {
            root = loader.load();
        } catch (IOException e) {
            Server.logger.error("Failed to load world");
            System.exit(1);
            return null;
        }

        var numberOfTeams = root.node("teams").childrenList().size();
        var savePath = Path.of(Objects.requireNonNull(root.node("savePath").getString()));

        var lobbySpawnPos = getPosFromNode(root.node("lobbySpawn"));
        var diaSpawnerPos = getPosFromNode(root.node("diaSpawner"));

        var teamColors = new String[numberOfTeams];
        var teamSpawnPoints = new Pos[numberOfTeams];
        var itemShopNPCPositions = new Pos[numberOfTeams];
        var teamShopNPCPositions = new Pos[numberOfTeams];
        var ironSpawnerPos = new Pos[numberOfTeams];

        for (int i = 0; i < root.node("teams").childrenList().size(); i++) {
            var teamNode = root.node("teams").childrenList().get(i);
            teamColors[i] = teamNode.node("color").getString();
            teamSpawnPoints[i] = getPosFromNode(teamNode.node("spawn"));
            itemShopNPCPositions[i] = getPosFromNode(teamNode.node("itemShop"));
            teamShopNPCPositions[i] = getPosFromNode(teamNode.node("teamShop"));
            ironSpawnerPos[i] = getPosFromNode(teamNode.node("ironSpawner"));
        }
        return new World(numberOfTeams, savePath, lobbySpawnPos, teamColors, teamSpawnPoints, itemShopNPCPositions, teamShopNPCPositions, ironSpawnerPos, diaSpawnerPos);
    }
    public static void setupExampleWorld() {
        Path worldsDir = Path.of("worlds");
        if (Files.isDirectory(worldsDir)) {
            return;
        }
        try {
            Files.createDirectories(worldsDir);

            try (InputStream zipStream = Server.class.getResourceAsStream("/defaultworld.zip")) {
                if (zipStream == null) {
                    throw new IllegalStateException("Resource not found: defaultworld.zip");
                }

                try (ZipInputStream zis = new ZipInputStream(zipStream)) {
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        Path target = worldsDir.resolve(entry.getName()).normalize();

                        // Prevent zip-slip so archive entries cannot escape the worlds directory.
                        if (!target.startsWith(worldsDir)) {
                            throw new IOException("Invalid zip entry: " + entry.getName());
                        }

                        if (entry.isDirectory()) {
                            Files.createDirectories(target);
                        } else {
                            Path parent = target.getParent();
                            if (parent != null) {
                                Files.createDirectories(parent);
                            }
                            Files.copy(zis, target, StandardCopyOption.REPLACE_EXISTING);
                        }
                        zis.closeEntry();
                    }
                }
            }

            try (InputStream worldConfigStream = Server.class.getResourceAsStream("/defaultworld.yml")) {
                if (worldConfigStream == null) {
                    throw new IllegalStateException("Resource not found: defaultworld.yml");
                }
                Files.copy(worldConfigStream, worldsDir.resolve("defaultworld.yml"), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to setup example world", e);
        }
    }
    public static Pos getPosFromNode(CommentedConfigurationNode posNode) {
        return new Pos(posNode.node("x").getDouble(), posNode.node("y").getDouble(), posNode.node("z").getDouble(), posNode.node("yaw").getFloat(), posNode.node("pitch").getFloat());
    }
}
