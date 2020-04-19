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

import static org.lanternpowered.server.network.vanilla.message.codec.play.CodecUtils.wrapAngle;

import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnObject;
import org.spongepowered.api.data.Keys;
import org.spongepowered.math.vector.Vector3d;

public abstract class ObjectEntityProtocol<E extends LanternEntity> extends EntityProtocol<E> {

    private int lastObjectData;

    public ObjectEntityProtocol(E entity) {
        super(entity);
    }

    protected abstract String getObjectType();

    protected abstract int getObjectData();

    @Override
    protected void spawn(EntityProtocolUpdateContext context) {
        final int entityId = this.getRootEntityId();

        final Vector3d rot = this.entity.getRotation();
        final Vector3d pos = this.entity.getPosition();
        final Vector3d vel = this.entity.get(Keys.VELOCITY).orElse(Vector3d.ZERO);

        double yaw = rot.getY();
        double pitch = rot.getX();

        context.sendToAllExceptSelf(() -> new MessagePlayOutSpawnObject(entityId, this.entity.getUniqueId(),
                NetworkIDs.REGISTRY.require(getObjectType()), getObjectData(), pos, wrapAngle(yaw), wrapAngle(pitch), vel));
        spawnWithMetadata(context);
    }

    @Override
    protected void update(EntityProtocolUpdateContext context) {
        final int objectData = this.getObjectData();
        if (this.lastObjectData != objectData) {
            this.spawn(context);
            super.update(EntityProtocolUpdateContext.empty());
            this.lastObjectData = objectData;
        } else {
            super.update(context);
        }
    }
}
