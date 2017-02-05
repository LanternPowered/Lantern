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
package org.lanternpowered.server.entity;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.data.AbstractDataHolder;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.data.value.KeyRegistration;
import org.lanternpowered.server.entity.event.DamageEntityEvent;
import org.lanternpowered.server.entity.event.EntityEvent;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.registry.type.entity.EntityTypeRegistryModule;
import org.lanternpowered.server.network.entity.EntityProtocolType;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.util.Quaternions;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.translation.FixedTranslation;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.RelativePositions;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

public class LanternEntity implements Entity, AbstractDataHolder, AbstractPropertyHolder {

    @SuppressWarnings("unused")
    private static boolean bypassEntityTypeLookup;

    // The unique id of this entity
    private final UUID uniqueId;

    // The entity type of this entity
    private final LanternEntityType entityType;

    // The random object of this entity
    private final Random random = new Random();

    // The raw value map
    private final Map<Key<?>, KeyRegistration> rawValueMap = new HashMap<>();
    private final Map<Class<?>, DataManipulator<?, ?>> rawAdditionalManipulators = new ConcurrentHashMap<>();

    // The world this entity is located in, may be null
    private LanternWorld world;

    // The position of the entity
    private Vector3d position = Vector3d.ZERO;

    // The rotation of the entity
    private Vector3d rotation = Vector3d.ZERO;

    // The scale of the entity
    private Vector3d scale = Vector3d.ONE;

    /**
     * The entity protocol type of this entity.
     */
    @Nullable private EntityProtocolType<?> entityProtocolType;

    /**
     * The state of the removal of this entity.
     */
    @Nullable private RemoveState removeState;

    private boolean onGround;

    @Nullable private volatile Vector3i lastChunkCoords;

    /**
     * The base of the {@link AABB} of this entity.
     */
    @Nullable private AABB boundingBoxBase;
    @Nullable private AABB boundingBox;

    @Nullable private volatile UUID creator;
    @Nullable private volatile UUID notifier;

    @Nullable private LanternEntity vehicle;
    private final List<LanternEntity> passengers = new ArrayList<>();

    public enum RemoveState {
        /**
         * The entity was destroyed through the {@link #remove()}
         * method. Will not be respawned in any case.
         */
        DESTROYED,
        /**
         * The entity was removed due chunk unloading. It will appear
         * as "removed", but it is basicly just unloaded.
         */
        CHUNK_UNLOAD,
    }

    public LanternEntity(UUID uniqueId) {
        this.uniqueId = uniqueId;
        if (!bypassEntityTypeLookup) {
            this.entityType = (LanternEntityType) EntityTypeRegistryModule.get().getByClass(getClass()).orElseThrow(
                    () -> new IllegalStateException("Every entity class should be registered as a EntityType."));
        } else {
            //noinspection ConstantConditions
            this.entityType = null;
        }
        registerKeys();
    }

    @Override
    public void registerKeys() {
        registerKey(Keys.DISPLAY_NAME, Text.EMPTY);
        registerKey(Keys.CUSTOM_NAME_VISIBLE, true);
        registerKey(Keys.VELOCITY, Vector3d.ZERO).notRemovable();
        registerKey(Keys.FIRE_TICKS, 0).notRemovable();
        registerKey(Keys.FALL_DISTANCE, 0f).notRemovable();
        registerKey(Keys.GLOWING, false).notRemovable();
        registerKey(LanternKeys.INVULNERABLE, false).notRemovable();
        registerKey(LanternKeys.PORTAL_COOLDOWN_TICKS, 0).notRemovable();
    }

    /**
     * Gets the {@link Direction} that the entity is looking.
     *
     * @param division The division
     * @return The direction
     */
    public Direction getDirection(Direction.Division division) {
        return Direction.getClosest(getDirectionVector(), division);
    }

