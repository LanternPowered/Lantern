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

import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.advancement.criteria.AdvancementCriterion
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger

abstract class AbstractCriterionBuilder<T : AdvancementCriterion, B : AdvancementCriterion.BaseBuilder<T, B>> :
        AdvancementCriterion.BaseBuilder<T, B> {

    private var trigger: FilteredTrigger<*>? = null
    private var name: String? = null

    private fun apply(fn: () -> Unit): B {
        fn()
        return this.uncheckedCast()
    }

    override fun trigger(trigger: FilteredTrigger<*>): B = this.apply { this.trigger = trigger }
    override fun name(name: String): B = this.apply { this.name = name }

    override fun build(): T {
        val name = checkNotNull(this.name) { "The name must be set" }
        return this.build(name, this.trigger)
    }

    abstract fun build(name: String, trigger: FilteredTrigger<*>?): T

    override fun from(value: T): B = this.apply {
        this.trigger = value.trigger.orElse(null)
        this.name = value.name
    }

    override fun reset(): B = this.apply {
        this.trigger = null
        this.name = null
    }
}
