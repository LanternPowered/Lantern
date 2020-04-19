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
package org.lanternpowered.server.entity.living.player;

import org.spongepowered.api.entity.living.player.User;

import java.util.UUID;

interface IUser extends User {

    @Override
    default String getName() {
        return getProfile().getName().get();
    }

    @Override
    default UUID getUniqueId() {
        return getProfile().getUniqueId();
    }

    @Override
    default String getIdentifier() {
        return getProfile().getUniqueId().toString();
    }
}
