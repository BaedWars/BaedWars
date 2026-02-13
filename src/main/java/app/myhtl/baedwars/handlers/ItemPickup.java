package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.game.CoreGame;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.network.packet.server.ServerPacket;

public class ItemPickup {
    public static void handle(PickupItemEvent event) {
        if (event.getLivingEntity() instanceof Player player) {
            var inventory = player.getInventory();
            if (CoreGame.getFreePlayerInvSlots(player) <= 0 | player.getGameMode() == GameMode.SPECTATOR) {
                event.setCancelled(true);
            } else {
                inventory.addItemStack(event.getItemStack());
            }
        }
    }
}
