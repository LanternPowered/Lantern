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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.type.LanternSkinPart;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityHeadLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityVelocity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnPlayer;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandPreference;
import org.spongepowered.api.data.type.HandPreferences;
import org.spongepowered.api.data.type.SkinPart;
import org.spongepowered.api.text.Text;
import org.spongepowered.math.vector.Vector3d;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class HumanoidEntityProtocol<E extends LanternEntity> extends LivingEntityProtocol<E> {

    private HandPreference lastDominantHand = HandPreferences.RIGHT;
    @Nullable private Set<SkinPart> lastSkinParts;

    public HumanoidEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected void spawn(EntityProtocolUpdateContext context) {
        final int entityId = getRootEntityId();

        final Vector3d rot = this.entity.getRotation();
        final Vector3d headRot = this.entity.get(Keys.HEAD_ROTATION).orElse(null);
        final Vector3d pos = this.entity.getPosition();
        final Vector3d vel = this.entity.get(Keys.VELOCITY).orElse(Vector3d.ZERO);

        final double yaw = rot.getY();
        final double pitch = headRot != null ? headRot.getX() : rot.getX();

        context.sendToAllExceptSelf(() -> new MessagePlayOutSpawnPlayer(entityId, this.entity.getUniqueId(),
                pos, wrapAngle(yaw), wrapAngle(pitch)));
        if (headRot != null) {
            context.sendToAllExceptSelf(() -> new MessagePlayOutEntityHeadLook(entityId, wrapAngle(headRot.getY())));
        }
        if (!vel.equals(Vector3d.ZERO)) {
            context.sendToAllExceptSelf(() -> new MessagePlayOutEntityVelocity(entityId, vel.getX(), vel.getY(), vel.getZ()));
        }
        spawnWithMetadata(context);
        spawnWithEquipment(context);
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

    @Override
    protected boolean hasEquipment() {
        return true;
    }

    @Override
    boolean isCustomNameVisible() {
        // The display doesn't have to be updated through
        // the parameters for humanoids
        return false;
    }

    @Override
    Optional<Text> getCustomName() {
        // The display doesn't have to be updated through
        // the parameters for humanoids
        return Optional.empty();
    }
}
