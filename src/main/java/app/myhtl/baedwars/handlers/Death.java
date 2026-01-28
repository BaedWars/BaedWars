package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.game.CoreGame;
import io.github.togar2.pvp.events.FinalDamageEvent;
import net.kyori.adventure.text.Component;

import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerDeathEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.timer.Scheduler;

public class Death {
    public static void handle(FinalDamageEvent event, Scheduler scheduler) {
        if (event.doesKillEntity() && event.getEntity() instanceof Player player) {
            event.setCancelled(true);
            CoreGame.killPlayer(player, scheduler);
        }
    }
}
