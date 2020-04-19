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
package org.lanternpowered.server.profile;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.lanternpowered.server.data.DataQueries;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.util.Objects;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

@ConfigSerializable
public final class LanternProfileProperty implements ProfileProperty {

    public static final LanternProfileProperty EMPTY_TEXTURES = new LanternProfileProperty(ProfileProperty.TEXTURES, "", null);

    @Setting(value = "name")
    private String name;

    @Setting(value = "value")
    private String value;

    @Nullable
    @Setting(value = "signature")
    private String signature;

    private LanternProfileProperty() {
    }

    public LanternProfileProperty(String name, String value, @Nullable String signature) {
        this.value = checkNotNull(value, "value");
        this.name = checkNotNull(name, "name");
        this.signature = signature;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public Optional<String> getSignature() {
        return Optional.ofNullable(this.signature);
    }

    public LanternProfileProperty withoutSignature() {
        return this.signature == null ? this : new LanternProfileProperty(this.name, this.value, null);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        final DataContainer dataContainer = DataContainer.createNew()
                .set(DataQueries.PROPERTY_NAME, this.name)
                .set(DataQueries.PROPERTY_VALUE, this.value);
        if (this.signature != null) {
            dataContainer.set(DataQueries.PROPERTY_SIGNATURE, this.signature);
        }
        return dataContainer;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        final LanternProfileProperty o = (LanternProfileProperty) other;
        return this.name.equals(o.name) && this.value.equals(o.value) && Objects.equals(
                this.signature, o.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.value, this.signature);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", this.name)
                .add("value", this.value)
                .add("signature", this.signature)
                .omitNullValues()
                .toString();
    }

    /**
     * Creates {@link LanternProfileProperty} from the specified {@link JsonObject}.
     *
     * @param jsonObject The json object
     * @return The profile property
     */
    public static LanternProfileProperty createFromJson(JsonObject jsonObject) {
        final String name = jsonObject.get("name").getAsString();
        final String value = jsonObject.get("value").getAsString();
        final String signature = jsonObject.has("signature") ? jsonObject.get("signature").getAsString() : null;
        return new LanternProfileProperty(name, value, signature);
    }

    /**
     * Creates a multimap with {@link LanternProfileProperty}s from the specified {@link JsonArray}.
     *
     * @param jsonArray The json array
     * @return The multimap
     */
    public static Multimap<String, ProfileProperty> createPropertiesMapFromJson(JsonArray jsonArray) {
        final Multimap<String, ProfileProperty> properties = LinkedHashMultimap.create();
        for (int i = 0; i < jsonArray.size(); i++) {
            final ProfileProperty profileProperty = createFromJson(jsonArray.get(i).getAsJsonObject());
            properties.put(profileProperty.getName(), profileProperty);
        }
        return properties;
    }
}
