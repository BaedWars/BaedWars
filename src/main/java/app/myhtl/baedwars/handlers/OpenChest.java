package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.game.CoreGame;
import app.myhtl.baedwars.game.Team;
import net.kyori.adventure.text.Component;
import net.minestom.server.color.Color;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.timer.Scheduler;

public class OpenChest {
    public static void handle(PlayerBlockInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().compare(Block.CHEST)) {
            assert event.getBlock().nbt() != null;
            System.out.println(event.getBlock().nbt().get("Items").toString());
        } else if ((event.getBlock().compare(Block.ENDER_CHEST))) {
            player.openInventory(Team.getTeamFromPlayer(player).inv);
        }
        Inventory inventory = new Inventory(InventoryType.CHEST_6_ROW, Component.text("Team Chest"));
    }
}
