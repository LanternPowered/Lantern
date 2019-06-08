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
