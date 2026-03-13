package app.myhtl.baedwars.commands;

import app.myhtl.baedwars.Server;
import app.myhtl.baedwars.game.CoreGame;
import app.myhtl.baedwars.game.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.minestom.actor.MinestomCommandActor;

import static app.myhtl.baedwars.game.CoreGame.teams;

public class AdminCommands {

    @Command("help")
    @Description("Provides a list of all commands")
    public void help(MinestomCommandActor actor) {
        if (actor.asPlayer() == null) {
            Server.logger.info(CoreGame.serverPrefixText + "You can only run this command as a player!");
        } else {
            actor.sender().sendMessage(CoreGame.serverPrefix.append(Component.text("WIP (not enough commands), /stop will stop this server")));
        }
    }

    @Command("skip")
    @Description("Skips the pre-game lobby")
    public void skip(MinestomCommandActor actor) {
        if (actor.asPlayer() instanceof Player senderP) {
            if (senderP.getPermissionLevel() >= 2) {
                actor.sender().sendMessage(CoreGame.serverPrefix.append(Component.text("Lobby has been skipped")));
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
            } else {
                actor.sender().sendMessage(CoreGame.serverPrefix.append(Component.text("You can't execute this command!")));
            }
        } else {
            Server.logger.info(CoreGame.serverPrefixText + "You can only run this command as a player!");
        }
    }

    @Command("stop")
    @Description("Stops the server")
    public void stop(MinestomCommandActor actor) {
        if (actor.asPlayer() == null) {
            MinecraftServer.stopCleanly();
        } else {
            actor.sender().sendMessage(CoreGame.serverPrefix.append(Component.text("You can't execute this command!")));
        }
    }
}
