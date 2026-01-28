package app.myhtl.baedwars.handlers;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;

public class PlaceBlock {
    public static void handle(PlayerBlockPlaceEvent event) {
        Block block = event.getBlock().withTag(Tag.Boolean("PlayerPlaced"), true);
        event.setBlock(block);
    }
}
