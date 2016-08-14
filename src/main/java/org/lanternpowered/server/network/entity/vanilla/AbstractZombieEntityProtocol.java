/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
import org.lanternpowered.server.entity.LanternEntityLiving;
import org.lanternpowered.server.network.entity.parameter.ParameterList;

public abstract class AbstractZombieEntityProtocol<E extends LanternEntityLiving> extends AgeableEntityProtocol<E> {

    private boolean lastAreHandsUp;

    public AbstractZombieEntityProtocol(E entity) {
        super(entity);
    }

    private boolean areHandsUp() {
        return this.entity.get(LanternKeys.ARE_HANDS_UP).orElse(false);
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        parameterList.add(EntityParameters.AbstractZombie.UNUSED, 0);
        parameterList.add(EntityParameters.AbstractZombie.HANDS_UP, this.areHandsUp());
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);
        final boolean handsUp = this.areHandsUp();
        if (handsUp != this.lastAreHandsUp) {
            parameterList.add(EntityParameters.AbstractZombie.HANDS_UP, handsUp);
            this.lastAreHandsUp = handsUp;
        }
    }
}
