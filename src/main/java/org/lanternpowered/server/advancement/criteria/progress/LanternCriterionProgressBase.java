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
package org.lanternpowered.server.advancement.criteria.progress;

import org.lanternpowered.server.advancement.LanternAdvancementProgress;
import org.lanternpowered.server.advancement.criteria.AbstractCriterion;
import org.lanternpowered.server.advancement.criteria.trigger.LanternTrigger;

import java.time.Instant;
import java.util.Optional;

import javax.annotation.Nullable;

abstract class LanternCriterionProgressBase<T extends AbstractCriterion> extends AbstractCriterionProgress<T> {

    @Nullable Instant achievingTime;
    private boolean attached = false;

    LanternCriterionProgressBase(T criterion, LanternAdvancementProgress progress) {
        super(criterion, progress);
    }

    @Override
    public boolean achieved() {
        return this.achievingTime != null;
    }

    @Override
    public Optional<Instant> get() {
        return Optional.ofNullable(this.achievingTime);
    }

    @Override
    public void attachTrigger() {
        this.criterion.getTrigger().ifPresent(trigger -> {
            // Only attach once
            if (this.attached) {
                return;
            }
            this.attached = true;
            ((LanternTrigger) trigger.getType()).add(getAdvancementProgress().getPlayerAdvancements(), this);
        });
    }

    @Override
    public void detachTrigger() {
        this.criterion.getTrigger().ifPresent(trigger -> {
            // Only detach once
            if (!this.attached) {
                return;
            }
            this.attached = false;
            ((LanternTrigger) trigger.getType()).remove(getAdvancementProgress().getPlayerAdvancements(), this);
        });
    }
}
