package org.lanternpowered.server.entity;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Nullable;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.matrix.Matrix4d;
import com.flowpowered.math.vector.Vector3d;

public class LanternTransform <E extends Extent> implements Transform<E> {

    @Nullable private E extent;

    private Vector3d position;
    private Vector3d rotation;
    private Vector3d scale;

    @Nullable private Location<E> location = null;
    @Nullable private Quaterniond rotationQuaternion = null;

    public LanternTransform() {
        this(null, Vector3d.ZERO, Vector3d.ZERO, Vector3d.ONE);
    }

    public LanternTransform(@Nullable E extent, Vector3d position) {
        this(extent, position, Vector3d.ZERO, Vector3d.ONE);
    }

    public LanternTransform(@Nullable E extent, Vector3d position, Vector3d rotation, Vector3d scale) {
        this.position = checkNotNull(position, "position");
        this.rotation = checkNotNull(rotation, "rotation");
        this.scale = checkNotNull(scale, "scale");
        this.extent = extent;
    }

    @Override
    public Location<E> getLocation() {
        checkState(extent != null, "Transform has no extent");
        if (this.location == null) {
            this.location = new Location<E>(this.extent, this.position);
        }
        return this.location;
    }

    @Override
    public Transform<E> setLocation(Location<E> location) {
        checkNotNull(location, "location");
        this.setExtent(location.getExtent());
        this.setPosition(location.getPosition());
        return this;
    }

    @Override
    public E getExtent() {
        checkState(extent != null, "Transform has no extent");
        return this.extent;
    }

    @Override
    public Transform<E> setExtent(E extent) {
        checkNotNull(extent, "extent");
        this.extent = extent;
        this.location = null;
        return this;
    }

    @Override
    public Vector3d getPosition() {
        return this.position;
    }

    @Override
    public Transform<E> setPosition(Vector3d position) {
        checkNotNull(position, "position");
        this.position = position;
        this.location = null;
        return this;
    }

    @Override
    public Vector3d getRotation() {
        return this.rotation;
    }

    @Override
    public Transform<E> setRotation(Vector3d rotation) {
        checkNotNull(rotation, "rotation");
        this.rotation = rotation;
        this.rotationQuaternion = null;
        return this;
    }

    @Override
    public Quaterniond getRotationAsQuaternion() {
        if (this.rotationQuaternion == null) {
            this.rotationQuaternion = Quaterniond.fromAxesAnglesDeg(
                    this.rotation.getX(), -this.rotation.getY(), this.rotation.getZ());
        }
        return this.rotationQuaternion;
    }

    @Override
    public Transform<E> setRotation(Quaterniond rotation) {
        checkNotNull(rotation, "rotation");
        final Vector3d axesAngles = rotation.getAxesAnglesDeg();
        this.rotation = new Vector3d(axesAngles.getX(), -axesAngles.getY(), axesAngles.getZ());
        this.rotationQuaternion = rotation;
        return this;
    }

    @Override
    public double getPitch() {
        return this.rotation.getX();
    }

    @Override
    public double getYaw() {
        return this.rotation.getY();
    }

    @Override
    public double getRoll() {
        return this.rotation.getZ();
    }

    @Override
    public Vector3d getScale() {
        return this.scale;
    }

    @Override
    public Transform<E> setScale(Vector3d scale) {
        checkNotNull(scale, "scale");
        this.scale = scale;
        return this;
    }

    @Override
    public Transform<E> add(Transform<E> other) {
        checkNotNull(other, "other");
        this.addTranslation(other.getPosition());
        this.addRotation(other.getRotationAsQuaternion());
        this.addScale(other.getScale());
        return this;
    }

    @Override
    public Transform<E> addTranslation(Vector3d translation) {
        checkNotNull(translation, "translation");
        this.setPosition(this.getPosition().add(translation));
        return this;
    }

    @Override
    public Transform<E> addRotation(Vector3d rotation) {
        checkNotNull(rotation, "rotation");
        return this.addRotation(Quaterniond.fromAxesAnglesDeg(
                rotation.getX(), -rotation.getY(), rotation.getZ()));
    }

    @Override
    public Transform<E> addRotation(Quaterniond rotation) {
        checkNotNull(rotation, "rotation");
        this.setRotation(rotation.mul(this.getRotationAsQuaternion()));
        return this;
    }

    @Override
    public Transform<E> addScale(Vector3d scale) {
        checkNotNull(scale, "scale");
        this.setScale(this.getScale().mul(scale));
        return this;
    }

    @Override
    public Matrix4d toMatrix() {
        return Matrix4d.createScaling(this.getScale().toVector4(1)).rotate(this.getRotationAsQuaternion())
                .translate(this.getPosition());
    }

    @Override
    public boolean isValid() {
        return this.extent != null && this.extent.isLoaded();
    }

    @Override
    public String toString() {
        return "Transform{location=" + this.getLocation() + ", rotation=" + this.getRotation() +
                ", scale=" + this.getScale() + '}';
    }
}
