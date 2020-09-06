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
package org.lanternpowered.api.profile

import org.lanternpowered.api.util.optional.orNull

typealias GameProfile = org.spongepowered.api.profile.GameProfile
typealias ProfileProperty = org.spongepowered.api.profile.property.ProfileProperty

fun GameProfile.copy(): GameProfile {
    val copy = GameProfile.of(this.uniqueId, this.name.orNull())
    for (property in this.propertyMap.values())
        copy.addProperty(property)
    return copy
}

fun GameProfile.copyWithoutProperties(): GameProfile =
        GameProfile.of(this.uniqueId, this.name.orNull())
