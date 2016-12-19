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
package org.lanternpowered.server.network.entity.vanilla;

import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.parameter.ParameterList;

public class SnowmanEntityProtocol<E extends LanternEntity> extends InsentientEntityProtocol<E> {

    private boolean lastNoPumpkin;

    public SnowmanEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected int getMobType() {
        return 97;
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        parameterList.add(EntityParameters.Snowman.FLAGS, (byte) (this.entity.get(LanternKeys.HAS_PUMPKIN_HEAD).orElse(false) ? 0 : 0x10));
    }

    @Override
    protected void update(ParameterList parameterList) {
        final boolean noPumpkin = !this.entity.get(LanternKeys.HAS_PUMPKIN_HEAD).orElse(false);
        if (this.lastNoPumpkin != noPumpkin) {
            parameterList.add(EntityParameters.Snowman.FLAGS, (byte) (noPumpkin ? 0x10 : 0));
            this.lastNoPumpkin = noPumpkin;
        }
    }
}
