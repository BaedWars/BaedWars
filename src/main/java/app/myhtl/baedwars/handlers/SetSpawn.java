package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.game.CoreGame;
import app.myhtl.baedwars.game.Team;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import app.myhtl.baedwars.Server;
import java.awt.*;
import java.util.Objects;

import static app.myhtl.baedwars.game.CoreGame.getSpawnPos;


public class SetSpawn {
    public static void handle(AsyncPlayerConfigurationEvent event, InstanceContainer instanceContainer) {
        final Player player = event.getPlayer();
        event.setSpawningInstance(instanceContainer);
        if (!Server.gameStarted) {
            player.setGameMode(GameMode.ADVENTURE);
            //player.setGameMode(GameMode.CREATIVE);
            if (Objects.equals(Team.getTeamFromPlayer(player).color, "")) {
                CoreGame.joinRandomTeam(player);
            }
            player.setRespawnPoint(getSpawnPos(player));
        } else {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }
}
