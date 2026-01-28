package app.myhtl.baedwars.game;

import app.myhtl.baedwars.Server;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;

public class TeamShop {
    public static void openShop(Player player) {
        Inventory inventory = new Inventory(InventoryType.CHEST_6_ROW, Component.text("Team Shop").color(TextColor.fromHexString("#55555")));
        player.openInventory(inventory);
    }
    public static void handle(InventoryPreClickEvent event) {
        //event.setCancelled(true);
    }
}
