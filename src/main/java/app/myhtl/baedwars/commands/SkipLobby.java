package app.myhtl.baedwars.commands;

import app.myhtl.baedwars.Server;
import app.myhtl.baedwars.game.CoreGame;
import app.myhtl.baedwars.game.Team;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;

import java.util.Objects;

import static app.myhtl.baedwars.game.CoreGame.teams;

public class SkipLobby extends Command {
    public SkipLobby() {
        super("skip");
        setDefaultExecutor((sender, context) -> {
            if (Objects.equals(sender.identity().uuid().toString(), "54912342-edf2-4833-a217-bc25b418422a")) {
                sender.sendMessage(CoreGame.serverPrefix.append(Component.text("Lobby has been skipped")));
                Server.gameStarted = true;
                Audiences.players().clearTitle();
                for (Team team : teams) {
                    for (Player player : team.players) {
                        if (player != null) {
                            player.teleport(team.spawnPos);
                            player.setGameMode(GameMode.SURVIVAL);
                            player.setInvisible(false);
                            CoreGame.lobbySidebar.removeViewer(player);
                            Team.getTeamFromPlayer(player).sidebar.addViewer(player);
                        }
                    }
                }
            }
        });
    }
}
