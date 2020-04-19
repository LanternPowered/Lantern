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

import com.google.common.collect.Multimap;
import org.lanternpowered.server.data.DataQueries;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.util.Optional;
import java.util.UUID;

public class LanternGameProfileBuilder extends AbstractDataBuilder<GameProfile> {

    public LanternGameProfileBuilder() {
        super(GameProfile.class, 1);
    }

    @Override
    protected Optional<GameProfile> buildContent(DataView container) throws InvalidDataException {
        if (!container.contains(DataQueries.USER_UUID)) {
            return Optional.of(LanternGameProfile.UNKNOWN);
        }
        final UUID uuid = getUUIDByString(container.getString(DataQueries.USER_UUID).get());
        if (!container.contains(DataQueries.USER_NAME)) {
            return Optional.of(GameProfile.of(uuid));
        }

        final GameProfile profile = GameProfile.of(uuid, container.getString(DataQueries.USER_NAME).get());
        container.getViewList(DataQueries.PROFILE_PROPERTIES).ifPresent(properties -> {
            final Multimap<String, ProfileProperty> propertyMap = profile.getPropertyMap();
            DataBuilder<ProfileProperty> builder = Sponge.getDataManager().getBuilder(ProfileProperty.class).get();

            for (DataView propertyView : properties) {
                ProfileProperty property = builder.build(propertyView).get();
                propertyMap.put(property.getName(), property);
            }
        });
        return Optional.of(profile);
    }

    private static UUID getUUIDByString(String uuidString) throws InvalidDataException {
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("Invalid UUID string: " + uuidString);
        }
    }
}
