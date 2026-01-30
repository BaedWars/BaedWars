package app.myhtl.baedwars.handlers;

import app.myhtl.baedwars.game.Team;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPickBlockEvent;
import net.minestom.server.item.ItemStack;

public class MiddlePick {
    public static void handle(PlayerPickBlockEvent event) {
        Player player = event.getPlayer();
        for (int i = 36; i < 44; i++) {
            ItemStack itemStack = player.getInventory().getItemStack(i);
            if (itemStack.material().id() == event.getBlock().id()) {
                player.setHeldItemSlot( (byte) (i-36) );
                return;
            }
        }
        player.sendMessage("You are in Team: " + Team.getTeamFromPlayer(player).color);
    }
}
