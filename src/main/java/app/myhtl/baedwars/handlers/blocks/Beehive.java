package app.myhtl.baedwars.handlers.blocks;

import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.BlockHandler;

public class Beehive implements BlockHandler {

    @Override
    public boolean onInteract(Interaction interaction) {
        return BlockHandler.super.onInteract(interaction);
    }

    @Override
    public Key getKey() {
        return Key.key("minecraft:beehive");
    }
}
