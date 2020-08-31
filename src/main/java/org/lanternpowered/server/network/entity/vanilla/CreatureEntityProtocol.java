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

import static org.lanternpowered.server.network.vanilla.packet.codec.play.CodecUtils.wrapAngle;

import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnMobPacket;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.Keys;
import org.spongepowered.math.vector.Vector3d;

public abstract class CreatureEntityProtocol<E extends LanternEntity> extends LivingEntityProtocol<E> {

    public CreatureEntityProtocol(E entity) {
        super(entity);
    }

    /**
     * Gets the mob type.
     *
     * @return The mob type
     */
    protected abstract String getMobType();

    @Override
    protected void spawn(EntityProtocolUpdateContext context) {
        final Vector3d rot = this.entity.getRotation();
        final Vector3d headRot = this.entity.get(Keys.HEAD_ROTATION).orElse(null);
        final Vector3d pos = this.entity.getPosition();
        final Vector3d vel = this.entity.get(Keys.VELOCITY).orElse(Vector3d.ZERO);

        final double yaw = rot.getY();
        final double pitch = headRot != null ? headRot.getX() : rot.getX();
        final double headYaw = headRot != null ? headRot.getY() : 0;

        final int entityTypeId = EntityNetworkIDs.REGISTRY.require(ResourceKey.resolve(getMobType()));

        context.sendToAllExceptSelf(() -> new SpawnMobPacket(getRootEntityId(), this.entity.getUniqueId(), entityTypeId,
                pos, wrapAngle(yaw), wrapAngle(pitch), wrapAngle(headYaw), vel));
        spawnWithMetadata(context);
        spawnWithEquipment(context);
    }
}
