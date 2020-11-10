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
package org.lanternpowered.server.network.vanilla.packet.processor.play

import com.github.benmanes.caffeine.cache.Caffeine
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.server.effect.particle.LanternParticleEffect
import org.lanternpowered.server.inventory.LanternItemStack
import org.lanternpowered.server.network.entity.EntityProtocolManager
import org.lanternpowered.server.network.entity.parameter.MutableParameterList
import org.lanternpowered.server.network.entity.vanilla.EntityParameters
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.packet.PacketProcessor
import org.lanternpowered.server.network.vanilla.packet.type.play.DestroyEntitiesPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EffectPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityMetadataPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityStatusPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ParticleEffectPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnObjectPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnParticlePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnParticlePacket.DustData
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnParticlePacket.ItemData
import org.lanternpowered.server.registry.type.block.BlockStateRegistry
import org.lanternpowered.server.registry.type.data.NotePitchRegistry
import org.spongepowered.api.block.BlockState
import org.lanternpowered.api.data.Keys
import org.spongepowered.api.data.type.NotePitch
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.particle.ParticleOptions
import org.spongepowered.api.effect.particle.ParticleTypes
import org.spongepowered.api.effect.potion.PotionEffectType
import org.spongepowered.api.effect.potion.PotionEffectTypes
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.util.Color
import org.spongepowered.api.util.Direction
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3f
import java.util.Optional
import java.util.Random
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

object ParticleEffectProcessor : PacketProcessor<ParticleEffectPacket> {

    /**
     * Using a cache to bring the amount of operations down for spawning particles.
     */
    private val cache = Caffeine.newBuilder()
            .weakKeys().expireAfterAccess(3, TimeUnit.MINUTES)
            .build { effect: ParticleEffect -> preProcess(effect) }

    private val potionEffectTypeToId: Object2IntMap<PotionEffectType> = Object2IntOpenHashMap()

    init {
        this.potionEffectTypeToId.defaultReturnValue(0) // Default to water?
        this.potionEffectTypeToId[PotionEffectTypes.NIGHT_VISION.get()] = 5
        this.potionEffectTypeToId[PotionEffectTypes.INVISIBILITY.get()] = 7
        this.potionEffectTypeToId[PotionEffectTypes.JUMP_BOOST.get()] = 9
        this.potionEffectTypeToId[PotionEffectTypes.FIRE_RESISTANCE.get()] = 12
        this.potionEffectTypeToId[PotionEffectTypes.SPEED.get()] = 14
        this.potionEffectTypeToId[PotionEffectTypes.SLOWNESS.get()] = 17
        this.potionEffectTypeToId[PotionEffectTypes.WATER_BREATHING.get()] = 19
        this.potionEffectTypeToId[PotionEffectTypes.INSTANT_HEALTH.get()] = 21
        this.potionEffectTypeToId[PotionEffectTypes.INSTANT_DAMAGE.get()] = 23
        this.potionEffectTypeToId[PotionEffectTypes.POISON.get()] = 25
        this.potionEffectTypeToId[PotionEffectTypes.REGENERATION.get()] = 28
        this.potionEffectTypeToId[PotionEffectTypes.STRENGTH.get()] = 31
        this.potionEffectTypeToId[PotionEffectTypes.WEAKNESS.get()] = 34
        this.potionEffectTypeToId[PotionEffectTypes.LUCK.get()] = 36
    }

