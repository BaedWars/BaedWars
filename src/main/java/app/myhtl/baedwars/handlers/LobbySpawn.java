package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.Server;
import app.myhtl.baedwars.game.CoreGame;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.advancements.Notification;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.Scheduler;

public class LobbySpawn {
    public static void handle(PlayerSpawnEvent event, Scheduler scheduler) {
        Player player = event.getPlayer();
        player.setInvisible(true);
        CoreGame.updateLobbySidebar();
        player.teleport(Server.map.lobbySpawnPos);
        CoreGame.startGame(scheduler);
    }
    public static void handleEverthing(CancellableEvent event) {
        event.setCancelled(true);
    }
}
