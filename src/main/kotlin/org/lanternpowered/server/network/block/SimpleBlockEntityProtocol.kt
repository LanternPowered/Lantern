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
package org.lanternpowered.server.network.block

import org.spongepowered.api.block.entity.BlockEntity

class SimpleBlockEntityProtocol<T : BlockEntity>(blockEntity: T) : VanillaBlockEntityProtocol<T>(blockEntity) {

    override val type: String
        get() = this.blockEntity.type.key.formatted
}
