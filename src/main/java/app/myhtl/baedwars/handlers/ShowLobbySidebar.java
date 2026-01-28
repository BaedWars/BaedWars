package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.game.CoreGame;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.scoreboard.Sidebar;

public class ShowLobbySidebar {
    public static void handle(PlayerSpawnEvent event) {
        CoreGame.updateLobbySidebar();
        Sidebar sidebar = CoreGame.lobbySidebar;
        if (!sidebar.isViewer(event.getPlayer())) {
            sidebar.addViewer(event.getPlayer());
        }
    }
}
