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
package org.lanternpowered.server.block.entity.vanilla

import org.lanternpowered.api.effect.sound.SoundCategory
import org.lanternpowered.api.effect.sound.SoundTypes
import org.lanternpowered.api.effect.sound.soundEffectOf
import org.lanternpowered.api.world.Location
import org.lanternpowered.server.block.entity.BlockEntityCreationData
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.inventory.InventorySnapshot
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes
import org.lanternpowered.server.inventory.vanilla.block.ChestInventory
import org.lanternpowered.server.network.block.BlockEntityProtocolTypes
import org.spongepowered.api.data.Keys
import kotlin.random.Random

class LanternShulkerBox(creationData: BlockEntityCreationData) : ContainerBlockEntity<ChestInventory>() {

    init {
        this.protocolType = BlockEntityProtocolTypes.DEFAULT

        keyRegistry {
            register(Keys.DISPLAY_NAME)
            registerProvider(LanternKeys.INVENTORY_SNAPSHOT) {
                set { element ->
                    this.inventory.clear()
                    element.offerTo(this.inventory)
                }
                get {
                    InventorySnapshot.ofInventory(this.inventory)
                }
            }
        }
    }

    override fun createInventory(): ChestInventory =
            VanillaInventoryArchetypes.SHULKER_BOX.builder().withCarrier(this).build(Lantern.getMinecraftPlugin())

    override fun playOpenSound(location: Location) {
        val effect = soundEffectOf(SoundTypes.BLOCK_SHULKER_BOX_OPEN,
                category = SoundCategory.BLOCK, volume = 0.5, pitch = Random.nextDouble() * 0.1 + 0.9)
        location.world.playSound(effect, location.position.add(0.5, 0.5, 0.5))
    }

    override fun playCloseSound(location: Location) {
        val effect = soundEffectOf(SoundTypes.BLOCK_SHULKER_BOX_CLOSE,
                category = SoundCategory.BLOCK, volume = 0.5, pitch = Random.nextDouble() * 0.1 + 0.9)
        location.world.playSound(effect, location.position.add(0.5, 0.5, 0.5))
    }
}
