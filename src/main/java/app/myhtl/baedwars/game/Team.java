package app.myhtl.baedwars.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.scoreboard.Sidebar;

import java.util.UUID;

public class Team {
    public int alivePlayers;
    public boolean bedDestroyed = false;
    public String color = "";
    public Pos spawnPos;
    public Player[] players;
    public UUID[] playerUUIDs;
    public Sidebar sidebar;
}
