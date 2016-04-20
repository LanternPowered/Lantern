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
import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.component.BaseComponentHolder;
import org.lanternpowered.server.component.misc.Health;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.util.IdAllocator;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.RelativePositions;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

@NonnullByDefault
public class LanternEntity extends BaseComponentHolder implements Entity, AbstractPropertyHolder {

    private static final IdAllocator idAllocator = new IdAllocator();

    public static IdAllocator getIdAllocator() {
        return idAllocator;
    }

    protected final static float EPSILON = 1.0e-004f;

    // The entity id that will be used for the client
    private int entityId;

    // The unique id of this entity
    private final UUID uniqueId;

    // The random object of this entity
    private final Random random = new Random();

    // The world this entity is located in, may be null
    private LanternWorld world;

    // The position of the entity
    private Vector3d position = Vector3d.ZERO;

    // The rotation of the entity
    private Vector3d rotation = Vector3d.ZERO;

    protected float motionX;
    protected float motionY;
    protected float motionZ;

    public LanternEntity(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public LanternEntity() {
        this(UUID.randomUUID());
    }

    public int getEntityId() {
        return this.entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public boolean validateRawData(DataContainer container) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setRawData(DataContainer container) throws InvalidDataException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getContentVersion() {
        return 0;
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

    public void setPositionAndWorld(World world, Vector3d position) {
        this.setRawPosition(position);
        this.setWorld((LanternWorld) world);
    }

    @Override
    public Location<World> getLocation() {
        checkState(this.world != null, "This entity doesn't have a world.");
        return new Location<>(this.world, this.position);
    }

    @Override
    public void setLocation(Location<World> location) {
        checkNotNull(location, "location");
        this.setPositionAndWorld(location.getExtent(), location.getPosition());
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
    public boolean setLocationSafely(Location<World> location) {
        this.setLocation(location);
        // TODO: Will be removed in the future
        // TODO: Check whether the location safe is
        return true;
    }

    @Override
    public boolean setLocationAndRotationSafely(Location<World> location, Vector3d rotation) {
        this.setLocationAndRotation(location, rotation);
        // TODO: Will be removed in the future
        // TODO: Check whether the location safe is
        return true;
    }

    @Override
    public boolean transferToWorld(String worldName, Vector3d position) {
        final Optional<World> world = Sponge.getServer().getWorld(checkNotNull(worldName, "worldName"));
        if (!world.isPresent()) {
            return false;
        }
        this.setPositionAndWorld(world.get(), position);
        return true;
    }

    @Override
    public boolean transferToWorld(UUID uuid, Vector3d position) {
        final Optional<World> world = Sponge.getServer().getWorld(checkNotNull(uuid, "uuid"));
        if (!world.isPresent()) {
            return false;
        }
        this.setPositionAndWorld(world.get(), position);
        return true;
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
    public void setTransform(Transform<World> transform) {
        checkNotNull(transform, "transform");
        this.setLocationAndRotation(transform.getLocation(), transform.getRotation());
        this.setScale(transform.getScale());
    }

    @Override
    public void setLocationAndRotation(Location<World> location, Vector3d rotation) {
        checkNotNull(location, "location");
        checkNotNull(rotation, "rotation");

        this.setWorld((LanternWorld) location.getExtent());
        this.setRawPosition(location.getPosition());
        this.setRawRotation(rotation);
    }

    @Override
    public void setLocationAndRotation(Location<World> location, Vector3d rotation, EnumSet<RelativePositions> relativePositions) {
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
    }

    @Override
    public boolean setLocationAndRotationSafely(Location<World> location, Vector3d rotation, EnumSet<RelativePositions> relativePositions) {
        this.setLocationAndRotation(location, rotation, relativePositions);
        // TODO: Check whether the location safe is
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
    public boolean isOnGround() {
        // TODO Auto-generated method stub
        return false;
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
    public <T extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supports(Class<? extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> holderClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <E> DataTransactionResult transform(Key<? extends BaseValue<E>> key, Function<E, E> function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> DataTransactionResult offer(Key<? extends BaseValue<E>> key, E value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> DataTransactionResult offer(BaseValue<E> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(org.spongepowered.api.data.manipulator.DataManipulator<?, ?> valueContainer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(org.spongepowered.api.data.manipulator.DataManipulator<?, ?> valueContainer, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(Iterable<org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> valueContainers) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult
            offer(Iterable<org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> valueContainers, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult remove(Class<? extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> containerClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult remove(BaseValue<?> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult remove(Key<?> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult undo(DataTransactionResult result) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(DataHolder that) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(DataHolder that, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> getContainers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> E getOrNull(Key<? extends BaseValue<E>> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> E getOrElse(Key<? extends BaseValue<E>> key, E defaultValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supports(Key<?> key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(BaseValue<?> baseValue) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DataHolder copy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ImmutableSet<Key<?>> getKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ImmutableSet<ImmutableValue<?>> getValues() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector3d getScale() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setScale(Vector3d scale) {
        // TODO Auto-generated method stub
        
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

}
