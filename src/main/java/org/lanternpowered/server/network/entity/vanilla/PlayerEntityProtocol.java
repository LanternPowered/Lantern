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

import static org.lanternpowered.server.network.entity.EntityProtocolManager.INVALID_ENTITY_ID;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.ByteBufferAllocator;
import org.lanternpowered.server.network.entity.EntityProtocolInitContext;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.entity.parameter.ByteBufParameterList;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityMetadata;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetGameMode;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnObject;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.ItemTypes;

import java.util.UUID;

public class PlayerEntityProtocol extends HumanoidEntityProtocol<LanternPlayer> {

    private boolean lastHasNoGravity;
    private GameMode lastGameMode = GameModes.NOT_SET;

    private int elytraRocketId = INVALID_ENTITY_ID;
    private boolean lastElytraFlying;
    private boolean lastElytraSpeedBoost;

    public PlayerEntityProtocol(LanternPlayer entity) {
        super(entity);
        setTickRate(1);
    }

    @Override
    protected void init(EntityProtocolInitContext context) {
        super.init(context);
        this.elytraRocketId = context.acquire();
    }

    @Override
    protected void remove(EntityProtocolInitContext context) {
        super.remove(context);
        context.release(this.elytraRocketId);
        this.elytraRocketId = INVALID_ENTITY_ID;
    }

    @Override
    protected void spawn(EntityProtocolUpdateContext context) {
        super.spawn(context);
        context.sendToSelf(() -> new MessagePlayOutEntityMetadata(getRootEntityId(), fillParameters(true)));
        context.sendToSelf(() -> new MessagePlayOutSetGameMode((LanternGameMode) getEntity().get(Keys.GAME_MODE).get()));
    }

    @Override
    protected void update(EntityProtocolUpdateContext context) {
        final GameMode gameMode = getEntity().get(Keys.GAME_MODE).get();
        if (gameMode != this.lastGameMode) {
            context.sendToSelf(() -> new MessagePlayOutSetGameMode((LanternGameMode) gameMode));
            this.lastGameMode = gameMode;
        }
        super.update(context);
        // Some 1.11.2 magic, ultra secret stuff...
        final boolean elytraFlying = this.entity.get(LanternKeys.IS_ELYTRA_FLYING).orElse(false);
        final boolean elytraSpeedBoost = this.entity.get(LanternKeys.ELYTRA_SPEED_BOOST).orElse(false);
        if (this.lastElytraFlying != elytraFlying || this.lastElytraSpeedBoost != elytraSpeedBoost) {
            if (this.lastElytraFlying && this.lastElytraSpeedBoost) {
                context.sendToAll(() -> new MessagePlayOutDestroyEntities(this.elytraRocketId));
            } else if (elytraFlying && elytraSpeedBoost) {
                // Create the fireworks data item
                final LanternItemStack itemStack = new LanternItemStack(ItemTypes.FIREWORKS);

                // Write the item to a parameter list
                final ByteBufParameterList parameterList = new ByteBufParameterList(ByteBufferAllocator.unpooled());
                parameterList.add(EntityParameters.Fireworks.ITEM, itemStack);
                parameterList.add(EntityParameters.Fireworks.ELYTRA_BOOST_PLAYER, getRootEntityId());

                parameterList.getByteBuffer().ifPresent(ByteBuffer::retain);

                context.sendToAll(() -> new MessagePlayOutSpawnObject(this.elytraRocketId, UUID.randomUUID(), 76, 0,
                        this.entity.getPosition(), 0, 0, Vector3d.ZERO));
                context.sendToAll(() -> new MessagePlayOutEntityMetadata(this.elytraRocketId, parameterList));
            }
            this.lastElytraSpeedBoost = elytraSpeedBoost;
            this.lastElytraFlying = elytraFlying;
        }
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        parameterList.add(EntityParameters.Humanoid.SCORE, this.entity.get(LanternKeys.SCORE).orElse(0));
        parameterList.add(EntityParameters.Humanoid.ADDITIONAL_HEARTS, 0f);
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);

        final boolean hasNoGravity = hasNoGravity();
        if (hasNoGravity != this.lastHasNoGravity) {
            parameterList.add(EntityParameters.Base.NO_GRAVITY, hasNoGravity);
            this.lastHasNoGravity = hasNoGravity;
        }
    }

    @Override
    boolean hasNoGravity() {
        return !this.entity.get(Keys.HAS_GRAVITY).orElse(true);
    }
}
