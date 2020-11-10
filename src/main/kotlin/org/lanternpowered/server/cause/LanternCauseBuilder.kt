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
package org.lanternpowered.server.cause

import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.cause.CauseBuilder
import org.lanternpowered.api.cause.CauseContext
import org.lanternpowered.api.cause.CauseContextKey

class LanternCauseBuilder : CauseBuilder {

    private val causes = ArrayList<Any>()
    private var contextValues: MutableMap<CauseContextKey<*>, Any?>? = null
    private var context: CauseContext? = null

    override fun append(cause: Any): CauseBuilder = this.apply {
        this.causes.add(cause)
    }

    override fun appendAll(causes: Iterable<Any>): CauseBuilder = this.apply {
        this.causes.addAll(causes)
    }

    override fun context(causeContext: CauseContext): CauseBuilder = this.apply {
        this.contextValues?.clear()
        this.context = causeContext
    }

    override fun <T> context(key: CauseContextKey<T>, value: T): CauseBuilder = this.apply {
        this.context = null
        val map = this.contextValues ?: HashMap<CauseContextKey<*>, Any?>().also { this.contextValues = it }
        map[key] = value
    }

    override fun build(): Cause {
        var context = this.context
        if (context == null) {
            val contextValues = this.contextValues
            if (contextValues != null && contextValues.isNotEmpty()) {
                context = CauseContext.of(contextValues).also { this.context = it }
            } else {
                context = CauseContext.empty()
            }
        }
        return Cause.of(context, this.causes)
    }

    override fun reset(): CauseBuilder = this.apply {
        this.causes.clear()
        this.contextValues?.clear()
        this.context = null
    }
}
