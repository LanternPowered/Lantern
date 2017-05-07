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
import it.unimi.dsi.fastutil.ints.IntSet;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.event.EntityEvent;
import org.lanternpowered.server.entity.event.RefreshAbilitiesPlayerEvent;
import org.lanternpowered.server.entity.event.SpectateEntityEvent;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.extra.accessory.TopHat;
import org.lanternpowered.server.extra.accessory.TopHats;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.ByteBufferAllocator;
import org.lanternpowered.server.network.entity.EntityProtocolInitContext;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.entity.parameter.ByteBufParameterList;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityEquipment;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityHeadLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityMetadata;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerAbilities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetCamera;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetEntityPassengers;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetGameMode;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnMob;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnObject;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.ItemTypes;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

public class PlayerEntityProtocol extends HumanoidEntityProtocol<LanternPlayer> {

    private boolean lastHasNoGravity;
    private GameMode lastGameMode = GameModes.NOT_SET;

    private int elytraRocketId = INVALID_ENTITY_ID;
    private boolean lastElytraFlying;
    private boolean lastElytraSpeedBoost;

    private boolean lastCanFly;
    private float lastFlySpeed;

    private int[] passengerStack = new int[12];
    @Nullable private TopHat lastTopHat;

    private byte lastYaw0;
    private byte lastPitch0;

    public PlayerEntityProtocol(LanternPlayer entity) {
        super(entity);
        setTickRate(1);
    }

    @Override
    protected void init(EntityProtocolInitContext context) {
        super.init(context);
        this.elytraRocketId = context.acquire();
        context.acquire(this.passengerStack);
        getEntity().offer(Keys.INVISIBLE, false);
    }

    @Override
    protected void remove(EntityProtocolInitContext context) {
        super.remove(context);
        context.release(this.elytraRocketId);
        this.elytraRocketId = INVALID_ENTITY_ID;
        context.release(this.passengerStack);
    }

    @Override
    protected IntSet getPassengerIds(EntityProtocolUpdateContext context) {
        final IntSet passengerIds = super.getPassengerIds(context);
        if (this.lastTopHat != null) {
            passengerIds.add(this.passengerStack[0]);
        }
        return passengerIds;
    }

    private void sendStackEntry(EntityProtocolUpdateContext context, int index, int type) {
        final ParameterList parameterList = new ByteBufParameterList(ByteBufferAllocator.unpooled());
        parameterList.add(EntityParameters.Base.FLAGS, (byte) 0x20);
        if (type == 55) {
            parameterList.add(EntityParameters.AbstractSlime.SIZE, -1);
        } else if (type == 101) {
            parameterList.add(EntityParameters.Ageable.IS_BABY, true);
        }
        context.sendToAll(() -> new MessagePlayOutSpawnMob(this.passengerStack[index], UUID.randomUUID(), type, getEntity().getPosition(),
                (byte) 0, (byte) 0, (byte) 0, Vector3d.ZERO, parameterList));
    }

    private void sendPassengers(EntityProtocolUpdateContext context, int index, int... indexes) {
        final int[] passengers = new int[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            passengers[i] = this.passengerStack[indexes[i]];
        }
        context.sendToAll(() -> new MessagePlayOutSetEntityPassengers(this.passengerStack[index], passengers));
    }

    private void sendPassengerStack(EntityProtocolUpdateContext context) {
        sendStackEntry(context, 0, 55);
        sendStackEntry(context, 1, 55);
        sendStackEntry(context, 2, 93);
        sendStackEntry(context, 3, 55);
        sendStackEntry(context, 4, 55);
        sendStackEntry(context, 5, 60);
        sendStackEntry(context, 6, 55);
        sendStackEntry(context, 7, 98);
        sendStackEntry(context, 8, 101);
        sendStackEntry(context, 9, 101);

        sendPassengers(context, 0, 1);
        sendPassengers(context, 1, 2);
        sendPassengers(context, 2, 3);
        sendPassengers(context, 3, 4);
        sendPassengers(context, 4, 5);
        sendPassengers(context, 5, 6);
        sendPassengers(context, 6, 7, 8);
        sendPassengers(context, 7, 9);
    }

    private void removePassengerStack(EntityProtocolUpdateContext context) {
        context.sendToAll(() -> new MessagePlayOutDestroyEntities(this.passengerStack));
    }

