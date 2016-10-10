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

import static org.lanternpowered.server.network.vanilla.message.codec.play.CodecUtils.wrapAngle;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.ByteBufferAllocator;
import org.lanternpowered.server.network.entity.AbstractEntityProtocol;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.entity.parameter.ByteBufParameterList;
import org.lanternpowered.server.network.entity.parameter.EmptyParameterList;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityHeadLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityLookAndRelativeMove;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityMetadata;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityRelativeMove;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityTeleport;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityVelocity;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;

public abstract class EntityProtocol<E extends LanternEntity> extends AbstractEntityProtocol<E> {

    private double lastX;
    private double lastY;
    private double lastZ;

    private double lastYaw;
    private double lastPitch;

    private double lastHeadYaw;

    private double lastVelX;
    private double lastVelY;
    private double lastVelZ;

    private byte lastFlags;

    public EntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected void destroy(EntityProtocolUpdateContext context) {
        context.sendToAllExceptSelf(new MessagePlayOutDestroyEntities(new int[] { this.entity.getEntityId() }));
    }

    @Override
    public void update(EntityProtocolUpdateContext context) {
        final Vector3d rot = this.entity.getRotation();
        final Vector3d headRot = this.entity instanceof Living ? ((Living) this.entity).getHeadRotation() : null;
        final Vector3d pos = this.entity.getPosition();

        final double x = pos.getX();
        final double y = pos.getY();
        final double z = pos.getZ();

        final double yaw = rot.getY();
        // All living entities have a head rotation and changing the pitch
        // would only affect the head pitch.
        final double pitch = (headRot != null ? headRot : rot).getX();

        boolean dirtyPos = x != this.lastX || y != this.lastY || z != this.lastZ;
        boolean dirtyRot = yaw != this.lastYaw || z != this.lastPitch;

        // TODO: On ground state

        final int entityId = this.entity.getEntityId();

        if (dirtyPos) {
            double dx = x - this.lastX;
            double dy = y - this.lastY;
            double dz = z - this.lastZ;

            if (dirtyRot) {
                this.lastYaw = yaw;
                this.lastPitch = pitch;
            }
            if (Math.abs(dx) < 8 && Math.abs(dy) < 8 && Math.abs(dz) < 8) {
                int dxu = (int) (dx * 4096);
                int dyu = (int) (dy * 4096);
                int dzu = (int) (dz * 4096);

                if (dirtyRot) {
                    context.sendToAllExceptSelf(new MessagePlayOutEntityLookAndRelativeMove(entityId,
                            dxu, dyu, dzu, wrapAngle(yaw), wrapAngle(pitch), false));
                    // The rotation is already send
                    dirtyRot = false;
                } else {
                    context.sendToAllExceptSelf(new MessagePlayOutEntityRelativeMove(entityId,
                            dxu, dyu, dzu, false));
                }
            } else {
                context.sendToAllExceptSelf(new MessagePlayOutEntityTeleport(entityId,
                        x, y, z, wrapAngle(yaw), wrapAngle(pitch), false));
                // The rotation is already send
                dirtyRot = false;
            }
            this.lastX = x;
            this.lastY = y;
            this.lastZ = z;
        }
        if (dirtyRot) {
            context.sendToAllExceptSelf(() -> new MessagePlayOutEntityLook(entityId, wrapAngle(yaw), wrapAngle(pitch), false));
        }
        if (headRot != null) {
            double headYaw = headRot.getY();
            if (headYaw != this.lastHeadYaw) {
                context.sendToAllExceptSelf(() -> new MessagePlayOutEntityHeadLook(entityId, wrapAngle(headYaw)));
            }
            this.lastHeadYaw = yaw;
        }
        final Vector3d velocity = this.entity.getVelocity();
        final double vx = velocity.getX();
        final double vy = velocity.getY();
        final double vz = velocity.getZ();
        if (vx != this.lastVelX || vy != this.lastVelY || vz != this.lastVelZ) {
            context.sendToAll(() -> new MessagePlayOutEntityVelocity(entityId, vx, vy, vz));
            this.lastVelX = vx;
            this.lastVelY = vy;
            this.lastVelZ = vz;
        }
        final ParameterList parameterList = context == EntityProtocolUpdateContext.empty() ?
                this.fillParameters(false, EmptyParameterList.INSTANCE) : this.fillParameters(false);
        // There were parameters applied
        if (!parameterList.isEmpty()) {
            context.sendToAll(() -> new MessagePlayOutEntityMetadata(entityId, parameterList));
        }
        // TODO: Update attributes
    }

