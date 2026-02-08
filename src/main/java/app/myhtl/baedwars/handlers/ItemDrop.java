package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.Server;
import app.myhtl.baedwars.game.BuyableItem;
import app.myhtl.baedwars.game.ShopCategory;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.item.ItemStack;

import java.time.Duration;
import java.util.HashSet;

import static app.myhtl.baedwars.Server.itemShopData;
import static app.myhtl.baedwars.Server.permanentItems;

public class ItemDrop {
    public static void handle(ItemDropEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItemStack();

        ItemEntity entity = new ItemEntity(stack);
        entity.setPickupDelay(Duration.ofMillis(1250));

        for (BuyableItem buyableItem : permanentItems) {
            if (stack.material() == buyableItem.item) {
                event.setCancelled(true);
                return;
            }
        }

        double yaw = Math.toRadians(player.getPosition().yaw());
        double pitch = Math.toRadians(player.getPosition().pitch());
        double x = -Math.sin(yaw) * Math.cos(pitch);
        double y = -Math.sin(pitch) + 0.1;
        double z = Math.cos(yaw) * Math.cos(pitch);

        Vec velocity = new Vec(x, y, z).normalize().mul(6); // 0.3 ≈ Vanilla-Wert
        entity.setVelocity(velocity);
        entity.setInstance(player.getInstance(), player.getPosition().add(0, 1.5, 0));
    }
}
