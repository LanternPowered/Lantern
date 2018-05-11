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
package org.lanternpowered.server.world;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

public final class WeakWorldReferencedLocation {

    private final WeakWorldReference world;

    // Lazily loaded position
    @Nullable private Vector3d position;
    // Lazily loaded block position
    @Nullable private Vector3i blockPosition;
    // Lazily loaded location
    @Nullable private Location<World> location;

    /**
     * Constructs a new {@link WeakWorldReferencedLocation} with
     * the given {@link Location}.
     *
     * @param location The location
     */
    public WeakWorldReferencedLocation(Location<World> location) {
        this(location.getExtent(), location.getPosition());
        // Store the provided location so it can be reused
        this.location = location;
    }

    /**
     * Constructs a new {@link WeakWorldReferencedLocation} with
     * the given world {@link UUID} and position.
     *
     * @param worldUniqueId The world unique id
     * @param position The position
     */
    public WeakWorldReferencedLocation(UUID worldUniqueId, Vector3i position) {
        this(new WeakWorldReference(worldUniqueId), position);
    }

    /**
     * Constructs a new {@link WeakWorldReferencedLocation} with
     * the given world {@link UUID} and position.
     *
     * @param worldUniqueId The world unique id
     * @param position The position
     */
    public WeakWorldReferencedLocation(UUID worldUniqueId, Vector3d position) {
        this(new WeakWorldReference(worldUniqueId), position);
    }

    /**
     * Constructs a new {@link WeakWorldReferencedLocation} with
     * the given {@link World} and position.
     *
     * @param world The world
     * @param position The position
     */
    public WeakWorldReferencedLocation(World world, Vector3i position) {
        this(new WeakWorldReference(world), position);
    }

    /**
     * Constructs a new {@link WeakWorldReferencedLocation} with
     * the given {@link World} and position.
     *
     * @param world The world
     * @param position The position
     */
    public WeakWorldReferencedLocation(World world, Vector3d position) {
        this(new WeakWorldReference(world), position);
    }

    /**
     * Constructs a new {@link WeakWorldReferencedLocation} with
     * the given {@link WeakWorldReference} and position.
     *
     * @param world The world reference
     * @param position The position
     */
    public WeakWorldReferencedLocation(WeakWorldReference world, Vector3i position) {
        checkNotNull(world, "world");
        checkNotNull(position, "position");
        this.blockPosition = position;
        this.world = world;
    }

    /**
     * Constructs a new {@link WeakWorldReferencedLocation} with
     * the given {@link WeakWorldReference} and position.
     *
     * @param world The world reference
     * @param position The position
     */
    public WeakWorldReferencedLocation(WeakWorldReference world, Vector3d position) {
        checkNotNull(world, "world");
        checkNotNull(position, "position");
        this.position = position;
        this.world = world;
    }

    /**
     * Gets the {@link WeakWorldReference} that
     * points to the {@link World}.
     *
     * @return The world reference
     */
    public WeakWorldReference getWorld() {
        return this.world;
    }

    /**
     * Gets the position.
     *
     * @return The position
     */
    public Vector3d getPosition() {
        if (this.position == null) {
            checkState(this.blockPosition != null);
            this.position = this.blockPosition.toDouble();
        }
        return this.position;
    }

    /**
     * Gets the block position.
     *
     * @return The block position
     */
    public Vector3i getBlockPosition() {
        if (this.blockPosition == null) {
            checkState(this.position != null);
            this.blockPosition = this.position.toInt();
        }
        return this.blockPosition;
    }

    /**
     * Gets this {@link WeakWorldReferencedLocation} as a regular {@link Location}.
     *
     * @return The location
     */
    public Optional<Location<World>> asLocation() {
        final Optional<World> world = this.world.getWorld();
        if (!world.isPresent()) {
            return Optional.empty();
        }
        if (this.location != null) {
            try {
                this.location.getExtent();
            } catch (IllegalStateException ignored) {
                // The internal world reference expired, create a new location
                this.location = null;
            }
        }
        if (this.location == null) {
            if (this.position != null) {
                this.location = new Location<>(world.get(), this.position);
            } else {
                checkState(this.blockPosition != null);
                this.location = new Location<>(world.get(), this.blockPosition);
            }
        }
        return Optional.of(this.location);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("world", this.world)
                .add("position", this.position == null ? this.blockPosition : this.position)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WeakWorldReferencedLocation)) {
            return false;
        }
        final WeakWorldReferencedLocation other = (WeakWorldReferencedLocation) obj;
        return other.world.equals(this.world) && other.getPosition().equals(getPosition());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.world, getPosition());
    }
}
