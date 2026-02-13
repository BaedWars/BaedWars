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
import java.util.Arrays;
import java.util.List;

import static app.myhtl.baedwars.Server.bedList;
import static app.myhtl.baedwars.game.CoreGame.teams;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class BreakBlock {

    public static void handle(PlayerBlockBreakEvent event, Scheduler scheduler) {
        Block broken = event.getBlock().defaultState();
        Player player = event.getPlayer();
        Team destroyedTeam = null;
        if (bedList.contains(broken)) {
            for (Team team: teams) {
                if (broken.name().contains(team.color.toLowerCase())) {
                    destroyedTeam = team;
                    break;
                }
            }
            if (destroyedTeam != null) {
                for (Player teamplayer : destroyedTeam.players) {
                    if (teamplayer != null) {
                        teamplayer.sendTitlePart(TitlePart.TITLE, Component.text("BED DESTROYED!").color(RED));
                        teamplayer.sendTitlePart(TitlePart.SUBTITLE, Component.text("You will no longer respawn!").color(WHITE));
                    }
                }
                destroyedTeam.bedDestroyed = true;
                for (Team team : teams) {
                    CoreGame.updateTeamSidebar(team, scheduler);
                }
            }
        } else if (!event.getBlock().hasTag(Tag.Boolean("PlayerPlaced"))) {
            player.sendMessage(Component.text("You can't break that!").color(RED));
            event.setCancelled(true);
        }
    }
}
