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
package org.lanternpowered.server.scoreboard

import org.lanternpowered.api.text.Text
import org.spongepowered.api.scoreboard.criteria.Criterion
import org.spongepowered.api.scoreboard.objective.Objective
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes

class LanternObjectiveBuilder : Objective.Builder {

    private var name: String? = null
    private var displayName: Text? = null
    private var criterion: Criterion? = null
    private var objectiveDisplayMode: ObjectiveDisplayMode = ObjectiveDisplayModes.INTEGER.get()

    override fun name(name: String) = apply { this.name = name }
    override fun displayName(displayName: Text)= apply { this.displayName = displayName }
    override fun criterion(criterion: Criterion) = apply { this.criterion = criterion }
    override fun objectiveDisplayMode(mode: ObjectiveDisplayMode) = apply { this.objectiveDisplayMode = mode }

    override fun from(value: Objective): LanternObjectiveBuilder = name(value.name)
            .criterion(value.criterion)
            .displayName(value.displayName)
            .objectiveDisplayMode(value.displayMode)

    override fun reset() = apply {
        this.name = null
        this.displayName = null
        this.criterion = null
        this.objectiveDisplayMode = ObjectiveDisplayModes.INTEGER.get()
    }

    override fun build(): Objective {
        val name = checkNotNull(this.name) { "name is not set" }
        val displayName = checkNotNull(this.displayName) { "displayName is not set" }
        val criterion = checkNotNull(this.criterion) { "criterion is not set" }
        return LanternObjective(name, criterion, this.objectiveDisplayMode, displayName)
    }
}
