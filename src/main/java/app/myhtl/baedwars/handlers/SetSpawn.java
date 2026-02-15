package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.game.CoreGame;
import app.myhtl.baedwars.game.Team;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import app.myhtl.baedwars.Server;
import net.minestom.server.timer.Scheduler;

import java.util.Objects;

import static app.myhtl.baedwars.game.CoreGame.getSpawnPos;


public class SetSpawn {
    public static void handle(AsyncPlayerConfigurationEvent event, InstanceContainer instanceContainer, Scheduler scheduler) {
        final Player player = event.getPlayer();
        event.setSpawningInstance(instanceContainer);
        if (Server.permissionData.get(player.getUuid()) != null) {
            player.setPermissionLevel(Server.permissionData.get(player.getUuid()));
        }
        player.setGameMode(GameMode.ADVENTURE);
        //player.setGameMode(GameMode.CREATIVE);
        if (!Server.gameStarted) {
            if (Objects.equals(Team.getTeamFromPlayer(player).color, "")) {
                CoreGame.joinRandomTeam(player);
            }
            player.setRespawnPoint(getSpawnPos(player));
            Server.logger.info("Player {} has joined BedWars Lobby #{} ({}/{})",
                    player.getUsername(), Server.round_id, CoreGame.totalPlayers, CoreGame.playersPerTeams*CoreGame.teamsAmount);
        } else {
            if (Objects.equals(Team.getTeamFromPlayer(player).color, "")) {
                CoreGame.joinSpectatorTeam();
                Server.logger.info("Player {} has joined BedWars Game #{}",
                        player.getUsername(), Server.round_id);
            } else {
                Server.logger.info("Player {} has rejoined BedWars Game #{}",
                        player.getUsername(), Server.round_id);
            }
        }
    }
}
