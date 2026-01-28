package app.myhtl.baedwars.listeners;

import app.myhtl.baedwars.game.ItemShop;
import app.myhtl.baedwars.game.TeamShop;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.trait.PlayerEvent;
import app.myhtl.baedwars.handlers.SetSpawn;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.timer.Scheduler;

public class AllEventListener {
    public static EventNode<?> getPlayerEvent(InstanceContainer instanceContainer, Scheduler scheduler) {
        EventNode<PlayerEvent> playerNode = EventNode.type("all-playernode", EventFilter.PLAYER);
        playerNode.addListener(AsyncPlayerConfigurationEvent.class, event -> SetSpawn.handle(event, instanceContainer));
        playerNode.addListener(InventoryPreClickEvent.class, ItemShop::handle);
        playerNode.addListener(InventoryPreClickEvent.class, TeamShop::handle);
        return playerNode;
    }
}
