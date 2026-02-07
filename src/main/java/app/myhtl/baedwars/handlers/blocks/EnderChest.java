package app.myhtl.baedwars.handlers.blocks;

import app.myhtl.baedwars.game.Team;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class EnderChest implements BlockHandler {
    @Override
    public boolean onInteract(Interaction interaction) {
        Player player = interaction.getPlayer();
        player.openInventory(Team.getTeamFromPlayer(player).enderChests.get(player.getUuid()));
        return true;
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("minecraft:ender_chest");
    }
}
