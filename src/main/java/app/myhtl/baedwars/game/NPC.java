package app.myhtl.baedwars.game;

import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.avatar.PlayerMeta;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class NPC extends Entity {
    private final String username;
    private final String type;
    private final String skinTexture;
    private final String skinSignature;

    public NPC(@NotNull String username, @Nullable String skinTexture, @Nullable String skinSignature, @NotNull String type) {
        super(EntityType.PLAYER);
        this.username = username;
        this.type = type;

        this.skinTexture = skinTexture;
        this.skinSignature = skinSignature;
        this.setTag(Tag.String("NPC"), type);

        setNoGravity(true);
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        var properties = new ArrayList<PlayerInfoUpdatePacket.Property>();
        if (skinTexture != null && skinSignature != null) {
            properties.add(new PlayerInfoUpdatePacket.Property("textures", skinTexture, skinSignature));
        }
        var entry = new PlayerInfoUpdatePacket.Entry(getUuid(), username, properties, false,
                0, GameMode.SURVIVAL, null, null, 0, false);
        player.sendPacket(new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry));

        // Spawn the player entity
        super.updateNewViewer(player);

        ((PlayerMeta) getEntityMeta()).setDisplayedSkinParts((byte) 0x7F);
    }

    @Override
    public void updateOldViewer(@NotNull Player player) {
        super.updateOldViewer(player);

        player.sendPacket(new PlayerInfoRemovePacket(getUuid()));
    }
}