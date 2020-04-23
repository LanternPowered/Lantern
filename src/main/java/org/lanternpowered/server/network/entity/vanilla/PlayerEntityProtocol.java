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

import static org.lanternpowered.server.network.entity.EntityProtocolManager.INVALID_ENTITY_ID;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.event.EntityEvent;
import org.lanternpowered.server.entity.event.RefreshAbilitiesPlayerEvent;
import org.lanternpowered.server.entity.event.SpectateEntityEvent;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.extra.accessory.TopHat;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.network.entity.EntityProtocolInitContext;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.entity.parameter.DefaultParameterList;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityHeadLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityMetadata;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerAbilities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerHealthUpdate;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetCamera;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetEntityPassengers;
import org.lanternpowered.server.network.vanilla.message.type.play.SetGameModeMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.SpawnMobMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnObject;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.math.vector.Vector3d;

import java.util.OptionalInt;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

public class PlayerEntityProtocol extends HumanoidEntityProtocol<LanternPlayer> {

    private boolean lastHasNoGravity;
    private GameMode lastGameMode = GameModes.NOT_SET;

    private int elytraRocketId = INVALID_ENTITY_ID;
    private boolean lastElytraFlying;
    private boolean lastElytraSpeedBoost;

    private boolean lastCanFly;
    private float lastFlySpeed;
    private boolean lastFlying;
    private float lastFieldOfView;

    private int[] passengerStack = new int[13];
    @Nullable private TopHat lastTopHat;

    private byte lastYaw0;
    private byte lastPitch0;
    private byte lastFlags0;

