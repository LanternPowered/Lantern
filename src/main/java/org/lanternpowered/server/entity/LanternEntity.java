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
package org.lanternpowered.server.entity;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.component.BaseComponentHolder;
import org.lanternpowered.server.component.misc.Health;
import org.lanternpowered.server.data.AbstractDataHolder;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.util.IdAllocator;
import org.lanternpowered.server.data.value.KeyRegistration;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.RelativePositions;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

@NonnullByDefault
public class LanternEntity extends BaseComponentHolder implements Entity, AbstractDataHolder, AbstractPropertyHolder {

    private static final IdAllocator idAllocator = new IdAllocator();

    public static IdAllocator getIdAllocator() {
        return idAllocator;
    }

    // The entity id that will be used for the client
    private int entityId;

    // The unique id of this entity
    private final UUID uniqueId;

    // The random object of this entity
    private final Random random = new Random();

    // The raw value map
    private final Map<Key<?>, KeyRegistration> rawValueMap = new HashMap<>();

    // The world this entity is located in, may be null
    private LanternWorld world;

    // The position of the entity
    private Vector3d position = Vector3d.ZERO;

    // The rotation of the entity
    private Vector3d rotation = Vector3d.ZERO;

    // The scale of the entity
    private Vector3d scale = Vector3d.ONE;

    private boolean onGround;

    public LanternEntity(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.registerKeys();
    }

    public LanternEntity() {
        this(UUID.randomUUID());
    }

    @Override
    public void registerKeys() {
        this.registerKey(Keys.DISPLAY_NAME, null);
        this.registerKey(Keys.CUSTOM_NAME_VISIBLE, true);
        this.registerKey(Keys.VELOCITY, Vector3d.ZERO);
        this.registerKey(Keys.FIRE_TICKS, 0).nonRemovableAttachedValueProcessor();
        this.registerKey(Keys.FALL_DISTANCE, 0f).nonRemovableAttachedValueProcessor();
        this.registerKey(LanternKeys.INVULNERABLE, false).nonRemovableAttachedValueProcessor();
        this.registerKey(LanternKeys.PORTAL_COOLDOWN_TICKS, 0).nonRemovableAttachedValueProcessor();
    }

    /**
     * Gets the internal entity id.
     *
     * @return The entity id
     */
    public int getEntityId() {
        return this.entityId;
    }

    /**
     * Sets the internal entity id, this will be
     * used for the client.
     *
     * @param entityId The entity id
     */
    public void setEntityId(int entityId) {
        this.entityId = entityId;
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

    @Override
    public Map<Key<?>, KeyRegistration> getRawValueMap() {
        return this.rawValueMap;
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
        // TODO Auto-generated method stub
        return null;
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
    }

    protected void setRawRotation(Vector3d rotation) {
        this.rotation = checkNotNull(rotation, "rotation");
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public void setPosition(Vector3d position) {
        this.setRawPosition(position);
    }

    public boolean setPositionAndWorld(World world, Vector3d position) {
        this.setRawPosition(position);
        this.setWorld((LanternWorld) world);
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
        return this.setPositionAndWorld(location.getExtent(), location.getPosition());
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
        this.setRawRotation(rotation);
    }

    @Override
    public boolean transferToWorld(World world, Vector3d position) {
        return this.setPositionAndWorld(checkNotNull(world, "world"), position);
    }

    @Override
    public Optional<AABB> getBoundingBox() {
        return Optional.empty();
    }

    @Override
    public List<Entity> getPassengers() {
        return Collections.emptyList();
    }

    @Override
    public DataTransactionResult addPassenger(Entity entity) {
        return DataTransactionResult.failNoData();
    }

    @Override
    public DataTransactionResult removePassenger(Entity entity) {
        return DataTransactionResult.failNoData();
    }

    @Override
    public DataTransactionResult clearPassengers() {
        return DataTransactionResult.failNoData();
    }

    @Override
    public Transform<World> getTransform() {
        return new Transform<>(this.world, this.position, this.rotation);
    }

    @Override
    public boolean setTransform(Transform<World> transform) {
        checkNotNull(transform, "transform");
        this.setLocationAndRotation(transform.getLocation(), transform.getRotation());
        this.setScale(transform.getScale());
        // TODO: Events
        return true;
    }

    @Override
    public boolean setLocationAndRotation(Location<World> location, Vector3d rotation) {
        checkNotNull(location, "location");
        checkNotNull(rotation, "rotation");

        this.setWorld((LanternWorld) location.getExtent());
        this.setRawPosition(location.getPosition());
        this.setRawRotation(rotation);
        // TODO: Events
        return true;
    }

    @Override
    public boolean setLocationAndRotation(Location<World> location, Vector3d rotation, EnumSet<RelativePositions> relativePositions) {
        checkNotNull(location, "location");
        checkNotNull(rotation, "rotation");
        checkNotNull(relativePositions, "relativePositions");

        World world = location.getExtent();
        Vector3d pos = location.getPosition();

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

        this.setWorld((LanternWorld) world);
        this.setRawPosition(new Vector3d(x, y, z));
        this.setRawRotation(new Vector3d(pitch, yaw, roll));

        // TODO: Events
        return true;
    }

    @Override
    public Optional<Entity> getVehicle() {
        return Optional.empty();
    }

    @Override
    public DataTransactionResult setVehicle(@Nullable Entity entity) {
        return DataTransactionResult.failNoData();
    }

    @Override
    public Entity getBaseVehicle() {
        return this;
    }

    @Override
    public boolean isRemoved() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isLoaded() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void remove() {
        // TODO Auto-generated method stub
        
    }

    /**
     * Pulses the entity.
     */
    protected void pulse() {

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
        Optional<Health> health = this.getComponent(Health.class);
        if (health.isPresent()) {
            return health.get().damage(damage, damageSource, cause);
        }
        return false;
    }

    @Override
    public Optional<UUID> getCreator() {
        return Optional.empty();
    }

    @Override
    public Optional<UUID> getNotifier() {
        return Optional.empty();
    }

    @Override
    public void setCreator(@Nullable UUID uuid) {

    }

    @Override
    public void setNotifier(@Nullable UUID uuid) {

    }

    @Override
    public Translation getTranslation() {
        return null;
    }

    @Override
    public DataHolder copy() {
        return null;
    }

    @Override
    public EntityArchetype createArchetype() {
        return null;
    }
}
