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
package org.lanternpowered.server.advancement.criteria.trigger

import org.lanternpowered.api.util.ToStringHelper
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger
import org.spongepowered.api.advancement.criteria.trigger.FilteredTriggerConfiguration
import org.spongepowered.api.advancement.criteria.trigger.Trigger

class LanternFilteredTrigger<C : FilteredTriggerConfiguration>(
        private val type: Trigger<C>,
        private val config: C
) : FilteredTrigger<C> {

    override fun getType(): Trigger<C> = this.type
    override fun getConfiguration(): C = this.config

    override fun toString(): String = ToStringHelper(this)
            .add("type", this.type.key)
            .add("config", this.config)
            .toString()
}