    private float lastAbsorption;
    private float lastHealth;
    private int lastFoodLevel;
    private boolean lastHungry = true;

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
        final ParameterList parameterList = new DefaultParameterList();
        parameterList.add(EntityParameters.Base.FLAGS, (byte) 0x20);
        if (type == 55) {
            parameterList.add(EntityParameters.AbstractSlime.SIZE, -1);
        } else if (type == 101) {
            parameterList.add(EntityParameters.Ageable.IS_BABY, true);
        }
        context.sendToAll(() -> new SpawnMobMessage(this.passengerStack[index], UUID.randomUUID(), type, getEntity().getPosition(),
                (byte) 0, (byte) 0, (byte) 0, Vector3d.ZERO));
        context.sendToAll(() -> new MessagePlayOutEntityMetadata(this.passengerStack[index], parameterList));
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
        /*
        final LanternItemStack paneItem;
        final LanternItemStack blockItem;

        final Optional<DyeColor> dyeColor = hat.getDyeColor();
        if (dyeColor.isPresent()) {
            paneItem = new LanternItemStack(ItemTypes.CARPET);
            paneItem.offer(Keys.DYE_COLOR, dyeColor.get());
            blockItem = new LanternItemStack(ItemTypes.WOOL);
            blockItem.offer(Keys.DYE_COLOR, dyeColor.get());
        } else if (hat == TopHats.GOLD) {
            paneItem = new LanternItemStack(ItemTypes.LIGHT_WEIGHTED_PRESSURE_PLATE);
            blockItem = new LanternItemStack(ItemTypes.GOLD_BLOCK);
        } else if (hat == TopHats.IRON) {
            paneItem = new LanternItemStack(ItemTypes.HEAVY_WEIGHTED_PRESSURE_PLATE);
            blockItem = new LanternItemStack(ItemTypes.IRON_BLOCK);
        } else if (hat == TopHats.WOOD) {
            paneItem = new LanternItemStack(ItemTypes.WOODEN_PRESSURE_PLATE);
            blockItem = new LanternItemStack(ItemTypes.PLANKS);
        } else if (hat == TopHats.STONE) {
            paneItem = new LanternItemStack(ItemTypes.STONE_PRESSURE_PLATE);
            blockItem = new LanternItemStack(ItemTypes.STONE);
        } else if (hat == TopHats.SNOW) {
            paneItem = new LanternItemStack(ItemTypes.SNOW_LAYER);
            blockItem = new LanternItemStack(ItemTypes.SNOW);
        } else {
            throw new IllegalStateException();
        }

        // TODO: Update
        byte flags = 0x20;
        if (getEntity().get(Keys.GLOWING).get()) {
            flags |= 0x40;
        }

        final ParameterList parameterList1 = new DefaultParameterList();
        parameterList1.add(EntityParameters.ArmorStand.FLAGS, (byte) (0x08 | 0x10));
        parameterList1.add(EntityParameters.Base.FLAGS, flags);
        final int id1 = this.passengerStack[10];

        context.sendToAll(() -> new MessagePlayOutSpawnObject(id1, UUID.randomUUID(), 78, 0,
                getEntity().getPosition(), 0, 0, Vector3d.ZERO));
        context.sendToAll(() -> new MessagePlayOutEntityMetadata(id1, parameterList1));
        context.sendToAll(() -> new MessagePlayOutEntityEquipment(id1, 5, paneItem));

        final ParameterList parameterList2 = new DefaultParameterList();
        parameterList2.add(EntityParameters.ArmorStand.FLAGS, (byte) (0x08 | 0x10 | 0x01));
        parameterList2.add(EntityParameters.Base.FLAGS, flags);
        final int id2 = this.passengerStack[11];

        context.sendToAll(() -> new MessagePlayOutSpawnObject(id2, UUID.randomUUID(), 78, 0,
                getEntity().getPosition(), 0, 0, Vector3d.ZERO));
        context.sendToAll(() -> new MessagePlayOutEntityMetadata(id2, parameterList2));
        context.sendToAll(() -> new MessagePlayOutEntityEquipment(id2, 5, blockItem));

        sendPassengers(context, 8, 10);
        sendPassengers(context, 9, 11);
*/
        /*
        final LanternItemStack boneItem = new LanternItemStack(ItemTypes.BONE);

        float angleA = 250f;
        float angleB = 250f;
        for (int i = 12; i <= 14; i++) {
            final ParameterList parameterList3 = new ByteBufParameterList(ByteBufferAllocator.unpooled());
            parameterList3.add(EntityParameters.ArmorStand.FLAGS, (byte) (0x08 | 0x10 | 0x01 | 0x04));
            parameterList3.add(EntityParameters.ArmorStand.LEFT_ARM_ROTATION, new Vector3f(50f, 360f - angleA, angleB));
            parameterList3.add(EntityParameters.ArmorStand.RIGHT_ARM_ROTATION, new Vector3f(50f, angleA, 360f - angleB));
            parameterList3.add(EntityParameters.Base.FLAGS, flags);
            final int id3 = this.passengerStack[i];

            context.sendToAll(() -> new MessagePlayOutSpawnObject(id3, UUID.randomUUID(), 78, 0,
                    getEntity().getPosition(), 0, 0, Vector3d.ZERO));
            context.sendToAll(() -> new MessagePlayOutEntityMetadata(id3, parameterList3));
            context.sendToAll(() -> new MessagePlayOutEntityEquipment(id3, 0, boneItem));
            context.sendToAll(() -> new MessagePlayOutEntityEquipment(id3, 1, boneItem));

            angleA -= 15f;
            angleB -= 15f;
        }

        sendPassengers(context, 7, 9, 12, 13, 14);
        */
    }

    @Override
    protected void spawn(EntityProtocolUpdateContext context) {
        super.spawn(context);
        context.sendToSelf(() -> new MessagePlayOutEntityMetadata(getRootEntityId(), fillSpawnParameters()));
        final GameMode gameMode = getEntity().get(Keys.GAME_MODE).get();
        context.sendToSelf(() -> new SetGameModeMessage((LanternGameMode) gameMode));
        context.sendToSelf(() -> new MessagePlayOutPlayerAbilities(
                this.entity.get(Keys.IS_FLYING).orElse(false), canFly(), false, gameMode == GameModes.CREATIVE, getFlySpeed(), getFovModifier()));

        final TopHat topHat = getTopHat();
        if (topHat != null && !getEntity().get(Keys.INVISIBLE).get()) {
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
        final float fieldOfView = getFovModifier();
        final boolean flying = this.entity.get(Keys.IS_FLYING).orElse(false);
        if (gameMode != this.lastGameMode || canFly != this.lastCanFly || flySpeed != this.lastFlySpeed
                || fieldOfView != this.lastFieldOfView || flying != this.lastFlying) {
            if (gameMode != this.lastGameMode) {
                context.sendToSelf(() -> new SetGameModeMessage((LanternGameMode) gameMode));
            }
            context.sendToSelf(() -> new MessagePlayOutPlayerAbilities(
                    flying, canFly, false, gameMode == GameModes.CREATIVE, flySpeed, fieldOfView));
            this.lastGameMode = gameMode;
            this.lastCanFly = canFly;
            this.lastFlySpeed = flySpeed;
            this.lastFieldOfView = fieldOfView;
            this.lastFlying = flying;
        }
        final float health = this.entity.get(Keys.HEALTH).get().floatValue();
        final int foodLevel = this.entity.get(Keys.FOOD_LEVEL).get();
        final float saturation = this.entity.get(Keys.SATURATION).get().floatValue();
        if (health != this.lastHealth || foodLevel != this.lastFoodLevel || saturation == 0.0f != this.lastHungry) {
            context.sendToSelf(() -> new MessagePlayOutPlayerHealthUpdate(health, foodLevel, saturation));
            this.lastHealth = health;
            this.lastFoodLevel = foodLevel;
            this.lastHungry = saturation == 0.0f;
        }
        super.update(context);
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
        if (this.lastYaw0 != this.lastYaw || this.lastPitch0 != this.lastPitch || this.lastFlags0 != this.lastFlags) {
            for (final int id : this.passengerStack) {
                context.sendToSelf(() -> new MessagePlayOutEntityLook(id, this.lastYaw, this.lastPitch, this.entity.isOnGround()));
            }
            if (this.lastTopHat != null) {
                context.sendToSelf(() -> new MessagePlayOutEntityHeadLook(this.passengerStack[10], this.lastYaw));
                context.sendToSelf(() -> new MessagePlayOutEntityHeadLook(this.passengerStack[11], this.lastYaw));
                context.sendToSelf(() -> new MessagePlayOutEntityHeadLook(this.passengerStack[12], this.lastYaw));
                // context.sendToSelf(() -> new MessagePlayOutEntityHeadLook(this.passengerStack[13], this.lastYaw));
                // context.sendToSelf(() -> new MessagePlayOutEntityHeadLook(this.passengerStack[14], this.lastYaw));
                if (this.lastFlags0 != this.lastFlags) {
                    final boolean glow = (this.lastFlags & 0x40) != 0;
                    final ParameterList parameterList = new DefaultParameterList();
                    parameterList.add(EntityParameters.Base.FLAGS, (byte) (0x20 | (glow ? 0x40 : 0x00)));
                    context.sendToAll(() -> new MessagePlayOutEntityMetadata(this.passengerStack[10], parameterList));
                    context.sendToAll(() -> new MessagePlayOutEntityMetadata(this.passengerStack[11], parameterList));
                }
            }
            this.lastYaw0 = this.lastYaw;
            this.lastPitch0 = this.lastPitch;
            this.lastFlags0 = this.lastFlags;
        }
        // Some 1.11.2 magic, ultra secret stuff...
        final boolean elytraFlying = this.entity.get(Keys.IS_ELYTRA_FLYING).orElse(false);
        final boolean elytraSpeedBoost = this.entity.get(LanternKeys.ELYTRA_SPEED_BOOST).orElse(false);
        if (this.lastElytraFlying != elytraFlying || this.lastElytraSpeedBoost != elytraSpeedBoost) {
            if (this.lastElytraFlying && this.lastElytraSpeedBoost) {
                context.sendToAll(() -> new MessagePlayOutDestroyEntities(this.elytraRocketId));
            } else if (elytraFlying && elytraSpeedBoost) {
                // Create the fireworks data item
                final LanternItemStack itemStack = new LanternItemStack(ItemTypes.FIREWORK_ROCKET);

                // Write the item to a parameter list
                final DefaultParameterList parameterList = new DefaultParameterList();
                parameterList.add(EntityParameters.Fireworks.ITEM, itemStack);
                parameterList.add(EntityParameters.Fireworks.ELYTRA_BOOST_PLAYER, OptionalInt.of(getEntity().getNetworkId()));

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
            final float fov = getFovModifier();
            context.sendToSelf(() -> new MessagePlayOutPlayerAbilities(
                    this.entity.get(Keys.IS_FLYING).orElse(false), canFly(), false, gameMode == GameModes.CREATIVE, flySpeed, fov));
        } else {
            super.handleEvent(context, event);
        }
    }

    @Override
    protected boolean isSprinting() {
        return this.entity.get(Keys.IS_SPRINTING).orElse(false);
    }

    private float getFovModifier() {
        // client = (walkSpeed / fovModifier + 1) / 2
        // fovModifier = walkSpeed / (client * 2 - 1)
        final float walkSpeed = this.entity.get(Keys.WALKING_SPEED).orElse(0.1).floatValue();
        final float client = this.entity.get(LanternKeys.FIELD_OF_VIEW_MODIFIER).orElse(1.0).floatValue();
        return walkSpeed / (client * 2f - 1f);
    }

    private float getFlySpeed() {
        return this.entity.get(Keys.IS_ELYTRA_FLYING).orElse(false) ? this.entity.get(LanternKeys.ELYTRA_GLIDE_SPEED).orElse(0.1).floatValue() :
                this.entity.get(Keys.CAN_FLY).orElse(false) ? this.entity.get(Keys.FLYING_SPEED).orElse(0.1).floatValue() : 0f;
    }

    private boolean canFly() {
        // TODO: Double jump?
        return this.entity.get(Keys.CAN_FLY).orElse(false) || this.entity.get(LanternKeys.CAN_WALL_JUMP).orElse(false) ||
                (this.entity.get(LanternKeys.SUPER_STEVE).orElse(false) && !this.entity.get(Keys.IS_ELYTRA_FLYING).orElse(false));
    }

    private float getAbsorption() {
        return getEntity().get(Keys.ABSORPTION).get().floatValue();
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        parameterList.add(EntityParameters.Humanoid.SCORE, this.entity.get(LanternKeys.SCORE).orElse(0));
        parameterList.add(EntityParameters.Humanoid.ADDITIONAL_HEARTS, getAbsorption());
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);

        final boolean hasNoGravity = hasNoGravity();
        if (hasNoGravity != this.lastHasNoGravity) {
            parameterList.add(EntityParameters.Base.NO_GRAVITY, hasNoGravity);
            this.lastHasNoGravity = hasNoGravity;
        }
        final float absorption = getAbsorption();
        if (absorption != this.lastAbsorption) {
            parameterList.add(EntityParameters.Humanoid.ADDITIONAL_HEARTS, absorption);
            this.lastAbsorption = absorption;
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
