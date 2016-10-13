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

import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnExperienceOrb;
import org.spongepowered.api.data.key.Keys;

public class ExperienceOrbEntityProtocol<E extends LanternEntity> extends EntityProtocol<E> {

    private int lastQuantity;

    public ExperienceOrbEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected void spawn(EntityProtocolUpdateContext context) {
        this.spawn(context, this.entity.get(Keys.HELD_EXPERIENCE).orElse(1));
    }

    private void spawn(EntityProtocolUpdateContext context, int quantity) {
        if (quantity == 0) {
            context.sendToAll(() -> new MessagePlayOutDestroyEntities(this.getRootEntityId()));
        } else {
            context.sendToAll(() -> new MessagePlayOutSpawnExperienceOrb(this.getRootEntityId(), quantity, this.entity.getPosition()));
        }
    }

    @Override
    protected void update(EntityProtocolUpdateContext context) {
        final int quantity = this.entity.get(Keys.HELD_EXPERIENCE).orElse(1);
        if (this.lastQuantity != quantity) {
            this.spawn(context, quantity);
            this.lastQuantity = quantity;
        } else {
            this.update0(context);
        }
    }

    protected void update0(EntityProtocolUpdateContext context) {
        super.update(context);
    }
}