    private fun preProcess(effect: ParticleEffect): ICachedMessage {
        effect as LanternParticleEffect

        val type = effect.type
        val internalType = type.internalType

        // Special cases
        if (internalType == null) {
            when (type) {
                ParticleTypes.FIREWORKS.get() -> {
                    // Create the fireworks data item
                    val itemStack = LanternItemStack(ItemTypes.FIREWORK_ROCKET.get())
                    itemStack.tryOffer(Keys.FIREWORK_EFFECTS, effect.getOptionOrDefault(ParticleOptions.FIREWORK_EFFECTS).get())

                    // Write the item to a parameter list
                    val parameterList = MutableParameterList()
                    parameterList.add(EntityParameters.Fireworks.ITEM, itemStack)
                    return CachedFireworksMessage(EntityMetadataPacket(CachedFireworksMessage.ENTITY_ID, parameterList))
                }
                ParticleTypes.FERTILIZER.get() -> {
                    val quantity = effect.getOptionOrDefault(ParticleOptions.QUANTITY).get()
                    return CachedEffectMessage(2005, quantity, false)
                }
                ParticleTypes.BREAK_SPLASH_POTION.get() -> {
                    val potionId = potionEffectTypeToId.getInt(effect.getOptionOrDefault(ParticleOptions.POTION_EFFECT_TYPE).get())
                    return CachedEffectMessage(2002, potionId, false)
                }
                ParticleTypes.BREAK_BLOCK.get() -> {
                    val state = getBlockState(effect, type.getDefaultOption(ParticleOptions.BLOCK_STATE))
                    return if (state == 0) {
                        EmptyCachedMessage
                    } else CachedEffectMessage(2001, state, false)
                }
                ParticleTypes.MOBSPAWNER_FLAMES.get() -> {
                    return CachedEffectMessage(2004, 0, false)
                }
                ParticleTypes.BREAK_EYE_OF_ENDER.get() -> {
                    return CachedEffectMessage(2003, 0, false)
                }
                ParticleTypes.DRAGON_BREATH_ATTACK.get() -> {
                    return CachedEffectMessage(2006, 0, false)
                }
                ParticleTypes.FIRE_SMOKE.get() -> {
                    val direction = effect.getOptionOrDefault(ParticleOptions.DIRECTION).get()
                    return CachedEffectMessage(2000, getDirectionData(direction), false)
                }
                else -> return EmptyCachedMessage
            }
        }

        val internalId: Int = internalType
        val offset = effect.getOption(ParticleOptions.OFFSET).map { obj: Vector3d -> obj.toFloat() }.orElse(Vector3f.ZERO)
        val quantity = effect.getOption(ParticleOptions.QUANTITY).orElse(1)
        var extra: SpawnParticlePacket.Data? = null

        // The extra values, normal behavior offsetX, offsetY, offsetZ
        var f0 = 0.0
        var f1 = 0.0
        var f2 = 0.0

        // Depends on behavior
        // Note: If the count > 0 -> speed = 0f else if count = 0 -> speed = 1f
        var defaultBlockState = emptyOptional<BlockState>()
        if (type != ParticleTypes.ITEM.get() && type.getDefaultOption(ParticleOptions.BLOCK_STATE).also { defaultBlockState = it }.isPresent) {
            val state = getBlockState(effect, defaultBlockState)
            if (state == 0) {
                return EmptyCachedMessage
            }
            extra = SpawnParticlePacket.BlockData(state)
        }
        var defaultItemStackSnapshot = emptyOptional<ItemStackSnapshot>()
        if (extra == null && type.getDefaultOption(ParticleOptions.ITEM_STACK_SNAPSHOT).also { defaultItemStackSnapshot = it }.isPresent) {
            val optItemStackSnapshot = effect.getOption(ParticleOptions.ITEM_STACK_SNAPSHOT)
            val item: ItemStack
            item = if (optItemStackSnapshot.isPresent) {
                optItemStackSnapshot.get().createStack()
            } else {
                val optBlockState = effect.getOption(ParticleOptions.BLOCK_STATE)
                if (optBlockState.isPresent) {
                    val blockState = optBlockState.get()
                    val optItemType = blockState.type.item
                    if (optItemType.isPresent) {
                        ItemStack.of(optItemType.get(), 1)
                    } else {
                        return EmptyCachedMessage
                    }
                } else {
                    defaultItemStackSnapshot.get().createStack()
                }
            }
            extra = ItemData(item)
        }
        val defaultScale = type.getDefaultOption(ParticleOptions.SCALE)
        var defaultColor = emptyOptional<Color>()
        var defaultNote: Optional<NotePitch>
        var defaultVelocity: Optional<Vector3d>
        if (type == ParticleTypes.DUST.get()) {
            defaultColor = type.getDefaultOption(ParticleOptions.COLOR)

            // The following options must be present for dust
            val scale = effect.getOption(ParticleOptions.SCALE).orElse(defaultScale.get())
            val color = effect.getOption(ParticleOptions.COLOR).orElse(defaultColor.get())
            val r = color.red.toFloat() / 255f
            val g = color.green.toFloat() / 255f
            val b = color.blue.toFloat() / 255f
            extra = DustData(r, g, b, scale.toFloat())
        } else if (defaultScale.isPresent) {
            var scale = effect.getOption(ParticleOptions.SCALE).orElse(defaultScale.get())

            // The formula of the large explosion acts strange
            // Client formula: sizeClient = 1 - sizeServer * 0.5
            // The particle effect returns the client value so
            // Server formula: sizeServer = (-sizeClient * 2) + 2
            if (type == ParticleTypes.EXPLOSION.get() || type == ParticleTypes.SWEEP_ATTACK.get()) {
                scale = -scale * 2f + 2f
            }
            if (scale == 0.0) {
                return CachedParticleMessage(internalId, offset, quantity, extra)
            }
            f0 = scale
        } else if (type != ParticleTypes.DUST.get() && type.getDefaultOption(ParticleOptions.COLOR).also { defaultColor = it }.isPresent) {
            val isSpell = type == ParticleTypes.ENTITY_EFFECT.get() || type == ParticleTypes.AMBIENT_ENTITY_EFFECT.get()
            var color = effect.getOption(ParticleOptions.COLOR).orElse(null)
            if (!isSpell && (color == null || color == defaultColor.get())) {
                return CachedParticleMessage(internalId, offset, quantity, extra)
            } else if (isSpell && color == null) {
                color = defaultColor.get()
            }
            f0 = color!!.red / 255f.toDouble()
            f1 = color.green / 255f.toDouble()
            f2 = color.blue / 255f.toDouble()

            // Make sure that the x and z component are never 0 for these effects,
            // they would trigger the slow horizontal velocity (unsupported on the server),
            // but we already chose for the color, can't have both
            if (isSpell) {
                f0 = Math.max(f0, 0.001)
                f2 = Math.max(f0, 0.001)
            }
        } else if (type.getDefaultOption(ParticleOptions.NOTE).also { defaultNote = it }.isPresent) {
            val notePitch = effect.getOption(ParticleOptions.NOTE).orElse(defaultNote.get())
            val note = NotePitchRegistry.getId(notePitch).toFloat()
            if (note == 0f) {
                return CachedParticleMessage(internalId, offset, quantity, extra)
            }
            f0 = note / 24f.toDouble()
        }
        if (type.getDefaultOption(ParticleOptions.VELOCITY).also { defaultVelocity = it }.isPresent) {
            val velocity = effect.getOption(ParticleOptions.VELOCITY).orElse(defaultVelocity.get())
            f0 = velocity.x
            f1 = velocity.y
            f2 = velocity.z
            val slowHorizontalVelocity = type.getDefaultOption(ParticleOptions.SLOW_HORIZONTAL_VELOCITY)
            if (slowHorizontalVelocity.isPresent &&
                    effect.getOption(ParticleOptions.SLOW_HORIZONTAL_VELOCITY).orElse(slowHorizontalVelocity.get())) {
                f0 = 0.0
                f2 = 0.0
            }

            // The y value won't work for this effect, if the value isn't 0 the velocity won't work
            if (type == ParticleTypes.RAIN_SPLASH.get()) {
                f1 = 0.0
            }
            if (f0 == 0.0 && f1 == 0.0 && f2 == 0.0) {
                return CachedParticleMessage(internalId, offset, quantity, extra)
            }
        }

        // Is this check necessary?
        return if (f0 == 0.0 && f1 == 0.0 && f2 == 0.0) {
            CachedParticleMessage(internalId, offset, quantity, extra)
        } else CachedOffsetParticleMessage(internalId, Vector3f(f0, f1, f2), offset, quantity, extra)
    }

