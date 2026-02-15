package app.myhtl.baedwars.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.scoreboard.Sidebar;

import java.util.*;

import static app.myhtl.baedwars.game.CoreGame.teams;

public class Team {
    public int alivePlayers;
    public boolean bedDestroyed = false;
    public String color = "";
    public Pos spawnPos;
    public List<Player> players;
    public List<UUID> playerUUIDs;
    public Sidebar sidebar;
    public Inventory teamChest = new Inventory(InventoryType.CHEST_3_ROW, Component.text("Team Chest"));
    public Dictionary<UUID, Inventory> enderChests = new Hashtable<>();

    public static Team getTeamFromPlayer(Player player) {
        for (Team team : teams) {
            for (Player player1 : team.players) {
                if (player1 == player) {
                    return team;
                }
            }
            List<UUID> playerUUIDs = team.playerUUIDs;
            for (int i = 0; i < playerUUIDs.size(); i++) {
                UUID uuid = playerUUIDs.get(i);
                if (uuid != null) {
                    if (Objects.equals(uuid.toString(), player.getUuid().toString())) {
                        team.players.set(i, player);
                        return team;
                    }
                }
            }
        }
        return new Team();
    }
}
