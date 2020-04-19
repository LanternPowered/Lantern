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
package org.lanternpowered.server.network.entity.parameter;

import org.lanternpowered.server.entity.Pose;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecUtils;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;
import org.spongepowered.math.vector.Vector3f;
import org.spongepowered.math.vector.Vector3i;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

public final class ParameterValueTypes {

    public static final ParameterValueType<Byte> BYTE = new ParameterValueType<>(ByteBuffer::writeByte);
    public static final ParameterValueType<Integer> INT = new ParameterValueType<>(ByteBuffer::writeVarInt);
    public static final ParameterValueType<Float> FLOAT = new ParameterValueType<>(ByteBuffer::writeFloat);
    public static final ParameterValueType<String> STRING = new ParameterValueType<>(ByteBuffer::writeString);
    public static final ParameterValueType<Text> TEXT = new ParameterValueType<>(
            (ctx, buf, value) -> ctx.write(buf, ContextualValueTypes.TEXT, value));
    public static final ParameterValueType<Optional<Text>> OPTIONAL_TEXT = new ParameterValueType<>((ctx, buf, value) -> {
        buf.writeBoolean(value.isPresent());
        value.ifPresent(text -> ctx.write(buf, ContextualValueTypes.TEXT, text));
    });
    public static final ParameterValueType<ItemStack> ITEM_STACK = new ParameterValueType<>(
            (ctx, buf, value) -> ctx.write(buf, ContextualValueTypes.ITEM_STACK, value));
    public static final ParameterValueType<Boolean> BOOLEAN = new ParameterValueType<>(ByteBuffer::writeBoolean);
    public static final ParameterValueType<Vector3f> VECTOR_3F = new ParameterValueType<>(ByteBuffer::writeVector3f);
    public static final ParameterValueType<Vector3i> VECTOR_3I = new ParameterValueType<>(ByteBuffer::writePosition);
    public static final ParameterValueType<Optional<Vector3i>> OPTIONAL_VECTOR_3I = new ParameterValueType<>((buf, value) -> {
        buf.writeBoolean(value.isPresent());
        value.ifPresent(buf::writePosition);
    });
    public static final ParameterValueType<Direction> DIRECTION = new ParameterValueType<>(
            (buf, value) -> buf.writeVarInt(CodecUtils.encodeDirection(value)));
    public static final ParameterValueType<Optional<UUID>> OPTIONAL_UUID = new ParameterValueType<>((buf, value) -> {
        buf.writeBoolean(value.isPresent());
        value.ifPresent(buf::writeUniqueId);
    });
    public static final ParameterValueType<Optional<BlockState>> OPTIONAL_BLOCK_STATE = new ParameterValueType<>(
            (buf, value) -> buf.writeVarInt(value.map(v -> BlockRegistryModule.get().getStateInternalId(v)).orElse(0)));
    public static final ParameterValueType<Optional<DataView>> NBT_TAG = new ParameterValueType<>(
            (buf, value) -> buf.writeDataView(value.orElse(null)));
    public static final ParameterValueType<Void> PARTICLE = new ParameterValueType<>(
            (ctx, buf, value) -> { throw new UnsupportedOperationException("TODO"); }); // TODO
    public static final ParameterValueType<Void> VILLAGER_DATA = new ParameterValueType<>(
            (ctx, buf, value) -> { throw new UnsupportedOperationException("TODO"); }); // TODO
    public static final ParameterValueType<OptionalInt> OPTIONAL_INT = new ParameterValueType<>((buf, value) -> {
        if (value.isPresent()) {
            // What about -1? And Integer.MAX_VALUE?
            buf.writeVarInt(value.getAsInt() + 1);
        } else {
            buf.writeVarInt(0);
        }
    });
    public static final ParameterValueType<Pose> POSE = new ParameterValueType<>(
            (buf, value) -> buf.writeVarInt(value.ordinal()));
}
