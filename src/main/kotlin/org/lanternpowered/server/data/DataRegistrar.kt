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
package org.lanternpowered.server.data

import org.lanternpowered.api.data.Keys
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.type.BodyParts
import org.spongepowered.api.data.value.MapValue
import org.spongepowered.api.data.value.SetValue
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.util.Direction

object DataRegistrar {

    fun init() {
        registerPropertyStores()
        registerProviders()
    }

    private fun registerPropertyStores() {
        // TODO: Fix these
        //registerLuminancePropertyStore(Properties.BLOCK_LUMINANCE, LightTypes.BLOCK)
        //registerLuminancePropertyStore(Properties.SKY_LUMINANCE, LightTypes.SKY)
        //registerDominantHandPropertyStore()
        //registerBlockTemperaturePropertyStore()
        //registerTemperaturePropertyStore()
        //registerFuelBurnTimePropertyStore()
    }

    /*
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
    */

    /*
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
    }*/

    private fun registerProviders() {
        registerBigMushroomPoresProvider()
        registerConnectedDirectionsProvider()
        registerWireAttachmentsProvider()
        registerBodyRotationsProvider()
    }

    private fun registerDirectionBasedProvider(
            valueKey: Key<SetValue<Direction>>, directionKeys: Map<Key<Value<Boolean>>, Direction>) {
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
            valueKey: Key<MapValue<K, V>>, mappedTypes: Map<Key<Value<V>>, K>) {
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
                Keys.HAS_PORES_UP to Direction.UP,
                Keys.HAS_PORES_WEST to Direction.WEST,
                Keys.HAS_PORES_SOUTH to Direction.SOUTH,
                Keys.HAS_PORES_NORTH to Direction.NORTH,
                Keys.HAS_PORES_EAST to Direction.EAST,
                Keys.HAS_PORES_DOWN to Direction.DOWN
        )
        registerDirectionBasedProvider(Keys.PORES, directionKeys)
    }

    private fun registerConnectedDirectionsProvider() {
        val directionKeys = mapOf(
                Keys.IS_CONNECTED_WEST to Direction.WEST,
                Keys.IS_CONNECTED_EAST to Direction.EAST,
                Keys.IS_CONNECTED_NORTH to Direction.NORTH,
                Keys.IS_CONNECTED_SOUTH to Direction.SOUTH
        )
        registerDirectionBasedProvider(Keys.CONNECTED_DIRECTIONS, directionKeys)
    }

    private fun registerWireAttachmentsProvider() {
        val directionKeys = mapOf(
                Keys.WIRE_ATTACHMENT_WEST to Direction.WEST,
                Keys.WIRE_ATTACHMENT_EAST to Direction.EAST,
                Keys.WIRE_ATTACHMENT_NORTH to Direction.NORTH,
                Keys.WIRE_ATTACHMENT_SOUTH to Direction.SOUTH
        ).entries.associate { (key, value) -> key to value }
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
