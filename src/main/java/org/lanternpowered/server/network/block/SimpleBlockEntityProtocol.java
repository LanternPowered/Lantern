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
package org.lanternpowered.server.network.block;

import org.lanternpowered.server.block.entity.LanternBlockEntity;

public class SimpleBlockEntityProtocol<T extends LanternBlockEntity> extends BlockEntityProtocol<T> {

    /**
     * Constructs a new {@link AbstractBlockEntityProtocol} object.
     *
     * @param blockEntity The block entity
     */
    protected SimpleBlockEntityProtocol(T blockEntity) {
        super(blockEntity);
    }

    @Override
    protected String getType() {
        return this.blockEntity.getType().getKey().getValue();
    }
}
