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

import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.inventory.InventorySnapshot
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes
import org.lanternpowered.server.inventory.vanilla.block.ChestInventory
import org.lanternpowered.server.network.block.BlockEntityProtocolTypes
import org.spongepowered.api.data.Keys
import org.spongepowered.api.effect.sound.SoundCategories
import org.spongepowered.api.effect.sound.SoundTypes
import org.spongepowered.api.world.Location
import kotlin.random.Random

class LanternShulkerBox : ContainerBlockEntity<ChestInventory>() {

    init {
        this.protocolType = BlockEntityProtocolTypes.DEFAULT

        keyRegistry {
            register(Keys.DISPLAY_NAME)
            registerProvider(LanternKeys.INVENTORY_SNAPSHOT) {
                offerFast { element ->
                    this.inventory.clear()
                    element.offerTo(this.inventory)
                    true
                }
                get {
                    InventorySnapshot.ofInventory(this.inventory)
                }
            }
        }
    }

    override fun createInventory(): ChestInventory {
        return VanillaInventoryArchetypes.SHULKER_BOX.builder().withCarrier(this).build(Lantern.getMinecraftPlugin())
    }

    override fun playOpenSound(location: Location) {
        location.world.playSound(SoundTypes.BLOCK_SHULKER_BOX_OPEN, SoundCategories.BLOCK,
                location.position.add(0.5, 0.5, 0.5), 0.5, Random.nextDouble() * 0.1 + 0.9)
    }

    override fun playCloseSound(location: Location) {
        location.world.playSound(SoundTypes.BLOCK_SHULKER_BOX_CLOSE, SoundCategories.BLOCK,
                location.position.add(0.5, 0.5, 0.5), 0.5, Random.nextDouble() * 0.1 + 0.9)
    }
}
