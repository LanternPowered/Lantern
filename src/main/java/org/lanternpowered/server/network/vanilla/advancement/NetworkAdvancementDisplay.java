/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.advancement;

import com.flowpowered.math.vector.Vector2d;
import com.google.common.base.MoreObjects;
import org.lanternpowered.server.advancement.LanternAdvancementType;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.spongepowered.api.advancement.AdvancementType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public final class NetworkAdvancementDisplay {

    private final Text title;
    private final Text description;
    private final ItemStackSnapshot icon;
    private final LanternAdvancementType frameType;
    @Nullable private final String background;
    private final Vector2d position;
    private final boolean showToast;
    private final boolean hidden;

    public NetworkAdvancementDisplay(Text title, Text description, ItemStackSnapshot icon, AdvancementType frameType,
            @Nullable String background, Vector2d position, boolean showToast, boolean hidden) {
        this.frameType = (LanternAdvancementType) frameType;
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
        buf.writeVarInt(this.frameType.getInternalId());
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
