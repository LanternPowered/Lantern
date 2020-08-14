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
package org.lanternpowered.server.item.appearance

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.item.ItemType

/**
 * Represents the appearance of an [ItemType].
 *
 * @property itemTypeKey The vanilla/modded item type key.
 */
class ItemAppearance(val itemTypeKey: NamespacedKey)
