package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.Server;
import net.minestom.server.event.player.PlayerDisconnectEvent;

public class DisconnectMessage {

    public static void handle(PlayerDisconnectEvent event) {
        if (Server.gameStarted) {
            Server.logger.info("Player {} has left BedWars Game #{}",
                    event.getPlayer().getUsername(), Server.round_id);
        } else {
            Server.logger.info("Player {} has left BedWars Lobby #{}",
                    event.getPlayer().getUsername(), Server.round_id);
        }
    }
}
