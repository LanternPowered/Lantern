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
package org.lanternpowered.server.advancement.criteria

import org.lanternpowered.api.util.optional.asOptional
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger
import java.util.Optional

open class LanternCriterion internal constructor(name: String, private val trigger: FilteredTrigger<*>?) : AbstractCriterion(name) {
    override fun getTrigger(): Optional<FilteredTrigger<*>> = this.trigger.asOptional()
}