    public Vector3d getDirectionVector() {
        final Vector3d rotation = this instanceof Living ? ((Living) this).getHeadRotation() : this.rotation;
        // Invert the x direction because west and east are swapped
        return Quaternions.fromAxesAnglesDeg(rotation.mul(1, -1, 1)).getDirection();
    }

    public Vector3d getHorizontalDirectionVector() {
        final Vector3d rotation = this instanceof Living ? ((Living) this).getHeadRotation() : this.rotation;
        // Invert the x direction because west and east are swapped
        return Quaternions.fromAxesAnglesDeg(rotation.mul(0, 1, 0)).getDirection().mul(-1, 1, 1);
    }

    /**
     * Gets the {@link Direction} that the entity is looking in the horizontal plane.
     *
     * @param division The division
     * @return The direction
     */
    public Direction getHorizontalDirection(Direction.Division division) {
        return Direction.getClosest(getHorizontalDirectionVector(), division);
    }

    @Nullable
    public EntityProtocolType<?> getEntityProtocolType() {
        return this.entityProtocolType;
    }

    public void setEntityProtocolType(@Nullable EntityProtocolType<?> entityProtocolType) {
        if (entityProtocolType != null) {
            checkArgument(entityProtocolType.getEntityType().isInstance(this),
                    "The protocol type %s is not applicable to this entity.");
        }
        this.entityProtocolType = entityProtocolType;
    }

    @Override
    public boolean isRemoved() {
        return this.removeState != null;
    }

    @Override
    public void remove() {
        if (!isRemoved()) {
            remove(RemoveState.DESTROYED);
        }
    }

    @Nullable
    public RemoveState getRemoveState() {
        return this.removeState;
    }

    public void remove(RemoveState removeState) {
        checkNotNull(removeState, "removeState");
        this.removeState = removeState;
        if (removeState == RemoveState.DESTROYED) {
            setVehicle(null);
            clearPassengers();
        }
    }

    public void resurrect() {
        checkArgument(this.removeState != RemoveState.DESTROYED, "A destroyed entity cannot be resurrected/respawned.");
        this.removeState = null;
    }

    @Nullable
    public Vector3i getLastChunkSectionCoords() {
        return this.lastChunkCoords;
    }

    public void setLastChunkCoords(@Nullable Vector3i coords) {
        this.lastChunkCoords = coords;
    }

    @Override
    public boolean isOnGround() {
        return this.onGround;
    }

    /**
     * Sets the on ground state of this entity.
     *
     * @param onGround The on ground state
     */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public void setBoundingBoxBase(@Nullable AABB boundingBox) {
        this.boundingBoxBase = boundingBox;
        this.boundingBox = null;
    }

    @Override
    public Optional<AABB> getBoundingBox() {
        AABB boundingBox = this.boundingBox;
        if (boundingBox == null && this.boundingBoxBase != null) {
            boundingBox = this.boundingBoxBase.offset(this.position);
            this.boundingBox = boundingBox;
        }
        return Optional.ofNullable(boundingBox);
    }

    @Override
    public Map<Key<?>, KeyRegistration> getRawValueMap() {
        return this.rawValueMap;
    }

    @Override
    public Map<Class<?>, DataManipulator<?, ?>> getRawAdditionalContainers() {
        return this.rawAdditionalManipulators;
    }

    @Override
    public boolean validateRawData(DataView dataView) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setRawData(DataView dataView) throws InvalidDataException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityType getType() {
        return this.entityType;
    }

    @Override
    public LanternWorld getWorld() {
        return this.world;
    }

    protected void setWorld(@Nullable LanternWorld world) {
        this.world = world;
    }

    protected void setRawPosition(Vector3d position) {
        this.position = checkNotNull(position, "position");
        this.boundingBox = null;
    }

