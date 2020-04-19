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

import org.lanternpowered.server.data.type.LanternDyeColor;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.spongepowered.api.data.Keys;
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
