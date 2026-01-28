package app.myhtl.baedwars.listeners;

import app.myhtl.baedwars.Server;
import app.myhtl.baedwars.game.CoreGame;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerGameModeChangeEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.timer.Scheduler;

public class DeadEventListener {
    public static EventNode<?> getPlayerEvent(Scheduler scheduler) {
        EventNode<PlayerEvent> playerNode = EventNode.value("spectator-playernode", EventFilter.PLAYER, player -> Server.gameStarted && !player.getName().toString().contains("NPC") && CoreGame.getTeamFromPlayer(player).bedDestroyed && player.getGameMode() == GameMode.SPECTATOR);
        return playerNode;
    }
}