    protected void setRawRotation(Vector3d rotation) {
        this.rotation = checkNotNull(rotation, "rotation");
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public void setPosition(Vector3d position) {
        setRawPosition(position);
    }

    public boolean setPositionAndWorld(World world, Vector3d position) {
        setRawPosition(position);
        setWorld((LanternWorld) world);
        // TODO: Events
        return true;
    }

    @Override
    public Location<World> getLocation() {
        checkState(this.world != null, "This entity doesn't have a world.");
        return new Location<>(this.world, this.position);
    }

    @Override
    public boolean setLocation(Location<World> location) {
        checkNotNull(location, "location");
        return setPositionAndWorld(location.getExtent(), location.getPosition());
    }

    @Override
    public Vector3d getScale() {
        return this.scale;
    }

    @Override
    public void setScale(Vector3d scale) {
        this.scale = checkNotNull(scale, "scale");
    }

    @Override
    public Vector3d getRotation() {
        return this.rotation;
    }

    @Override
    public void setRotation(Vector3d rotation) {
        setRawRotation(rotation);
    }

    @Override
    public boolean transferToWorld(World world, Vector3d position) {
        return setPositionAndWorld(checkNotNull(world, "world"), position);
    }

    @Override
    public Transform<World> getTransform() {
        return new Transform<>(this.world, this.position, this.rotation);
    }

    @Override
    public boolean setTransform(Transform<World> transform) {
        checkNotNull(transform, "transform");
        setLocationAndRotation(transform.getLocation(), transform.getRotation());
        setScale(transform.getScale());
        // TODO: Events
        return true;
    }

    @Override
    public boolean setLocationAndRotation(Location<World> location, Vector3d rotation) {
        checkNotNull(location, "location");
        checkNotNull(rotation, "rotation");

        setWorld((LanternWorld) location.getExtent());
        setRawPosition(location.getPosition());
        setRawRotation(rotation);
        // TODO: Events
        return true;
    }

    @Override
    public boolean setLocationAndRotation(Location<World> location, Vector3d rotation, EnumSet<RelativePositions> relativePositions) {
        checkNotNull(location, "location");
        checkNotNull(rotation, "rotation");
        checkNotNull(relativePositions, "relativePositions");

        final World world = location.getExtent();
        final Vector3d pos = location.getPosition();

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        double pitch = rotation.getX();
        double yaw = rotation.getY();
        double roll = rotation.getZ();

        if (relativePositions.contains(RelativePositions.X)) {
            x += this.position.getX();
        }
        if (relativePositions.contains(RelativePositions.Y)) {
            y += this.position.getY();
        }
        if (relativePositions.contains(RelativePositions.Z)) {
            z += this.position.getZ();
        }
        if (relativePositions.contains(RelativePositions.PITCH)) {
            pitch += this.rotation.getX();
        }
        if (relativePositions.contains(RelativePositions.YAW)) {
            yaw += this.rotation.getY();
        }
        // TODO: No relative roll?

        setWorld((LanternWorld) world);
        setRawPosition(new Vector3d(x, y, z));
        setRawRotation(new Vector3d(pitch, yaw, roll));

        // TODO: Events
        return true;
    }

    @Override
    public List<Entity> getPassengers() {
        synchronized (this.passengers) {
            return ImmutableList.copyOf(this.passengers);
        }
    }

    @Override
    public boolean hasPassenger(Entity entity) {
        checkNotNull(entity, "entity");
        synchronized (this.passengers) {
            //noinspection SuspiciousMethodCalls
            return this.passengers.contains(entity);
        }
    }

    @Override
    public boolean addPassenger(Entity entity) {
        checkNotNull(entity, "entity");
        final LanternEntity entity1 = (LanternEntity) entity;
        return entity1.getVehicle0() == null && entity1.setVehicle(this);
    }

    @Override
    public void removePassenger(Entity entity) {
        checkNotNull(entity, "entity");
        final LanternEntity entity1 = (LanternEntity) entity;
        if (entity1.getVehicle0() != this) {
            return;
        }
        entity1.setVehicle(null);
    }

    @Override
    public void clearPassengers() {
        synchronized (this.passengers) {
            for (LanternEntity passenger : new ArrayList<>(this.passengers)) {
                passenger.setVehicle(null);
            }
        }
    }

    @Override
    public Optional<Entity> getVehicle() {
        synchronized (this.passengers) {
            return Optional.ofNullable(this.vehicle);
        }
    }

    @Override
    public boolean setVehicle(@Nullable Entity entity) {
        synchronized (this.passengers) {
            if (this.vehicle == entity) {
                return false;
            }
            if (this.vehicle != null) {
                this.vehicle.removePassenger0(this);
            }
            this.vehicle = (LanternEntity) entity;
            if (this.vehicle != null) {
                this.vehicle.addPassenger0(this);
            }
            return true;
        }
    }

    private void removePassenger0(LanternEntity passenger) {
        synchronized (this.passengers) {
            this.passengers.remove(passenger);
        }
    }

    private void addPassenger0(LanternEntity passenger) {
        synchronized (this.passengers) {
            int index = -1;
            if (passenger instanceof LanternPlayer) {
                do {
                    index++;
                } while (index < this.passengers.size() && this.passengers.get(index) instanceof LanternPlayer);
            }
            if (index == -1) {
                this.passengers.add(passenger);
            } else {
                this.passengers.add(index, passenger);
            }
        }
    }

    @Override
    public LanternEntity getBaseVehicle() {
        synchronized (this.passengers) {
            LanternEntity lastEntity = this;
            while (true) {
                final LanternEntity entity = lastEntity.getVehicle0();
                if (entity == null) {
                    return lastEntity;
                }
                lastEntity = entity;
            }
        }
    }

    @Nullable
    private LanternEntity getVehicle0() {
        synchronized (this.passengers) {
            return this.vehicle;
        }
    }

    @Override
    public boolean isLoaded() {
        return this.removeState != RemoveState.CHUNK_UNLOAD;
    }

    /**
     * Pulses the entity.
     */
    public void pulse() {
        synchronized (this.passengers) {
            if (this.vehicle != null) {
                this.position = this.vehicle.getPosition();
            }
        }
    }

    @Override
    public EntitySnapshot createSnapshot() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public boolean damage(double damage, DamageSource damageSource, Cause cause) {
        final Optional<Double> optHealth = get(Keys.HEALTH);
        if (!optHealth.isPresent()) {
            return false;
        }
        // TODO: Damage modifiers, etc.
        final org.spongepowered.api.event.entity.DamageEntityEvent event = SpongeEventFactory.createDamageEntityEvent(
                cause, new ArrayList<>(), this, damage);
        if (event.isCancelled()) {
            return false;
        }
        damage = event.getFinalDamage();
        if (damage > 0) {
            final double health = optHealth.get() - damage;
            offer(Keys.HEALTH, health);
            if (health <= 0.0) {
                // TODO: Notify stuff and kill the entity
            }
            return true;
        }
        return false;
    }

    @Override
    public Optional<UUID> getCreator() {
        return Optional.ofNullable(this.creator);
    }

    @Override
    public Optional<UUID> getNotifier() {
        return Optional.ofNullable(this.notifier);
    }

    @Override
    public void setCreator(@Nullable UUID uuid) {
        this.creator = uuid;
    }

    @Override
    public void setNotifier(@Nullable UUID uuid) {
        this.notifier = uuid;
    }

    @Override
    public Translation getTranslation() {
        final Optional<Text> displayName = this.get(Keys.DISPLAY_NAME);
        if (displayName.isPresent()) {
            return new FixedTranslation(LanternTexts.toLegacy(displayName.get()));
        }
        return this.entityType.getTranslation();
    }

    @Override
    public DataHolder copy() {
        return null;
    }

    @Override
    public EntityArchetype createArchetype() {
        return null;
    }

    /**
     * Triggers the {@link EntityEvent} for this entity.
     *
     * @param event The event
     */
    public void triggerEvent(EntityEvent event) {
        getWorld().getEntityProtocolManager().triggerEvent(this, event);
    }
}
