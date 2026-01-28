package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.game.CoreGame;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.timer.Scheduler;

public class VoidDeath {
    public static void handle(PlayerMoveEvent event, Scheduler scheduler) {
        if (event.getNewPosition().y() < 20) {
            CoreGame.killPlayer(event.getPlayer(), scheduler);
        }
    }
}
