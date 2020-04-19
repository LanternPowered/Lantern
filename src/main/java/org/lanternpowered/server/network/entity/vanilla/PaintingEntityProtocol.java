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
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityTeleport;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnPainting;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.Art;
import org.spongepowered.api.data.type.Arts;
import org.spongepowered.api.util.Direction;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import org.checkerframework.checker.nullness.qual.Nullable;

public class PaintingEntityProtocol<E extends LanternEntity> extends EntityProtocol<E> {

    @Nullable private Art lastArt;
    @Nullable private Direction lastDirection;

    private Vector3i lastBlockPos;

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
        spawn(context, getArt(), getDirection(), this.entity.getPosition().toInt());
    }

    private void spawn(EntityProtocolUpdateContext context, Art art, Direction direction, Vector3i position) {
        context.sendToAll(() -> new MessagePlayOutSpawnPainting(getRootEntityId(),
                this.entity.getUniqueId(), art, position.getX(), position.getY(), position.getZ(), direction));
    }

    @Override
    public void update(EntityProtocolUpdateContext context) {
        final Art art = getArt();
        final Direction direction = getDirection();
        final Vector3d pos = this.entity.getPosition();
        final Vector3i blockPos = pos.toInt();

        if (art != this.lastArt || direction != this.lastDirection) {
            spawn(context, art, direction, blockPos);
            update0(EntityProtocolUpdateContext.empty());
            this.lastDirection = direction;
            this.lastBlockPos = blockPos;
            this.lastArt = art;
        } else if (!blockPos.equals(this.lastBlockPos)) {
            update0(context);
            context.sendToAll(() -> new MessagePlayOutEntityTeleport(getRootEntityId(), pos, (byte) 0, (byte) 0, true));
            this.lastBlockPos = blockPos;
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
