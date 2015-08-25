package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public final class MessagePlayInPlayerBlockPlacement implements Message {

    private final Vector3i position;
    private final Vector3d clickOffset;
    private final Direction face;
    private final ItemStack heldItem;

    public MessagePlayInPlayerBlockPlacement(Vector3i position, Vector3d clickOffset, Direction face, ItemStack heldItem) {
        this.clickOffset = checkNotNull(clickOffset, "click offset");
        this.position = checkNotNull(position, "position");
        this.heldItem = checkNotNull(heldItem, "held item");
        this.face = checkNotNull(face, "face");
    }

    public Vector3i getBlockPosition() {
        return this.position;
    }

    public Vector3d getClickOffset() {
        return this.clickOffset;
    }

    public Vector3d getClickPosition() {
        return this.position.toDouble().add(this.clickOffset);
    }

    public Direction getBlockFace() {
        return this.face;
    }

    public ItemStack getHeldItem() {
        return this.heldItem;
    }

}