    private void sendHat(EntityProtocolUpdateContext context, TopHat hat) {
        final LanternItemStack paneItem;
        final LanternItemStack blockItem;

        final Optional<DyeColor> dyeColor = hat.getDyeColor();
        if (dyeColor.isPresent()) {
            paneItem = new LanternItemStack(BlockTypes.CARPET);
            paneItem.offer(Keys.DYE_COLOR, dyeColor.get());
            blockItem = new LanternItemStack(BlockTypes.WOOL);
            paneItem.offer(Keys.DYE_COLOR, dyeColor.get());
        } else if (hat == TopHats.GOLD) {
            paneItem = new LanternItemStack(BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE);
            blockItem = new LanternItemStack(BlockTypes.GOLD_BLOCK);
        } else if (hat == TopHats.IRON) {
            paneItem = new LanternItemStack(BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE);
            blockItem = new LanternItemStack(BlockTypes.IRON_BLOCK);
        } else if (hat == TopHats.WOOD) {
            paneItem = new LanternItemStack(BlockTypes.WOODEN_PRESSURE_PLATE);
            blockItem = new LanternItemStack(BlockTypes.PLANKS);
        } else if (hat == TopHats.STONE) {
            paneItem = new LanternItemStack(BlockTypes.STONE_PRESSURE_PLATE);
            blockItem = new LanternItemStack(BlockTypes.STONE);
        } else {
            throw new IllegalStateException();
        }

        final ParameterList parameterList1 = new ByteBufParameterList(ByteBufferAllocator.unpooled());
        parameterList1.add(EntityParameters.ArmorStand.FLAGS, (byte) (0x08 | 0x10));
        parameterList1.add(EntityParameters.Base.FLAGS, (byte) 0x20);
        final int id1 = this.passengerStack[10];

        context.sendToAll(() -> new MessagePlayOutSpawnObject(id1, UUID.randomUUID(), 78, 0,
                getEntity().getPosition(), 0, 0, Vector3d.ZERO));
        context.sendToAll(() -> new MessagePlayOutEntityMetadata(id1, parameterList1));
        context.sendToAll(() -> new MessagePlayOutEntityEquipment(id1, 5, paneItem));

        final ParameterList parameterList2 = new ByteBufParameterList(ByteBufferAllocator.unpooled());
        parameterList2.add(EntityParameters.ArmorStand.FLAGS, (byte) (0x08 | 0x10 | 0x01));
        parameterList2.add(EntityParameters.Base.FLAGS, (byte) 0x20);
        final int id2 = this.passengerStack[11];

        context.sendToAll(() -> new MessagePlayOutSpawnObject(id2, UUID.randomUUID(), 78, 0,
                getEntity().getPosition(), 0, 0, Vector3d.ZERO));
        context.sendToAll(() -> new MessagePlayOutEntityMetadata(id2, parameterList2));
        context.sendToAll(() -> new MessagePlayOutEntityEquipment(id2, 5, blockItem));

        sendPassengers(context, 8, 10);
        sendPassengers(context, 9, 11);
    }

    @Override
    protected void spawn(EntityProtocolUpdateContext context) {
        super.spawn(context);
        context.sendToSelf(() -> new MessagePlayOutEntityMetadata(getRootEntityId(), fillParameters(true)));
        final GameMode gameMode = getEntity().get(Keys.GAME_MODE).get();
        context.sendToSelf(() -> new MessagePlayOutSetGameMode((LanternGameMode) gameMode));
        context.sendToSelf(() -> new MessagePlayOutPlayerAbilities(
                this.entity.get(Keys.IS_FLYING).orElse(false), canFly(), false, gameMode == GameModes.CREATIVE, getFlySpeed(), 0.01f));

        final TopHat topHat = getTopHat();
        if (topHat != null) {
            sendPassengerStack(context);
            sendHat(context, topHat);
        }
    }

    @Nullable
    private TopHat getTopHat() {
        return (TopHat) getEntity().get(LanternKeys.ACCESSORIES).get().stream()
                .filter(a -> a instanceof TopHat).findFirst().orElse(null);
    }

