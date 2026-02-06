package app.myhtl.baedwars.listeners;

import app.myhtl.baedwars.Server;
import app.myhtl.baedwars.game.ItemShop;
import app.myhtl.baedwars.game.TeamShop;
import app.myhtl.baedwars.handlers.*;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.timer.Scheduler;


public class BedwarsEventListener {
    public static EventNode<?> getPlayerEvent(Scheduler scheduler) {
        EventNode<PlayerEvent> playerNode = EventNode.value("bedwars-playernode", EventFilter.PLAYER, player -> player.getGameMode() == GameMode.SURVIVAL && !player.getName().toString().contains("NPC") && Server.gameStarted);
        playerNode.addListener(PlayerPickBlockEvent.class, MiddlePick::handle);
        playerNode.addListener(PlayerBlockBreakEvent.class, event -> BreakBlock.handle(event, scheduler));
        playerNode.addListener(PlayerBlockPlaceEvent.class, PlaceBlock::handle);
        playerNode.addListener(PlayerEntityInteractEvent.class, ClickNPC::handle);
        playerNode.addListener(PlayerSpawnEvent.class, ShowGameSidebar::handle);
        return playerNode;
    }
}
