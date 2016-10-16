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

import static org.lanternpowered.server.network.vanilla.message.codec.play.CodecUtils.wrapAngle;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.type.LanternSkinPart;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.LanternLiving;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityHeadLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityVelocity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnPlayer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandPreference;
import org.spongepowered.api.data.type.HandPreferences;
import org.spongepowered.api.data.type.SkinPart;

import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

public abstract class HumanoidEntityProtocol<E extends LanternEntity> extends LivingEntityProtocol<E> {

    private HandPreference lastDominantHand = HandPreferences.RIGHT;
    @Nullable private Set<SkinPart> lastSkinParts;

    public HumanoidEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected void spawn(EntityProtocolUpdateContext context) {
        final int entityId = this.getRootEntityId();

        final Vector3d rot = this.entity.getRotation();
        final Vector3d headRot = this.entity instanceof LanternLiving ? ((LanternLiving) this.entity).getHeadRotation() : null;
        final Vector3d pos = this.entity.getPosition();
        final Vector3d vel = this.entity.getVelocity();

        final double yaw = rot.getY();
        final double pitch = headRot != null ? headRot.getX() : rot.getX();

        context.sendToAllExceptSelf(() -> new MessagePlayOutSpawnPlayer(entityId, this.entity.getUniqueId(),
                pos, wrapAngle(yaw), wrapAngle(pitch), this.fillParameters(true)));
        if (headRot != null) {
            context.sendToAllExceptSelf(() -> new MessagePlayOutEntityHeadLook(entityId, wrapAngle(headRot.getY())));
        }
        if (!vel.equals(Vector3d.ZERO)) {
            context.sendToAllExceptSelf(() -> new MessagePlayOutEntityVelocity(entityId, vel.getX(), vel.getY(), vel.getZ()));
        }
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        // Ignore the NoAI tag, isn't used on the client
        parameterList.add(EntityParameters.Humanoid.MAIN_HAND,
                (byte) (this.entity.get(Keys.DOMINANT_HAND).orElse(HandPreferences.RIGHT) == HandPreferences.RIGHT ? 1 : 0));
        final Set<SkinPart> skinParts = this.entity.get(LanternKeys.DISPLAYED_SKIN_PARTS).orElse(null);
        parameterList.add(EntityParameters.Humanoid.SKIN_PARTS,
                (byte) (skinParts == null ? 0xff : LanternSkinPart.toBitPattern(skinParts)));
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);
        final HandPreference dominantHand = this.entity.get(Keys.DOMINANT_HAND).orElse(HandPreferences.RIGHT);
        if (dominantHand != this.lastDominantHand) {
            parameterList.add(EntityParameters.Humanoid.MAIN_HAND, (byte) (dominantHand == HandPreferences.RIGHT ? 1 : 0));
            this.lastDominantHand = dominantHand;
        }
        final Set<SkinPart> skinParts = this.entity.get(LanternKeys.DISPLAYED_SKIN_PARTS).orElse(null);
        if (!Objects.equals(this.lastSkinParts, skinParts)) {
            parameterList.add(EntityParameters.Humanoid.SKIN_PARTS,
                    (byte) (skinParts == null ? 0xff : LanternSkinPart.toBitPattern(skinParts)));
            this.lastSkinParts = skinParts;
        }
    }
}
