/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.scoreboard;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.scoreboard.critieria.Criterion;
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
