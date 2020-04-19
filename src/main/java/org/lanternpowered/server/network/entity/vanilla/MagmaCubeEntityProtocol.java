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
package org.lanternpowered.server.network.entity.vanilla;

import org.lanternpowered.server.entity.LanternEntity;

public class MagmaCubeEntityProtocol<E extends LanternEntity> extends AbstractSlimeEntityProtocol<E> {

    public MagmaCubeEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected String getMobType() {
        return "minecraft:magma_cube";
    }
}
