package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.Server;
import net.minestom.server.event.player.PlayerCommandEvent;

public class PlayerCommand {
    public static void handle(PlayerCommandEvent event) {
        Server.logger.info("Player {} issued server command: {}", event.getPlayer().getUsername(), event.getCommand());
    }
}
