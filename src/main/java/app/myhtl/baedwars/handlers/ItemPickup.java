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

        var itemStack = event.getItemStack();
        if (event.getLivingEntity() instanceof Player player){
            if (!CoreGame.addToPlayerInv(player, itemStack) | player.getGameMode() == GameMode.SPECTATOR) {
                event.setCancelled(true);
            }
        }
    }
}
