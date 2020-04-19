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
package org.lanternpowered.api.registry

import org.lanternpowered.api.catalog.CatalogType

typealias BaseBuilder<T, B> = org.spongepowered.api.util.ResettableBuilder<T, B>
typealias CopyableBuilder<T, B> = org.spongepowered.api.util.CopyableBuilder<T, B>

/**
 * A base builder to construct [CatalogType]s.
 */
typealias CatalogBuilder<C, B> = org.spongepowered.api.util.CatalogBuilder<C, B>
