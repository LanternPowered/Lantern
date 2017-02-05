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
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.LanternLiving;
import org.lanternpowered.server.entity.event.CollectEntityEvent;
import org.lanternpowered.server.entity.event.EntityEvent;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.inventory.equipment.LanternEquipmentTypes;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.ByteBufferAllocator;
import org.lanternpowered.server.network.entity.AbstractEntityProtocol;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.entity.parameter.ByteBufParameterList;
import org.lanternpowered.server.network.entity.parameter.EmptyParameterList;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityCollectItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityEquipment;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityHeadLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityLookAndRelativeMove;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityMetadata;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityRelativeMove;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityTeleport;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityVelocity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetEntityPassengers;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

public abstract class EntityProtocol<E extends LanternEntity> extends AbstractEntityProtocol<E> {

    private static class Holder {

        private final static EquipmentType[] EQUIPMENT_TYPES =
                {
                        LanternEquipmentTypes.MAIN_HAND,
                        LanternEquipmentTypes.OFF_HAND,
                        EquipmentTypes.BOOTS,
                        EquipmentTypes.LEGGINGS,
                        EquipmentTypes.CHESTPLATE,
                        EquipmentTypes.HEADWEAR
                };
    }

    private long lastX;
    private long lastY;
    private long lastZ;

    private byte lastYaw;
    private byte lastPitch;

    private byte lastHeadYaw;

    private double lastVelX;
    private double lastVelY;
    private double lastVelZ;

    private byte lastFlags;
    private boolean lastSilent;
    private short lastAirLevel;
    private boolean lastCustomNameVisible;

    @Nullable private String lastCustomName;

    private List<Entity> lastPassengers = Collections.emptyList();

    private final Int2ObjectMap<ItemStack> lastEquipment = new Int2ObjectOpenHashMap<>();

    public EntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected void destroy(EntityProtocolUpdateContext context) {
        context.sendToAllExceptSelf(new MessagePlayOutDestroyEntities(getRootEntityId()));
    }

    protected void spawnWithEquipment(EntityProtocolUpdateContext context) {
        if (hasEquipment() && this.entity instanceof Carrier) {
            final Inventory inventory = ((Carrier) this.entity).getInventory();
            for (int i = 0; i < Holder.EQUIPMENT_TYPES.length; i++) {
                final EquipmentType equipmentType = Holder.EQUIPMENT_TYPES[i];
                final ItemStack itemStack = inventory.query(equipmentType).first().peek().orElse(null);
                final int slotIndex = i;
                if (itemStack != null) {
                    context.sendToAllExceptSelf(() -> new MessagePlayOutEntityEquipment(getRootEntityId(), slotIndex, itemStack));
                }
            }
        }
    }

