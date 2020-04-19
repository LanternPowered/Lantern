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

import org.lanternpowered.server.data.DataQueries;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.util.Optional;

public final class LanternProfilePropertyBuilder extends AbstractDataBuilder<ProfileProperty> {

    public LanternProfilePropertyBuilder() {
        super(ProfileProperty.class, 1);
    }

    @Override
    protected Optional<ProfileProperty> buildContent(DataView container) throws InvalidDataException {
        if (!(container.contains(DataQueries.PROPERTY_NAME) && container.contains(DataQueries.PROPERTY_VALUE))) {
            return Optional.empty();
        }

        final String name = container.getString(DataQueries.PROPERTY_NAME).get();
        final String value = container.getString(DataQueries.PROPERTY_VALUE).get();
        final Optional<String> signature = container.getString(DataQueries.PROPERTY_SIGNATURE);
        return Optional.of(Sponge.getServer().getGameProfileManager()
                .createProfileProperty(name, value, signature.orElse(null)));
    }
}
