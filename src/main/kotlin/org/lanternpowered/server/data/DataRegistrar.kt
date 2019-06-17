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
package org.lanternpowered.server.data

import org.lanternpowered.api.ext.*
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.server.data.property.GlobalPropertyRegistry
import org.lanternpowered.server.game.Lantern
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.property.Properties
import org.spongepowered.api.data.property.Property
import org.spongepowered.api.data.type.BodyParts
import org.spongepowered.api.data.value.MapValue
import org.spongepowered.api.data.value.SetValue
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.util.Direction
import org.spongepowered.api.world.LightType
import org.spongepowered.api.world.LightTypes
import org.spongepowered.api.world.Location

object DataRegistrar {

    fun init() {
        registerPropertyStores()
        registerProviders()
    }

    private fun registerPropertyStores() {
        registerLuminancePropertyStore(Properties.BLOCK_LUMINANCE, LightTypes.BLOCK)
        registerLuminancePropertyStore(Properties.SKY_LUMINANCE, LightTypes.SKY)
        registerDominantHandPropertyStore()
        registerBlockTemperaturePropertyStore()
        registerTemperaturePropertyStore()
        registerFuelBurnTimePropertyStore()
    }

    private fun registerLuminancePropertyStore(property: Property<Double>, lightType: LightType) {
        GlobalPropertyRegistry.registerProvider(property) {
            forHolder<Location> {
                get {
                    val chunk = this.world.getChunk(this.chunkPosition)
                    chunk.getLight(lightType, this.blockX, this.blockY, this.blockZ).toDouble() / 15.0
                }
            }
        }
    }

    private fun registerBlockTemperaturePropertyStore() {
        GlobalPropertyRegistry.registerProvider(Properties.BLOCK_TEMPERATURE) {
            forHolder<Location> {
                get {
                    this.biome.temperature
                }
            }
        }
    }

    private fun registerTemperaturePropertyStore() {
        GlobalPropertyRegistry.registerProvider(Properties.TEMPERATURE) {
            get {
                getProperty(Properties.BLOCK_TEMPERATURE).orNull() ?:
                    getProperty(Properties.FLUID_TEMPERATURE).orNull() ?:
                    getProperty(Properties.BIOME_TEMPERATURE).orNull()
            }
        }
    }

    private fun registerDominantHandPropertyStore() {
        GlobalPropertyRegistry.registerProvider(Properties.DOMINANT_HAND) {
            forHolder<DataHolder> {
                get {
                    get(Keys.DOMINANT_HAND).orNull()
                }
            }
        }
    }

    private fun registerFuelBurnTimePropertyStore() {
        GlobalPropertyRegistry.registerProvider(Properties.FUEL_BURN_TIME) {
            forHolder<ItemStack> {
                get {
                    val result = Lantern.getRegistry().fuelRegistry.getResult(createSnapshot())
                    if (result.isPresent) result.asInt else null
                }
            }
        }
    }

    private fun registerProviders() {
        registerBigMushroomPoresProvider()
        registerConnectedDirectionsProvider()
        registerWireAttachmentsProvider()
        registerBodyRotationsProvider()
    }

    private fun registerDirectionBasedProvider(
            valueKey: Key<out SetValue<Direction>>, directionKeys: Map<Key<Value<Boolean>>, Direction>) {
        GlobalKeyRegistry.register(valueKey).addProvider {
            supportedBy {
                directionKeys.keys.any { supports(it) }
            }
            get {
                val directions = mutableSetOf<Direction>()
                for ((key, direction) in directionKeys) {
                    if (get(key).orElse(false)) {
                        directions.add(direction)
                    }
                }
                if (directions.isEmpty()) null else directions
            }
            offer { directions ->
                val builder = DataTransactionResult.builder()
                for ((key, direction) in directionKeys) {
                    builder.absorbResult(offer(key, directions.contains(direction)))
                }
                builder.result(DataTransactionResult.Type.SUCCESS).build()
            }
        }
    }

    private fun <K, V> registerMapBasedProvider(
            valueKey: Key<out MapValue<K, V>>, mappedTypes: Map<Key<Value<V>>, K>) {
        GlobalKeyRegistry.register(valueKey).addProvider {
            supportedBy {
                mappedTypes.keys.any { supports(it) }
            }
            get {
                val map = mutableMapOf<K, V>()
                for ((key, type) in mappedTypes) {
                    get(key).ifPresent { value ->
                        map[type] = value
                    }
                }
                if (map.isEmpty()) null else map
            }
            offer { map ->
                val builder = DataTransactionResult.builder()
                for ((key, type) in mappedTypes) {
                    val value = map[type]
                    if (value != null) {
                        builder.absorbResult(offer(key, value))
                    }
                }
                builder.result(DataTransactionResult.Type.SUCCESS).build()
            }
        }
    }

    private fun registerBigMushroomPoresProvider() {
        val directionKeys = mapOf(
                Keys.BIG_MUSHROOM_PORES_UP to Direction.UP,
                Keys.BIG_MUSHROOM_PORES_WEST to Direction.WEST,
                Keys.BIG_MUSHROOM_PORES_SOUTH to Direction.SOUTH,
                Keys.BIG_MUSHROOM_PORES_NORTH to Direction.NORTH,
                Keys.BIG_MUSHROOM_PORES_EAST to Direction.EAST,
                Keys.BIG_MUSHROOM_PORES_DOWN to Direction.DOWN
        )
        registerDirectionBasedProvider(Keys.BIG_MUSHROOM_PORES, directionKeys)
    }

    private fun registerConnectedDirectionsProvider() {
        val directionKeys = mapOf(
                Keys.CONNECTED_WEST to Direction.WEST,
                Keys.CONNECTED_EAST to Direction.EAST,
                Keys.CONNECTED_NORTH to Direction.NORTH,
                Keys.CONNECTED_SOUTH to Direction.SOUTH
        )
        registerDirectionBasedProvider(Keys.CONNECTED_DIRECTIONS, directionKeys)
    }

    private fun registerWireAttachmentsProvider() {
        val directionKeys = mapOf(
                Keys.WIRE_ATTACHMENT_WEST to Direction.WEST,
                Keys.WIRE_ATTACHMENT_EAST to Direction.EAST,
                Keys.WIRE_ATTACHMENT_NORTH to Direction.NORTH,
                Keys.WIRE_ATTACHMENT_SOUTH to Direction.SOUTH
        )
        registerMapBasedProvider(Keys.WIRE_ATTACHMENTS, directionKeys)
    }

    private fun registerBodyRotationsProvider() {
        val bodyPartKeys = mapOf(
                Keys.RIGHT_ARM_ROTATION to BodyParts.RIGHT_ARM,
                Keys.RIGHT_LEG_ROTATION to BodyParts.RIGHT_LEG,
                Keys.LEFT_ARM_ROTATION to BodyParts.LEFT_ARM,
                Keys.LEFT_LEG_ROTATION to BodyParts.LEFT_LEG,
                Keys.HEAD_ROTATION to BodyParts.HEAD,
                Keys.CHEST_ROTATION to BodyParts.CHEST
        )
        registerMapBasedProvider(Keys.BODY_ROTATIONS, bodyPartKeys)
    }
}
