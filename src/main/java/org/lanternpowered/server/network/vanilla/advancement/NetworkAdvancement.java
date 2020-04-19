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
import com.google.common.collect.Iterables;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.CodecContext;

import java.util.Collection;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class NetworkAdvancement {

    private final String id;
    @Nullable private final String parentId;
    @Nullable private final NetworkAdvancementDisplay display;
    private final Collection<String> criteria;
    private final String[][] requirements;

    public NetworkAdvancement(String id, @Nullable String parentId, @Nullable NetworkAdvancementDisplay display,
            Collection<String> criteria, String[][] requirements) {
        this.requirements = requirements;
        this.parentId = parentId;
        this.criteria = criteria;
        this.display = display;
        this.id = id;
    }

    void write(CodecContext ctx, ByteBuffer buf) {
        buf.writeString(this.id);
        buf.writeBoolean(this.parentId != null);
        if (this.parentId != null) {
            buf.writeString(this.parentId);
        }
        buf.writeBoolean(this.display != null);
        if (this.display != null) {
            this.display.write(ctx, buf);
        }
        buf.writeVarInt(this.criteria.size());
        this.criteria.forEach(buf::writeString);
        buf.writeVarInt(this.requirements.length);
        for (String[] requirements : this.requirements) {
            buf.writeVarInt(requirements.length);
            for (String requirement : requirements) {
                buf.writeString(requirement);
            }
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", this.id)
                .add("parentId", this.parentId)
                .add("display", this.display)
                .add("criteria", Iterables.toString(this.criteria))
                .toString();
    }
}
