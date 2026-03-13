package app.myhtl.baedwars.game;

import net.minestom.server.coordinate.Pos;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class World {
    public int numberOfTeams;
    public Path savePath;
    public Pos diaSpawnerPos;
    public Pos lobbySpawnPos;
    public String[] teamColors;
    public Pos[] teamSpawnPoints;
    // Neue Felder: Positionen der Shop-NPCs je Team (optional)
    public Pos[] itemShopNPCPositions;
    public Pos[] teamShopNPCPositions;
    public Pos[] ironSpawnerPos;

    public World(int numberOfTeams, Path savePath, Pos lobbySpawnPos, String[] teamColors, Pos[] teamSpawnPoints, Pos[] itemShopNPCPositions, Pos[] teamShopNPCPositions, Pos[] ironSpawnerPos, Pos diaSpawnerPos) {
        this.numberOfTeams = numberOfTeams;
        this.savePath = savePath;
        this.lobbySpawnPos = lobbySpawnPos;
        this.teamColors = teamColors;
        this.teamSpawnPoints = teamSpawnPoints;
        this.itemShopNPCPositions = itemShopNPCPositions;
        this.teamShopNPCPositions = teamShopNPCPositions;
        this.ironSpawnerPos = ironSpawnerPos;
        this.diaSpawnerPos = diaSpawnerPos;
    }
}
