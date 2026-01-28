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
    public Pos lobbySpawnPos = new Pos(7.5, 76, -5.5, -90, 0);
    public String[] teamColors;
    public Pos[] teamSpawnPoints;
    // Neue Felder: Positionen der Shop-NPCs je Team (optional)
    public Pos[] itemShopNPCPositions;
    public Pos[] teamShopNPCPositions;

    // Kompakte Ladefunktion (≤50 Zeilen): YAML -> Map
    public static World load(Path yamlPath) throws IOException {
        try (InputStream in = Files.newInputStream(yamlPath);
             InputStreamReader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            Object rootObj = new Yaml().load(r);
            if (!(rootObj instanceof java.util.Map<?, ?> root))
                throw new IOException("Ungültiges YAML-Format: Root ist kein Mapping");

            World cfg = new World();
            Object sp = root.get("savePath");
            if (sp instanceof String s && !s.isBlank()) cfg.savePath = Path.of(s);

            Object teamsObj = root.get("teams");
            if (!(teamsObj instanceof Iterable<?> list))
                throw new IOException("Feld 'teams' fehlt oder ist kein Array");

            List<String> colors = new ArrayList<>();
            List<Pos> spawns = new ArrayList<>();
            // Sammler für optionale Shop-Positionen
            List<Pos> itemShops = new ArrayList<>();
            List<Pos> teamShops = new ArrayList<>();
            for (Object item : list) {
                if (!(item instanceof java.util.Map<?, ?> t)) continue;
                Object c = t.get("color");
                if (c instanceof String sc) colors.add(sc);
                Object so = t.get("spawn");
                if (so instanceof java.util.Map<?, ?> m) {
                    double x = asDouble(m.get("x"), 0), y = asDouble(m.get("y"), 64), z = asDouble(m.get("z"), 0);
                    float yaw = (float) asDouble(m.get("yaw"), 0), pitch = (float) asDouble(m.get("pitch"), 0);
                    spawns.add(new Pos(x, y, z, yaw, pitch));
                } else {
                    spawns.add(null);
                }
                // itemShop optional
                Object iso = t.get("itemShop");
                if (iso instanceof java.util.Map<?, ?> m) {
                    double x = asDouble(m.get("x"), 0), y = asDouble(m.get("y"), 64), z = asDouble(m.get("z"), 0);
                    float yaw = (float) asDouble(m.get("yaw"), 0), pitch = (float) asDouble(m.get("pitch"), 0);
                    itemShops.add(new Pos(x, y, z, yaw, pitch));
                } else {
                    itemShops.add(null);
                }
                // teamShop optional
                Object tso = t.get("teamShop");
                if (tso instanceof java.util.Map<?, ?> m) {
                    double x = asDouble(m.get("x"), 0), y = asDouble(m.get("y"), 64), z = asDouble(m.get("z"), 0);
                    float yaw = (float) asDouble(m.get("yaw"), 0), pitch = (float) asDouble(m.get("pitch"), 0);
                    teamShops.add(new Pos(x, y, z, yaw, pitch));
                } else {
                    teamShops.add(null);
                }
            }
            int n = Math.min(colors.size(), spawns.size());
            if (n == 0) throw new IOException("Keine Teams in der Konfiguration gefunden");
            cfg.numberOfTeams = n;
            cfg.teamColors = colors.subList(0, n).toArray(new String[0]);
            cfg.teamSpawnPoints = spawns.subList(0, n).toArray(new Pos[0]);
            // Arrays für Shop-Positionen (können null enthalten, wenn nicht gesetzt)
            cfg.itemShopNPCPositions = itemShops.subList(0, n).toArray(new Pos[0]);
            cfg.teamShopNPCPositions = teamShops.subList(0, n).toArray(new Pos[0]);
            return cfg;
        }
    }

    private static double asDouble(Object o, double def) {
        if (o instanceof Number n) return n.doubleValue();
        if (o instanceof String s) try { return Double.parseDouble(s); } catch (NumberFormatException ignored) {}
        return def;
    }
}
