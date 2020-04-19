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
package org.lanternpowered.server.block;

import org.lanternpowered.server.block.provider.BlockObjectProvider;
import org.spongepowered.api.block.entity.BlockEntity;

import java.util.function.Supplier;

@FunctionalInterface
public interface BlockEntityProvider extends BlockObjectProvider<BlockEntity> {

    static BlockEntityProvider of(Supplier<BlockEntity> supplier) {
        return (blockState, location, face) -> supplier.get();
    }
}
