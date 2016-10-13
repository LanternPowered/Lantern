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

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityTeleport;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnPainting;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.Art;
import org.spongepowered.api.data.type.Arts;
import org.spongepowered.api.util.Direction;

import javax.annotation.Nullable;

public class PaintingEntityProtocol<E extends LanternEntity> extends EntityProtocol<E> {

    @Nullable private Art lastArt;
    @Nullable private Direction lastDirection;

    private int lastX;
    private int lastY;
    private int lastZ;

    public PaintingEntityProtocol(E entity) {
        super(entity);
    }

    private Direction getDirection() {
        final Direction direction = this.entity.get(Keys.DIRECTION).orElse(Direction.SOUTH);
        // We can only support cardinal directions, up and down are also
        // not supported but they will also default to facing south
        if (!direction.isCardinal()) {
            return Direction.getClosest(direction.asOffset(), Direction.Division.CARDINAL);
        }
        return direction;
    }

    private Art getArt() {
        return this.entity.get(Keys.ART).orElse(Arts.KEBAB);
    }

    @Override
    protected void spawn(EntityProtocolUpdateContext context) {
        this.spawn(context, this.getArt(), this.getDirection(), this.entity.getPosition().toInt());
    }

    private void spawn(EntityProtocolUpdateContext context, Art art, Direction direction, Vector3i position) {
        context.sendToAll(() -> new MessagePlayOutSpawnPainting(this.getRootEntityId(),
                this.entity.getUniqueId(), art, position.getX(), position.getY(), position.getZ(), direction));
    }

    @Override
    public void update(EntityProtocolUpdateContext context) {
        final Art art = this.getArt();
        final Direction direction = this.getDirection();
        final Vector3i position = this.entity.getPosition().toInt();
        final int x = position.getX();
        final int y = position.getY();
        final int z = position.getZ();

        if (art != this.lastArt || direction != this.lastDirection) {
            this.spawn(context, art, direction, position);
            this.update0(EntityProtocolUpdateContext.empty());
            this.lastDirection = direction;
            this.lastArt = art;
            this.lastX = x;
            this.lastY = y;
            this.lastZ = z;
        } else if (x != this.lastX || y != this.lastY || z != this.lastZ) {
            this.update0(context);
            context.sendToAll(() -> new MessagePlayOutEntityTeleport(this.getRootEntityId(), x, y, z, 0, 0, true));
            this.lastX = x;
            this.lastY = y;
            this.lastZ = z;
        }
    }

    protected void update0(EntityProtocolUpdateContext context) {
    }

    @Override
    public void spawn(ParameterList parameterList) {
    }

    @Override
    public void update(ParameterList parameterList) {
    }
}
