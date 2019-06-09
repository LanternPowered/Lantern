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
    }

    public override fun registerKeys() {
        super.registerKeys()

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
