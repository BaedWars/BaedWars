package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.game.ItemShop;
import app.myhtl.baedwars.game.TeamShop;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.tag.Tag;

public class ClickNPC {
    public static void handle(PlayerEntityInteractEvent event) {
        Entity fp = event.getTarget();

        // Prüfen ob unser NPC
        if (fp.hasTag(Tag.String("NPC"))) {
            String type = fp.getTag(Tag.String("NPC"));

            switch (type) {
                case "SHOP" -> ItemShop.openShop(event.getPlayer());
                case "TEAMSHOP" -> TeamShop.openShop(event.getPlayer());
                default -> {
                    // nichts
                }
            }
        }
    }
}
