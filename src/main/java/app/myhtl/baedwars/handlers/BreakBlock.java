package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.game.CoreGame;
import app.myhtl.baedwars.game.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.TitlePart;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.instance.Explosion;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Scheduler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class BreakBlock {
    private static final Block[] bedArray = new Block[]{
            Block.BLACK_BED, Block.BLUE_BED, Block.BROWN_BED, Block.CYAN_BED,
            Block.GRAY_BED, Block.GREEN_BED, Block.LIGHT_BLUE_BED, Block.LIGHT_GRAY_BED,
            Block.LIME_BED, Block.MAGENTA_BED, Block.ORANGE_BED, Block.PINK_BED,
            Block.PURPLE_BED, Block.RED_BED, Block.WHITE_BED, Block.YELLOW_BED
    };

    public static void handle(PlayerBlockBreakEvent event, Scheduler scheduler) {
        Block broken = event.getBlock().defaultState();
        Player player = event.getPlayer();
        Team team = CoreGame.getTeamFromPlayer(player);
        for (Block bed : bedArray) {
            if (broken == bed) {
                Instance instance = player.getInstance();
                if (instance == null) return;

                Point center = event.getBlockPosition().add(0.5, 0.5, 0.5);
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
                for (Player teamplayer : team.players) {
                    teamplayer.sendTitlePart(TitlePart.TITLE, Component.text("BED DESTROYED!").color(RED));
                    teamplayer.sendTitlePart(TitlePart.SUBTITLE, Component.text("You will no longer respawn!").color(WHITE));
                }
                team.bedDestroyed = true;
                CoreGame.updateTeamSidebar(team, scheduler);
                return;
            }
        }
        if (!event.getBlock().hasTag(Tag.Boolean("PlayerPlaced"))) {
            player.sendMessage(Component.text("You can't break that!").color(RED));
            event.setCancelled(true);
        }
    }
}
