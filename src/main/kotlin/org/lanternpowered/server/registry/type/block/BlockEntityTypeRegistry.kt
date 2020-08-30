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
package org.lanternpowered.server.registry.type.block

import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.block.entity.BlockEntityCreationData
import org.lanternpowered.server.block.entity.LanternBlockEntityType
import org.lanternpowered.server.block.entity.vanilla.LanternBanner
import org.lanternpowered.server.block.entity.vanilla.LanternChest
import org.lanternpowered.server.block.entity.vanilla.LanternEnderChest
import org.lanternpowered.server.block.entity.vanilla.LanternFurnace
import org.lanternpowered.server.block.entity.vanilla.LanternJukebox
import org.lanternpowered.server.block.entity.vanilla.LanternShulkerBox
import org.lanternpowered.server.block.entity.vanilla.LanternSign
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.block.entity.BlockEntityType

val BlockEntityTypeRegistry = catalogTypeRegistry<BlockEntityType> {
    fun <T : BlockEntity> register(id: String, supplier: (creationData: BlockEntityCreationData) -> T) =
            register(LanternBlockEntityType(minecraftKey(id), supplier))

    register("banner", ::LanternBanner)
    register("chest", ::LanternChest)
    register("ender_chest", ::LanternEnderChest)
    register("furnace", ::LanternFurnace)
    register("jukebox", ::LanternJukebox)
    register("shulker_box", ::LanternShulkerBox)
    register("sign", ::LanternSign)
}
