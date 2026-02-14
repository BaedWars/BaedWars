package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.Server;
import app.myhtl.baedwars.game.CoreGame;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.timer.Scheduler;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class LobbySpawn {
    public static void handle(PlayerSpawnEvent event, Scheduler scheduler) {
        Player player = event.getPlayer();
        player.setInvisible(true);
        CoreGame.updateLobbySidebar();
        player.teleport(Server.map.lobbySpawnPos);
        player.sendMessage(Component.text(player.getUsername()).color(AQUA).append(Component.text(" has joined ").color(WHITE),
                Component.text("(").color(WHITE),
                Component.text(CoreGame.totalPlayers).color(AQUA), Component.text("/").color(WHITE), Component.text(CoreGame.playersPerTeams*CoreGame.teamsAmount).color(AQUA),
                Component.text(")").color(WHITE)));
        CoreGame.startGame(scheduler);
    }
    public static void handleEverthing(CancellableEvent event) {
        event.setCancelled(true);
    }
}
