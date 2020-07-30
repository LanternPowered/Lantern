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
package org.lanternpowered.server.advancement.criteria.progress

import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.advancement.LanternAdvancementProgress
import org.lanternpowered.server.advancement.criteria.AbstractCriterion
import org.lanternpowered.server.advancement.criteria.trigger.LanternTrigger
import java.time.Instant
import java.util.Optional

abstract class LanternCriterionProgressBase<T : AbstractCriterion>(
        criterion: T, progress: LanternAdvancementProgress
) : AbstractCriterionProgress<T>(criterion, progress) {

    protected var achievingTime: Instant? = null

    private var attached = false

    override fun achieved(): Boolean = this.achievingTime != null
    override fun get(): Optional<Instant> = this.achievingTime.optional()

    override fun attachTrigger() {
        val trigger = this.criterion.trigger.orNull() ?: return
        // Only attach once
        if (this.attached)
            return
        this.attached = true
        (trigger.type as LanternTrigger<*>).add(this.advancementProgress.playerAdvancements, this)
    }

    override fun detachTrigger() {
        val trigger = this.criterion.trigger.orNull() ?: return
        // Only detach once
        if (!this.attached)
            return
        this.attached = false
        (trigger.type as LanternTrigger<*>).remove(this.advancementProgress.playerAdvancements, this)
    }
}