    override fun process(context: CodecContext, packet: ParticleEffectPacket, output: MutableList<Packet>) {
        val cached = this.cache[packet.particleEffect]!!
        cached.process(packet.position, output)
    }

    private object EmptyCachedMessage : ICachedMessage {
        override fun process(position: Vector3d, output: MutableList<Packet>) {}
    }

    private class CachedFireworksMessage(private val entityMetadataMessage: EntityMetadataPacket) : ICachedMessage {

        companion object {

            // Get the next free entity id
            var ENTITY_ID = 0
            private var UNIQUE_ID: UUID
            private var DESTROY_ENTITY: DestroyEntitiesPacket
            private var TRIGGER_EFFECT: EntityStatusPacket

            init {
                ENTITY_ID = EntityProtocolManager.acquireEntityId()
                UNIQUE_ID = UUID.randomUUID()
                DESTROY_ENTITY = DestroyEntitiesPacket(ENTITY_ID)
                // The status index that is used to trigger the fireworks effect
                TRIGGER_EFFECT = EntityStatusPacket(ENTITY_ID, 17)
            }
        }

        override fun process(position: Vector3d, output: MutableList<Packet>) {
            // 76 -> The internal id used to spawn fireworks
            output.add(SpawnObjectPacket(ENTITY_ID, UNIQUE_ID, 76, 0, position, 0, 0, Vector3d.ZERO))
            output.add(this.entityMetadataMessage)
            output.add(TRIGGER_EFFECT)
            output.add(DESTROY_ENTITY)
        }
    }

