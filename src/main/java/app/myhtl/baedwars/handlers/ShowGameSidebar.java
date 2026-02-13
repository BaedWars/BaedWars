package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.game.Team;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.scoreboard.Sidebar;

public class ShowGameSidebar {
    public static void handle(PlayerEvent event) {
        Player player = event.getPlayer();
        handle(player);
    }

    public static void handle(Player player) {
        Sidebar sidebar = Team.getTeamFromPlayer(player).sidebar;
        if (!sidebar.isViewer(player)) {
            sidebar.addViewer(player);
        }
    }
}
