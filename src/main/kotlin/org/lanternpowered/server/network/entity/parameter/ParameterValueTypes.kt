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
package org.lanternpowered.server.network.entity.parameter

import org.lanternpowered.api.block.BlockState
import org.lanternpowered.api.data.persistence.DataView
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.Direction
import org.lanternpowered.server.entity.Pose
import org.lanternpowered.server.network.item.NetworkItemStack
import org.lanternpowered.server.network.text.NetworkText
import org.lanternpowered.server.network.value.VillagerData
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecUtils
import org.lanternpowered.server.registry.type.block.BlockStateRegistry
import org.lanternpowered.server.registry.type.data.ProfessionTypeRegistry
import org.lanternpowered.server.registry.type.data.VillagerTypeRegistry
import org.spongepowered.math.vector.Vector3f
import org.spongepowered.math.vector.Vector3i
import java.util.UUID

object ParameterValueTypes {

    @JvmField val BYTE: ParameterValueType<Byte> = parameterValueType { buf, value -> buf.writeByte(value) }

    @JvmField val INT: ParameterValueType<Int> = parameterValueType { buf, value -> buf.writeVarInt(value) }

    @JvmField val FLOAT: ParameterValueType<Float> = parameterValueType { buf, value -> buf.writeFloat(value) }

    @JvmField val STRING: ParameterValueType<String> = parameterValueType { buf, value -> buf.writeString(value) }

    @JvmField val TEXT: ParameterValueType<Text> = ParameterValueType(NetworkText)

    @JvmField val OPTIONAL_TEXT: ParameterValueType<Text?> = parameterValueType { ctx, buf, value ->
        buf.writeBoolean(value != null)
        if (value != null)
            NetworkText.write(ctx, buf, value)
    }

    @JvmField val ITEM_STACK: ParameterValueType<ItemStack> = parameterValueType(NetworkItemStack::write)

    @JvmField val BOOLEAN: ParameterValueType<Boolean> = parameterValueType { buf, value -> buf.writeBoolean(value) }

    @JvmField val VECTOR_3F: ParameterValueType<Vector3f> = parameterValueType { buf, value -> buf.writeVector3f(value) }

    @JvmField val BLOCK_POSITION: ParameterValueType<Vector3i> = parameterValueType { buf, value -> buf.writeBlockPosition(value) }

    @JvmField val OPTIONAL_BLOCK_POSITION: ParameterValueType<Vector3i?> = parameterValueType { buf, value ->
        buf.writeBoolean(value != null)
        if (value != null)
            buf.writeBlockPosition(value)
    }

    @JvmField val DIRECTION: ParameterValueType<Direction> = parameterValueType { buf, value -> buf.writeVarInt(CodecUtils.encodeDirection(value)) }

    @JvmField val OPTIONAL_UUID: ParameterValueType<UUID?> = parameterValueType { buf, value ->
        buf.writeBoolean(value != null)
        if (value != null)
            buf.writeUniqueId(value)
    }

    @JvmField val OPTIONAL_BLOCK_STATE: ParameterValueType<BlockState?> = parameterValueType { buf, value ->
        buf.writeVarInt(value?.let(BlockStateRegistry::getId) ?: 0)
    }

    @JvmField val NBT_TAG: ParameterValueType<DataView?> = parameterValueType { buf, value -> buf.writeDataView(value) }

    @JvmField val PARTICLE: ParameterValueType<Void> = parameterValueType { ctx, buf, value -> TODO() }

    @JvmField val VILLAGER_DATA: ParameterValueType<VillagerData> = parameterValueType { buf, value ->
        buf.writeVarInt(VillagerTypeRegistry.getId(value.type))
        buf.writeVarInt(ProfessionTypeRegistry.getId(value.profession))
        buf.writeVarInt(value.level)
    }

    @JvmField val OPTIONAL_INT: ParameterValueType<Int?> = parameterValueType { buf, value ->
        if (value != null) {
            // What about -1? And Integer.MAX_VALUE?
            buf.writeVarInt(value + 1)
        } else {
            buf.writeVarInt(0)
        }
    }

    @JvmField val POSE: ParameterValueType<Pose> = parameterValueType { buf, value -> buf.writeVarInt(value.ordinal) }
}
