package app.myhtl.baedwars.handlers;

import net.minestom.server.event.player.PlayerBlockInteractEvent;

public class InteractBlock {
    public static void handle(PlayerBlockInteractEvent event) {
        OpenChest.handle(event);
    }
}
