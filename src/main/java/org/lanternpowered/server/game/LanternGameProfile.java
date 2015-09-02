package org.lanternpowered.server.game;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.spongepowered.api.GameProfile;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.service.persistence.DataBuilder;
import org.spongepowered.api.service.persistence.InvalidDataException;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class LanternGameProfile implements GameProfile {

    private static final DataQuery NAME = DataQuery.of("Name");
    private static final DataQuery UNIQUE_ID = DataQuery.of("UniqueId");

    private final List<Property> properties;
    private final UUID uniqueId;
    private final String name;

    public LanternGameProfile(UUID uniqueId, String name) {
        this(uniqueId, name, Lists.newArrayList());
    }

    public LanternGameProfile(UUID uniqueId, String name, List<Property> properties) {
        this.properties = properties;
        this.uniqueId = uniqueId;
        this.name = name;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer()
                .set(NAME, this.name)
                .set(UNIQUE_ID, this.uniqueId.toString());
    }

    @Override
    public String getName() {
        return this.name;
    }

    public List<Property> getProperties() {
        return this.properties;
    }

    public static class LanternDataBuilder implements DataBuilder<GameProfile> {

        // Why use both the optional and data exception?
        @Override
        public Optional<GameProfile> build(DataView container) throws InvalidDataException {
            String uniqueId = container.getString(UNIQUE_ID).orNull();
            if (uniqueId == null) {
                throw new InvalidDataException("UniqueId is missing!");
            }
            UUID uniqueId0;
            try {
                uniqueId0 = UUID.fromString(uniqueId);
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Unknown uniqueId format!", e);
            }
            String name = container.getString(NAME).orNull();
            if (name == null) {
                throw new InvalidDataException("Name is missing!");
            }
            return Optional.<GameProfile>of(new LanternGameProfile(uniqueId0, name));
        }
    }

    public static class Property {

        private final String name;
        private final String value;

        @Nullable
        private final String signature;

        public Property(String name, String value, @Nullable String signature) {
            this.signature = signature;
            this.value = value;
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        @Nullable
        public String getSignature() {
            return this.signature;
        }
    }
}
