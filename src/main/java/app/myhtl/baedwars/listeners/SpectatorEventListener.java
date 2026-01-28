package app.myhtl.baedwars.listeners;

import net.minestom.server.entity.GameMode;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.PlayerEvent;

public class SpectatorEventListener {
    public static EventNode<?> getPlayerEvent() {
        EventNode<PlayerEvent> playerNode = EventNode.value("spectator-playernode", EventFilter.PLAYER, player -> player.getGameMode() == GameMode.SPECTATOR);

        return playerNode;
    }
}
