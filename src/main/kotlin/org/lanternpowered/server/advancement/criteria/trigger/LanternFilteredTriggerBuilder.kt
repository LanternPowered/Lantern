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

import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger
import org.spongepowered.api.advancement.criteria.trigger.FilteredTriggerConfiguration
import org.spongepowered.api.advancement.criteria.trigger.Trigger

@Suppress("UNCHECKED_CAST")
class LanternFilteredTriggerBuilder<C : FilteredTriggerConfiguration> : FilteredTrigger.Builder<C> {

    private var config: C? = null
    private var type: Trigger<C>? = null

    override fun <T : FilteredTriggerConfiguration> type(type: Trigger<T>): FilteredTrigger.Builder<T> {
        this.type = type as Trigger<C>
        return this as LanternFilteredTriggerBuilder<T>
    }

    override fun config(config: C): FilteredTrigger.Builder<C> = apply { this.config = config }

    override fun build(): FilteredTrigger<C> {
        val type = checkNotNull(this.type) { "The type must be set" }
        val config = checkNotNull(this.config) { "The config must be set" }
        return LanternFilteredTrigger(type, config)
    }

    override fun from(value: FilteredTrigger<C>): FilteredTrigger.Builder<C> = apply {
        this.config = value.configuration
        this.type = value.type
    }

    override fun reset(): FilteredTrigger.Builder<C> = apply {
        this.config = null
        this.type = null
    }
}