    @Override
    protected void update(EntityProtocolUpdateContext context) {
        final GameMode gameMode = this.entity.get(Keys.GAME_MODE).get();
        final boolean canFly = canFly();
        final float flySpeed = getFlySpeed();
        if (gameMode != this.lastGameMode) {
            context.sendToSelf(() -> new MessagePlayOutSetGameMode((LanternGameMode) gameMode));
            context.sendToSelf(() -> new MessagePlayOutPlayerAbilities(
                    this.entity.get(Keys.IS_FLYING).orElse(false), canFly, false, gameMode == GameModes.CREATIVE, flySpeed, 0.01f));
            this.lastGameMode = gameMode;
            this.lastCanFly = canFly;
            this.lastFlySpeed = flySpeed;
        } else if (canFly != this.lastCanFly || flySpeed != this.lastFlySpeed) {
            context.sendToSelf(() -> new MessagePlayOutPlayerAbilities(
                    this.entity.get(Keys.IS_FLYING).orElse(false), canFly, false, gameMode == GameModes.CREATIVE, flySpeed, 0.01f));
            this.lastCanFly = canFly;
            this.lastFlySpeed = flySpeed;
        }
        final TopHat topHat = getTopHat();
        if (topHat != this.lastTopHat) {
            if (this.lastTopHat == null) {
                sendPassengerStack(context);
                sendHat(context, topHat);
            } else if (topHat == null) {
                removePassengerStack(context);
            } else {
                sendHat(context, topHat);
            }
            this.lastTopHat = topHat;
        }
        super.update(context);
        if (this.lastYaw0 != this.lastYaw || this.lastPitch0 != this.lastPitch) {
            for (final int id : this.passengerStack) {
                context.sendToSelf(() -> new MessagePlayOutEntityLook(id, this.lastYaw, this.lastPitch, this.entity.isOnGround()));
            }
            this.lastYaw0 = this.lastYaw;
            this.lastPitch0 = this.lastPitch;
            if (this.lastTopHat != null) {
                context.sendToSelf(() -> new MessagePlayOutEntityHeadLook(this.passengerStack[10], this.lastYaw));
                context.sendToSelf(() -> new MessagePlayOutEntityHeadLook(this.passengerStack[11], this.lastYaw));
            }
        }
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
    protected void handleEvent(EntityProtocolUpdateContext context, EntityEvent event) {
        if (event instanceof SpectateEntityEvent) {
            final Entity entity = ((SpectateEntityEvent) event).getSpectatedEntity().orElse(null);
            if (entity == null) {
                context.sendToSelf(() -> new MessagePlayOutSetCamera(getRootEntityId()));
            } else {
                context.getId(entity).ifPresent(id -> context.sendToSelf(() -> new MessagePlayOutSetCamera(id)));
            }
        } else if (event instanceof RefreshAbilitiesPlayerEvent) {
            final GameMode gameMode = this.entity.get(Keys.GAME_MODE).get();
            final float flySpeed = getFlySpeed();
            context.sendToSelf(() -> new MessagePlayOutPlayerAbilities(
                    this.entity.get(Keys.IS_FLYING).orElse(false), canFly(), false, gameMode == GameModes.CREATIVE, flySpeed, 0.01f));
        } else {
            super.handleEvent(context, event);
        }
    }

    private float getFlySpeed() {
        return this.entity.get(LanternKeys.IS_ELYTRA_FLYING).orElse(false) ? this.entity.get(LanternKeys.ELYTRA_GLIDE_SPEED).orElse(0.1).floatValue() :
                this.entity.get(Keys.CAN_FLY).orElse(false) ? this.entity.get(Keys.FLYING_SPEED).orElse(0.1).floatValue() : 0f;
    }

    private boolean canFly() {
        // TODO: Double jump?
        return this.entity.get(Keys.CAN_FLY).orElse(false) || this.entity.get(LanternKeys.CAN_WALL_JUMP).orElse(false) ||
                (this.entity.get(LanternKeys.SUPER_STEVE).orElse(false) && !this.entity.get(LanternKeys.IS_ELYTRA_FLYING).orElse(false));
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

    @Override
    protected short getAirLevel() {
        final double max = this.entity.get(Keys.MAX_AIR).orElse(300);
        final double air = this.entity.get(Keys.REMAINING_AIR).orElse(300);
        return (short) (Math.min(1.0, air / max) * 300.0);
    }
}
