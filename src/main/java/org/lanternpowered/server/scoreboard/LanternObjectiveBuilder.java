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
package org.lanternpowered.server.scoreboard;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.scoreboard.criteria.Criterion;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes;
import org.spongepowered.api.text.Text;

public class LanternObjectiveBuilder implements Objective.Builder {

    private String name;
    private Text displayName;
    private Criterion criterion;
    private ObjectiveDisplayMode objectiveDisplayMode;

    public LanternObjectiveBuilder() {
        this.reset();
    }

    @Override
    public LanternObjectiveBuilder name(String name) {
        this.name = checkNotNull(name, "name");
        return this;
    }

    @Override
    public LanternObjectiveBuilder displayName(Text displayName) {
        this.displayName = checkNotNull(displayName, "displayName");
        return this;
    }

    @Override
    public LanternObjectiveBuilder criterion(Criterion criterion) {
        this.criterion = checkNotNull(criterion, "criterion");
        return this;
    }

    @Override
    public LanternObjectiveBuilder objectiveDisplayMode(ObjectiveDisplayMode objectiveDisplayMode) {
        this.objectiveDisplayMode = checkNotNull(objectiveDisplayMode, "objectiveDisplayMode");
        return this;
    }

    @Override
    public LanternObjectiveBuilder from(Objective value) {
        this.name(value.getName())
                .criterion(value.getCriterion())
                .displayName(value.getDisplayName())
                .objectiveDisplayMode(value.getDisplayMode());
        return this;
    }

    @Override
    public LanternObjectiveBuilder reset() {
        this.name = null;
        this.displayName = null;
        this.criterion = null;
        this.objectiveDisplayMode = ObjectiveDisplayModes.INTEGER;
        return this;
    }

    @Override
    public Objective build() throws IllegalStateException {
        checkState(this.name != null, "name is not set");
        checkState(this.displayName != null, "displayName is not set");
        checkState(this.criterion != null, "criterion is not set");
        return new LanternObjective(this.name, this.criterion, this.objectiveDisplayMode, this.displayName);
    }
}
