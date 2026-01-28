package app.myhtl.baedwars.listeners;

import app.myhtl.baedwars.Server;
import io.github.togar2.pvp.events.FinalDamageEvent;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import app.myhtl.baedwars.handlers.*;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerDeathEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.timer.Scheduler;
import org.jetbrains.annotations.NotNull;

public class VanillaEventListener {
    public static EventNode<?> getPlayerEvent(Scheduler scheduler) {
        EventNode<@NotNull EntityEvent> entityNode = EventNode.value("vanilla-entitynode", EventFilter.ENTITY, player -> Server.gameStarted);
        entityNode.addListener(FinalDamageEvent.class, playerDeathEvent -> Death.handle(playerDeathEvent, scheduler));
        entityNode.addListener(ItemDropEvent.class, ItemDrop::handle);
        entityNode.addListener(PickupItemEvent.class, ItemPickup::handle);
        entityNode.addListener(PlayerMoveEvent.class, event -> VoidDeath.handle(event, scheduler));
        //entityNode.addListener(PlayerMoveEvent.class, event -> FallDamage.handle(event, scheduler));

        return entityNode;
    }
}

