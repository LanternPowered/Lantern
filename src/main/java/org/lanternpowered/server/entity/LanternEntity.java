package org.lanternpowered.server.entity;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.persistence.InvalidDataException;
import org.spongepowered.api.util.RelativePositions;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.ImmutableSet;

public class LanternEntity implements Entity {

    protected final static float EPSILON = 1.0e-004f;

    protected double x;
    protected double y;
    protected double z;

    protected float yaw;
    protected float pitch;

    protected float motionX;
    protected float motionY;
    protected float motionZ;

    @Override
    public UUID getUniqueId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Class<T> propertyClass) {
        // TODO Auto-generated method stub
        return null;
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
    public World getWorld() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location<World> getLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLocation(Location<World> location) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLocationAndRotation(Location<World> location, Vector3d rotation) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean setLocationSafely(Location<World> location) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean setLocationAndRotationSafely(Location<World> location, Vector3d rotation) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean transferToWorld(String worldName, Vector3d position) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean transferToWorld(UUID uuid, Vector3d position) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Vector3d getRotation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setRotation(Vector3d rotation) {
        // TODO Auto-generated method stub
        
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
     * Ticks the entity.
     */
    protected void tick() {

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
    public Transform<World> getTransform() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTransform(Transform<World> transform) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLocationAndRotation(Location<World> location, Vector3d rotation, EnumSet<RelativePositions> relativePositions) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean setLocationAndRotationSafely(Location<World> location, Vector3d rotation, EnumSet<RelativePositions> relativePositions) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public EntitySnapshot createSnapshot() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean damage(double damage, Cause cause) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Collection<Property<?, ?>> getApplicableProperties() {
        // TODO Auto-generated method stub
        return null;
    }

}
