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
package org.lanternpowered.server.advancement;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.catalog.AbstractNamedCatalogBuilder;
import org.lanternpowered.server.text.translation.TextTranslation;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.text.translation.FixedTranslation;
import org.spongepowered.api.text.translation.Translation;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings({"NullableProblems", "ConstantConditions"})
public class LanternAdvancementBuilder extends AbstractNamedCatalogBuilder<Advancement, Advancement.Builder> implements Advancement.Builder {

    @Nullable private Advancement parent;
    private AdvancementCriterion criterion;
    @Nullable private DisplayInfo displayInfo;

    public LanternAdvancementBuilder() {
        reset();
    }

    @Override
    protected Translation getFinalName() {
        if (getName() != null) {
            return getName();
        } else if (this.displayInfo != null) {
            return TextTranslation.of(this.displayInfo.getTitle());
        }
        return new FixedTranslation(getKey().getValue());
    }

    @Override
    public Advancement.Builder parent(@Nullable Advancement parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public Advancement.Builder criterion(AdvancementCriterion criterion) {
        checkNotNull(criterion, "criterion");
        this.criterion = criterion;
        return this;
    }

    @Override
    public Advancement.Builder displayInfo(@Nullable DisplayInfo displayInfo) {
        this.displayInfo = displayInfo;
        return this;
    }

    @Override
    public Advancement.Builder reset() {
        this.criterion = AdvancementCriterion.empty();
        this.displayInfo = null;
        this.parent = null;
        return super.reset();
    }

    @Override
    protected Advancement build(CatalogKey key, Translation name) {
        return new LanternAdvancement(key, name, this.parent, this.displayInfo, this.criterion);
    }
}
