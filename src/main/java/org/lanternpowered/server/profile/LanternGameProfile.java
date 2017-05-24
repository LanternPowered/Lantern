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
package org.lanternpowered.server.profile;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

@ConfigSerializable
public final class LanternGameProfile implements GameProfile {

    public static final UUID UNKNOWN_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final LanternGameProfile UNKNOWN = new LanternGameProfile(UNKNOWN_UUID, null);

    private static final DataQuery NAME = DataQuery.of("Name");
    private static final DataQuery UNIQUE_ID = DataQuery.of("UniqueId");
    private static final DataQuery PROPERTIES = DataQuery.of("Properties");
    private static final DataQuery VALUE = DataQuery.of("Value");
    private static final DataQuery SIGNATURE = DataQuery.of("Signature");

    @Setting("properties")
    private Multimap<String, LanternProfileProperty> properties;

    @Setting("uniqueId")
    private UUID uniqueId;

    @Nullable
    @Setting("name")
    private String name;

    protected LanternGameProfile() {
        this.properties = LinkedHashMultimap.create();
    }

    public LanternGameProfile(UUID uniqueId, @Nullable String name) {
        this(uniqueId, name, LinkedHashMultimap.create());
    }

    @SuppressWarnings("unchecked")
    public LanternGameProfile(UUID uniqueId, @Nullable String name, Multimap<String, ProfileProperty> properties) {
        this.properties = (Multimap) checkNotNull(properties, "properties");
        this.uniqueId = checkNotNull(uniqueId, "uniqueId");
        this.name = name;
    }

    /**
     * Creates a copy of this game profile.
     *
     * @return The copy
     */
    public LanternGameProfile copy() {
        return new LanternGameProfile(this.uniqueId, this.name, LinkedHashMultimap.create(this.properties));
    }

    /**
     * Creates a copy of this game profile without all the properties.
     *
     * @return The copy
     */
    public LanternGameProfile copyWithoutProperties() {
        return new LanternGameProfile(this.uniqueId, this.name);
    }

    /**
     * Creates a new game profile without all the properties.
     * 
     * @return The new game profile
     */
    public LanternGameProfile withoutProperties() {
        if (this.properties.isEmpty()) {
            return this;
        }
        return new LanternGameProfile(this.uniqueId, this.name);
    }

    @Override
    public UUID getUniqueId() {
        //noinspection ConstantConditions
        checkState(this.uniqueId != null, "Invalid game profile, the unique id is null!");
        return this.uniqueId;
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        final DataContainer container = DataContainer.createNew()
                .set(UNIQUE_ID, this.uniqueId.toString());
        if (this.name != null) {
            container.set(NAME, this.name);
        }
        if (!this.properties.isEmpty()) {
            final DataContainer propertiesMap = DataContainer.createNew();
            for (String key : this.properties.keySet()) {
                final List<DataContainer> entries = Lists.newArrayList();
                for (ProfileProperty property : this.properties.get(key)) {
                    final DataContainer entry = DataContainer.createNew()
                            .set(VALUE, property.getValue());
                    property.getSignature().ifPresent(signature -> entry.set(SIGNATURE, signature));
                    entries.add(entry);
                }
                propertiesMap.set(DataQuery.of(key), entries);
            }
            container.set(PROPERTIES, propertiesMap);
        }
        return container;
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(this.name);
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Multimap<String, ProfileProperty> getPropertyMap() {
        return (Multimap) this.properties;
    }

    @Override
    public boolean isFilled() {
        return this.name != null;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        final LanternGameProfile o = (LanternGameProfile) other;
        return Objects.equals(this.name, o.name) && this.uniqueId.equals(o.uniqueId) &&
                this.properties.equals(o.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uniqueId, this.name, this.properties);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("uniqueId", this.uniqueId)
                .add("name", this.name)
                .add("properties", this.properties.isEmpty() ? null : Joiner.on(", ").join(this.properties.values().stream()
                        .map(LanternProfileProperty::toString).collect(Collectors.toList())))
                .toString();
    }

    /*
    public static class LanternDataBuilder implements DataBuilder<GameProfile> {

        // Why use both the optional and data exception?
        @Override
        public Optional<GameProfile> build(DataView container) throws InvalidDataException {
            String uniqueId = container.getString(UNIQUE_ID).orElse(null);
            if (uniqueId == null) {
                throw new InvalidDataException("UniqueId is missing!");
            }
            UUID uniqueId0;
            try {
                uniqueId0 = UUID.fromString(uniqueId);
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Unknown uniqueId format!", e);
            }
            String name = container.getString(NAME).orElse(null);
            if (name == null) {
                throw new InvalidDataException("Name is missing!");
            }
            List<DataView> views = container.getViewList(PROPERTIES).orElse(null);
            List<Property> properties;
            if (views != null && !views.isEmpty()) {
                properties = Lists.newArrayListWithCapacity(views.size());
                for (DataView view : views) {
                    if (view.contains(NAME) && view.contains(VALUE)) {
                        properties.add(new Property(view.getString(NAME).get(), view.getString(VALUE).get(),
                                view.getString(SIGNATURE).orElse(null)));
                    }
                }
            } else {
                properties = Lists.newArrayList();
            }
            return Optional.of(new LanternGameProfile(uniqueId0, name, properties));
        }
    }*/
}
