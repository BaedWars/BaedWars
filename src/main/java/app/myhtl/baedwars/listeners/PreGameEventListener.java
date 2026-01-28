package app.myhtl.baedwars.listeners;

import app.myhtl.baedwars.Server;
import app.myhtl.baedwars.game.CoreGame;
import app.myhtl.baedwars.handlers.LobbySpawn;
import app.myhtl.baedwars.handlers.ShowLobbySidebar;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.timer.Scheduler;

public class PreGameEventListener {
    public static EventNode<?> getPlayerEvent(Scheduler scheduler) {
        EventNode<PlayerEvent> playerNode = EventNode.value("spectator-playernode", EventFilter.PLAYER, player -> !Server.gameStarted && !player.getName().toString().contains("NPC"));
        playerNode.addListener(PlayerSpawnEvent.class, event -> LobbySpawn.handle(event, scheduler));
        playerNode.addListener(PlayerMoveEvent.class, LobbySpawn::handleEverthing);
        playerNode.addListener(PlayerBlockBreakEvent.class, LobbySpawn::handleEverthing);
        playerNode.addListener(PlayerSpawnEvent.class, ShowLobbySidebar::handle);
        playerNode.addListener(PlayerDisconnectEvent.class, player -> CoreGame.updateLobbySidebar());
        return playerNode;
    }
}
