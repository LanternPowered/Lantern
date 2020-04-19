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
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.spongepowered.api.data.Keys;

public abstract class AbstractSlimeEntityProtocol<E extends LanternEntity> extends InsentientEntityProtocol<E> {

    private int lastSize;

    public AbstractSlimeEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        parameterList.add(EntityParameters.AbstractSlime.SIZE, this.entity.get(Keys.SLIME_SIZE).orElse(1));
    }

    @Override
    protected void update(ParameterList parameterList) {
        final int size = this.entity.get(Keys.SLIME_SIZE).orElse(1);
        if (this.lastSize != size) {
            parameterList.add(EntityParameters.AbstractSlime.SIZE, size);
            this.lastSize = size;
        }
    }
}
