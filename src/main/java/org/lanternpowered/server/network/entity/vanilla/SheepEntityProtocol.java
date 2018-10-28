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

import org.lanternpowered.server.data.type.LanternDyeColor;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;

public class SheepEntityProtocol<E extends LanternEntity> extends AnimalEntityProtocol<E> {

    private DyeColor lastColor;
    private boolean lastSheared;

    public SheepEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected String getMobType() {
        return "minecraft:sheep";
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        byte flags = (byte) ((LanternDyeColor) this.entity.get(Keys.DYE_COLOR).orElse(DyeColors.WHITE)).getInternalId();
        if (this.entity.get(Keys.IS_SHEARED).orElse(false)) {
            flags |= 0x10;
        }
        parameterList.add(EntityParameters.Sheep.FLAGS, flags);
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);
        final DyeColor color = this.entity.get(Keys.DYE_COLOR).orElse(DyeColors.WHITE);
        int flags = -1;
        if (this.lastColor != color) {
            flags = 15 - ((LanternDyeColor) color).getInternalId();
            this.lastColor = color;
        }
        final boolean sheared = this.entity.get(Keys.IS_SHEARED).orElse(false);
        if (this.lastSheared != sheared) {
            if (flags == -1) {
                flags = 15 - ((LanternDyeColor) color).getInternalId();
            }
            if (sheared) {
                flags |= 0x10;
            }
            this.lastSheared = sheared;
        } else if (flags != -1 && sheared) {
            flags |= 0x10;
        }
        if (flags != -1) {
            parameterList.add(EntityParameters.Sheep.FLAGS, (byte) flags);
        }
    }
}