    private class CachedParticleMessage(
            private val particleId: Int,
            private val offsetData: Vector3f,
            private val count: Int,
            private val extra: SpawnParticlePacket.Data?
    ) : ICachedMessage {

        override fun process(position: Vector3d, output: MutableList<Packet>) {
            output.add(SpawnParticlePacket(this.particleId, position, this.offsetData, 0f, this.count, this.extra))
        }
    }

    private class CachedOffsetParticleMessage(
            private val particleId: Int,
            private val offsetData: Vector3f,
            private val offset: Vector3f,
            private val count: Int,
            private val extra: SpawnParticlePacket.Data?
    ) : ICachedMessage {

        override fun process(position: Vector3d, output: MutableList<Packet>) {
            val random: Random = ThreadLocalRandom.current()
            if (this.offset == Vector3f.ZERO) {
                val message = SpawnParticlePacket(this.particleId, position, this.offsetData, 1f, 0, this.extra)
                for (i in 0 until this.count) {
                    output.add(message)
                }
            } else {
                val px = position.x.toFloat()
                val py = position.y.toFloat()
                val pz = position.z.toFloat()
                val ox = this.offset.x
                val oy = this.offset.y
                val oz = this.offset.z
                for (i in 0 until this.count) {
                    val px0 = px + (random.nextFloat() * 2f - 1f) * ox.toDouble()
                    val py0 = py + (random.nextFloat() * 2f - 1f) * oy.toDouble()
                    val pz0 = pz + (random.nextFloat() * 2f - 1f) * oz.toDouble()
                    output.add(SpawnParticlePacket(this.particleId, Vector3d(px0, py0, pz0),
                            this.offsetData, 1f, 0, this.extra))
                }
            }
        }
    }

    private class CachedEffectMessage(
            private val type: Int,
            private val data: Int,
            private val broadcast: Boolean
    ) : ICachedMessage {

        override fun process(position: Vector3d, output: MutableList<Packet>) {
            output.add(EffectPacket(position.round().toInt(), type, data, broadcast))
        }
    }

    private interface ICachedMessage {
        fun process(position: Vector3d, output: MutableList<Packet>)
    }

    private fun getBlockState(effect: LanternParticleEffect, defaultBlockState: Optional<BlockState>): Int {
        val blockState = effect.getOption(ParticleOptions.BLOCK_STATE)
        return if (blockState.isPresent) {
            BlockStateRegistry.getId(blockState.get())
        } else {
            val optSnapshot = effect.getOption(ParticleOptions.ITEM_STACK_SNAPSHOT)
            if (optSnapshot.isPresent) {
                val snapshot = optSnapshot.get()
                val blockType = snapshot.type.block
                if (blockType.isPresent) {
                    val state: BlockState
                    state = if (blockType.get().defaultState.stateProperties.isEmpty()) {
                        blockType.get().defaultState
                    } else {
                        val builder = BlockState.builder().blockType(blockType.get())
                        for (value in snapshot.values)
                            builder.add(value)
                        builder.build()
                    }
                    BlockStateRegistry.getId(state)
                } else {
                    0
                }
            } else {
                BlockStateRegistry.getId(defaultBlockState.get())
            }
        }
    }

    private fun getDirectionData(direction: Direction): Int {
        @Suppress("NAME_SHADOWING")
        var direction = direction
        if (direction.isSecondaryOrdinal) {
            direction = Direction.getClosest(direction.asOffset(), Direction.Division.ORDINAL)
        }
        return when (direction) {
            Direction.SOUTHEAST -> 0
            Direction.SOUTH -> 1
            Direction.SOUTHWEST -> 2
            Direction.EAST -> 3
            Direction.WEST -> 5
            Direction.NORTHEAST -> 6
            Direction.NORTH -> 7
            Direction.NORTHWEST -> 8
            else -> 4
        }
    }
}
