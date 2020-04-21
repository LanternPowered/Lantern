/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network.vanilla.advancement;

import com.google.common.base.MoreObjects;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.registry.type.advancement.AdvancementTypeRegistry;
import org.spongepowered.api.advancement.AdvancementType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.math.vector.Vector2d;

public final class NetworkAdvancementDisplay {

    private final Text title;
    private final Text description;
    private final ItemStackSnapshot icon;
    private final AdvancementType frameType;
    @Nullable private final String background;
    private final Vector2d position;
    private final boolean showToast;
    private final boolean hidden;

    public NetworkAdvancementDisplay(Text title, Text description, ItemStackSnapshot icon, AdvancementType frameType,
            @Nullable String background, Vector2d position, boolean showToast, boolean hidden) {
        this.frameType = frameType;
        this.description = description;
        this.background = background;
        this.showToast = showToast;
        this.position = position;
        this.hidden = hidden;
        this.title = title;
        this.icon = icon;
    }

    void write(CodecContext ctx, ByteBuffer buf) {
        ctx.write(buf, ContextualValueTypes.TEXT, this.title);
        ctx.write(buf, ContextualValueTypes.TEXT, this.description);
        ctx.write(buf, ContextualValueTypes.ITEM_STACK, this.icon.createStack());
        buf.writeVarInt(AdvancementTypeRegistry.get().getId(this.frameType));
        int flags = 0;
        if (this.background != null) {
            flags |= 0x1;
        }
        if (this.showToast) {
            flags |= 0x2;
        }
        if (this.hidden) {
            flags |= 0x4;
        }
        buf.writeInt(flags);
        if (this.background != null) {
            buf.writeString(this.background);
        }
        buf.writeFloat((float) this.position.getX());
        buf.writeFloat((float) this.position.getY());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("icon", this.icon)
                .add("title", this.title)
                .add("description", this.description)
                .add("type", this.frameType.getKey().toString())
                .add("background", this.background)
                .add("showToast", this.showToast)
                .add("hidden", this.hidden)
                .add("position", this.position)
                .toString();
    }
}