    @Override
    protected void update(EntityProtocolUpdateContext context) {
        final Vector3d rot = this.entity.getRotation();
        final Vector3d headRot = this.entity instanceof Living ? ((Living) this.entity).getHeadRotation() : null;
        final Vector3d pos = this.entity.getPosition();

        final double x = pos.getX();
        final double y = pos.getY();
        final double z = pos.getZ();

        final long xu = (long) (x * 4096);
        final long yu = (long) (y * 4096);
        final long zu = (long) (z * 4096);

        final byte yaw = wrapAngle(rot.getY());
        // All living entities have a head rotation and changing the pitch
        // would only affect the head pitch.
        final byte pitch = wrapAngle((headRot != null ? headRot : rot).getX());

        boolean dirtyPos = xu != this.lastX || yu != this.lastY || zu != this.lastZ;
        boolean dirtyRot = yaw != this.lastYaw || pitch != this.lastPitch;

        // TODO: On ground state

        final int entityId = getRootEntityId();

        if (dirtyRot) {
            this.lastYaw = yaw;
            this.lastPitch = pitch;
        }
        if (dirtyPos) {
            final long dxu = xu - this.lastX;
            final long dyu = yu - this.lastY;
            final long dzu = zu - this.lastZ;
            this.lastX = xu;
            this.lastY = yu;
            this.lastZ = zu;

            // Don't send movement messages if the entity
            // is a passengers, otherwise glitches will
            // rule the world.
            if (!this.entity.getVehicle().isPresent()) {
                if (Math.abs(dxu) <= Short.MAX_VALUE && Math.abs(dyu) <= Short.MAX_VALUE && Math.abs(dzu) <= Short.MAX_VALUE) {
                    if (dirtyRot) {
                        context.sendToAllExceptSelf(new MessagePlayOutEntityLookAndRelativeMove(entityId,
                                (int) dxu, (int) dyu, (int) dzu, yaw, pitch, false));
                        // The rotation is already send
                        dirtyRot = false;
                    } else {
                        context.sendToAllExceptSelf(new MessagePlayOutEntityRelativeMove(entityId,
                                (int) dxu, (int) dyu, (int) dzu, false));
                    }
                } else {
                    context.sendToAllExceptSelf(new MessagePlayOutEntityTeleport(entityId,
                            x, y, z, yaw, pitch, false));
                    // The rotation is already send
                    dirtyRot = false;
                }
            }
        }
        if (dirtyRot) {
            context.sendToAllExceptSelf(() -> new MessagePlayOutEntityLook(entityId, yaw, pitch, false));
        }
        if (headRot != null) {
            final byte headYaw = wrapAngle(headRot.getY());
            if (headYaw != this.lastHeadYaw) {
                context.sendToAllExceptSelf(() -> new MessagePlayOutEntityHeadLook(entityId, headYaw));
                this.lastHeadYaw = headYaw;
            }
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
                fillParameters(false, EmptyParameterList.INSTANCE) : fillParameters(false);
        // There were parameters applied
        if (!parameterList.isEmpty()) {
            context.sendToAll(() -> new MessagePlayOutEntityMetadata(entityId, parameterList));
        }
        if (hasEquipment() && this.entity instanceof Carrier) {
            final Inventory inventory = ((Carrier) this.entity).getInventory();
            for (int i = 0; i < Holder.EQUIPMENT_TYPES.length; i++) {
                final EquipmentType equipmentType = Holder.EQUIPMENT_TYPES[i];
                final ItemStack itemStack = inventory.query(equipmentType).first().peek().orElse(null);
                final ItemStack oldItemStack = this.lastEquipment.get(i);
                if (!LanternItemStack.isSimilar(itemStack, oldItemStack)) {
                    this.lastEquipment.put(i, itemStack);
                    final int slotIndex = i;
                    context.sendToAllExceptSelf(() -> new MessagePlayOutEntityEquipment(getRootEntityId(), slotIndex, itemStack));
                }
            }
        }
        // TODO: Update attributes
    }

    /**
     * Gets whether the entity can hold equipment
     * on the client side.
     *
     * @return Has equipment
     */
    protected boolean hasEquipment() {
        return false;
    }

    @Override
    protected void handleEvent(EntityProtocolUpdateContext context, EntityEvent event) {
        if (event instanceof CollectEntityEvent) {
            final LanternLiving collector = (LanternLiving) ((CollectEntityEvent) event).getCollector();
            context.getId(collector).ifPresent(id -> {
                final int count = ((CollectEntityEvent) event).getCollectedItemsCount();
                context.sendToAll(() -> new MessagePlayOutEntityCollectItem(id, getRootEntityId(), count));
            });
        } else {
            super.handleEvent(context, event);
        }
    }

    @Override
    protected void postUpdate(EntityProtocolUpdateContext context) {
        final List<Entity> passengers = this.entity.getPassengers();
        if (!passengers.equals(this.lastPassengers)) {
            this.lastPassengers = passengers;
            sendPassengers(context, passengers);
        }
    }

    @Override
    public void postSpawn(EntityProtocolUpdateContext context) {
        sendPassengers(context, this.entity.getPassengers());
    }

    private void sendPassengers(EntityProtocolUpdateContext context, List<Entity> passengers) {
        context.sendToAll(() -> {
            final IntList passengerIds = new IntArrayList();
            for (Entity passenger : passengers) {
                context.getId(passenger).ifPresent(passengerIds::add);
            }
            return new MessagePlayOutSetEntityPassengers(getRootEntityId(), passengerIds.toIntArray());
        });
    }

    /**
     * Fills a {@link ByteBuffer} with parameters to spawn or update the {@link Entity}.
     *
     * @param initial Whether the entity is being spawned, the byte buffer can be null if this is false
     * @return The byte buffer
     */
    ParameterList fillParameters(boolean initial) {
        return fillParameters(initial, new ByteBufParameterList(ByteBufferAllocator.unpooled()));
    }

    private ParameterList fillParameters(boolean initial, ParameterList parameterList) {
        if (initial) {
            spawn(parameterList);
        } else {
            update(parameterList);
        }
        return parameterList;
    }

    protected boolean isSneaking() {
        return false;
    }

    protected boolean isUsingItem() {
        return false;
    }

    protected boolean isSprinting() {
        return false;
    }

    /**
     * Fills the {@link ParameterList} with parameters to spawn the {@link Entity} on
     * the client.
     *
     * @param parameterList The parameter list to fill
     */
    protected void spawn(ParameterList parameterList) {
        parameterList.add(EntityParameters.Base.FLAGS, packFlags());
        parameterList.add(EntityParameters.Base.AIR_LEVEL, getAirLevel());
        parameterList.add(EntityParameters.Base.CUSTOM_NAME, getCustomName());
        parameterList.add(EntityParameters.Base.CUSTOM_NAME_VISIBLE, isCustomNameVisible());
        parameterList.add(EntityParameters.Base.IS_SILENT, isSilent());
        parameterList.add(EntityParameters.Base.NO_GRAVITY, hasNoGravity());
    }

    boolean isCustomNameVisible() {
        return this.entity.get(Keys.CUSTOM_NAME_VISIBLE).orElse(true);
    }

    String getCustomName() {
        return this.entity.get(Keys.DISPLAY_NAME).map(LanternTexts::toLegacy).orElse("");
    }

    boolean isSilent() {
        // Always silent for regular entities, we will handle our own sounds
        return true;
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
        if (isSneaking()) {
            flags |= 0x02;
        }
        if (isSprinting()) {
            flags |= 0x08;
        }
        if (isUsingItem()) {
            flags |= 0x10;
        }
        if (this.entity.get(Keys.INVISIBLE).orElse(false)) {
            flags |= 0x20;
        }
        if (this.entity.get(Keys.GLOWING).orElse(false)) {
            flags |= 0x40;
        }
        if (this.entity.get(LanternKeys.IS_ELYTRA_FLYING).orElse(false)) {
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
        final byte flags = packFlags();
        if (flags != this.lastFlags) {
            parameterList.add(EntityParameters.Base.FLAGS, flags);
            this.lastFlags = flags;
        }
        final boolean silent = isSilent();
        if (silent != this.lastSilent) {
            parameterList.add(EntityParameters.Base.IS_SILENT, silent);
            this.lastSilent = silent;
        }
        final boolean customNameVisible = isCustomNameVisible();
        if (customNameVisible != this.lastCustomNameVisible) {
            parameterList.add(EntityParameters.Base.CUSTOM_NAME_VISIBLE, customNameVisible);
            this.lastCustomNameVisible = customNameVisible;
        }
        final String customName = getCustomName();
        if (!customName.equals(this.lastCustomName)) {
            parameterList.add(EntityParameters.Base.CUSTOM_NAME, customName);
            this.lastCustomName = customName;
        }
        final short airLevel = getAirLevel();
        if (airLevel != this.lastAirLevel) {
            parameterList.add(EntityParameters.Base.AIR_LEVEL, airLevel);
            this.lastAirLevel = airLevel;
        }
    }

    /**
     * Gets the air level of the entity.
     *
     * The air is by default 300 because the entities don't even use the air level,
     * except for the players. This method can be overridden if needed.
     *
     * @return The air level
     */
    protected short getAirLevel() {
        return 300;
    }
}
