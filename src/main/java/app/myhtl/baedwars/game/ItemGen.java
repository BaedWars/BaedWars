package app.myhtl.baedwars.game;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.time.TimeUnit;

import java.time.Duration;

public class ItemGen {
    public static void item(Pos[] posIrGoSpawner, InstanceContainer instanceContainer, Material material) {
        for (Pos posSpawner : posIrGoSpawner) {
            ItemEntity itemEntity = new ItemEntity(ItemStack.builder(material).build());
            itemEntity.setMergeRange(5);
            itemEntity.setMergeable(true);
            itemEntity.setPickupDelay(Duration.of(500, TimeUnit.MILLISECOND));
            itemEntity.setInstance(instanceContainer, posSpawner);
        }
    }
    public static void cleanup(Pos[] posIrGoSpawner, InstanceContainer instanceContainer) {
        for (Pos posSpawner : posIrGoSpawner) {
            for (Entity entity: instanceContainer.getNearbyEntities(posSpawner, 10)) {
                if (entity instanceof ItemEntity item) {
                    if (item.getTimeSinceSpawn() > 60) {
                        item.remove();
                    }
                }
            }
        }
    }
}
