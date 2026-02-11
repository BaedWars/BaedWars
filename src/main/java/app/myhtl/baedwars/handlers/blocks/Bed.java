package app.myhtl.baedwars.handlers.blocks;

import app.myhtl.baedwars.game.CoreGame;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Explosion;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class Bed implements BlockHandler {

    @Override
    public void onDestroy(Destroy destroy) {
        Instance instance = destroy.getInstance();

        Point center = destroy.getBlockPosition().add(0.5, 0.5, 0.5);
        final float radius = 2.0f;
        Explosion explosion = new Explosion((float) center.x(), (float) center.y(), (float) center.z(), radius) {
            @Override
            protected @NotNull List<Point> prepare(@NotNull Instance instance) {
                final double cx = center.x();
                final double cy = center.y();
                final double cz = center.z();

                final int minX = (int) Math.floor(cx - radius);
                final int maxX = (int) Math.floor(cx + radius);
                final int minYLoop = (int) Math.floor(cy - radius);
                final int maxYLoop = (int) Math.floor(cy + radius);
                final int minZ = (int) Math.floor(cz - radius);
                final int maxZ = (int) Math.floor(cz + radius);

                final double r2 = radius * radius;
                List<Point> points = new ArrayList<>();

                for (int x = minX; x <= maxX; x++) {
                    for (int y = minYLoop; y <= maxYLoop; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            double dx = (x + 0.5) - cx;
                            double dy = (y + 0.5) - cy;
                            double dz = (z + 0.5) - cz;
                            if ((dx * dx + dy * dy + dz * dz) > r2) continue;

                            Block block = instance.getBlock(x, y, z);
                            if (block == Block.AIR) continue; // Luft überspringen

                            points.add(new Vec(x, y, z));
                        }
                    }
                }
                return points;
            }
        };
        explosion.apply(instance);
    }

    @Override
    public Key getKey() {
        return Key.key("minecraft:bed");
    }
}