    /**
     * Fills a {@link ByteBuffer} with parameters to spawn or update the {@link Entity}.
     *
     * @param initial Whether the entity is being spawned, the byte buffer can be null if this is false
     * @return The byte buffer
     */
    ParameterList fillParameters(boolean initial) {
        return this.fillParameters(initial, new ByteBufParameterList(ByteBufferAllocator.unpooled()));
    }

    ParameterList fillParameters(boolean initial, ParameterList parameterList) {
        if (initial) {
            this.spawn(parameterList);
        } else {
            this.update(parameterList);
        }
        return parameterList;
    }

    protected boolean isCrouched() {
        return false;
    }

    protected boolean isUsingItem() {
        return false;
    }

    protected boolean isSprinting() {
        return false;
    }

    protected boolean isElytraFlying() {
        return false;
    }

    /**
     * Fills the {@link ParameterList} with parameters to spawn the {@link Entity} on
     * the client.
     *
     * @param parameterList The parameter list to fill
     */
    protected void spawn(ParameterList parameterList) {
        parameterList.add(EntityParameters.Base.FLAGS, this.packFlags());
        parameterList.add(EntityParameters.Base.AIR_LEVEL, this.getInitialAirLevel());
        parameterList.add(EntityParameters.Base.CUSTOM_NAME, this.entity.get(Keys.DISPLAY_NAME).map(LanternTexts::toLegacy).orElse(""));
        parameterList.add(EntityParameters.Base.CUSTOM_NAME_VISIBLE, this.entity.get(Keys.CUSTOM_NAME_VISIBLE).orElse(true));
        parameterList.add(EntityParameters.Base.IS_SILENT, this.entity.get(Keys.IS_SILENT).orElse(false));
        parameterList.add(EntityParameters.Base.NO_GRAVITY, this.hasNoGravity());
    }

    boolean hasNoGravity() {
        // Always disable gravity for regular entities, we will handle our own physics
        return true;
    }

    private byte packFlags() {
        byte flags = 0;
        if (this.entity.get(Keys.FIRE_TICKS).orElse(0) > 0) {
            flags |= 0x01;
        }
        if (this.isCrouched()) {
            flags |= 0x02;
        }
        if (this.isSprinting()) {
            flags |= 0x08;
        }
        if (this.isUsingItem()) {
            flags |= 0x10;
        }
        if (this.entity.get(Keys.INVISIBLE).orElse(false)) {
            flags |= 0x20;
        }
        if (this.entity.get(Keys.GLOWING).orElse(false)) {
            flags |= 0x40;
        }
        if (this.isElytraFlying()) {
            flags |= 0x80;
        }
        return flags;
    }

    /**
     * Fills the {@link ParameterList} with parameters to update the {@link Entity} on
     * the client.
     *
     * @param parameterList The parameter list to fill
     */
    protected void update(ParameterList parameterList) {
        final byte flags = this.packFlags();
        if (flags != this.lastFlags) {
            parameterList.add(EntityParameters.Base.FLAGS, flags);
            this.lastFlags = flags;
        }
    }

    /**
     * Gets the air level of the entity.
     *
     * The air is by default 100 because the entities don't even use the air level,
     * except for the players. This method can be overridden if needed.
     *
     * @return The air level
     */
    protected short getInitialAirLevel() {
        return 100;
    }
}
