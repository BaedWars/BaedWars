package app.myhtl.baedwars.handlers.blocks;

import app.myhtl.baedwars.game.Team;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.BlockHandler;
import org.jetbrains.annotations.NotNull;

public class Chest implements BlockHandler {

    @Override
    public boolean onInteract(Interaction interaction) {
        Player player = interaction.getPlayer();
        player.openInventory(Team.getTeamFromPlayer(player).teamChest);
        return true;
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("minecraft:chest");
    }
}
