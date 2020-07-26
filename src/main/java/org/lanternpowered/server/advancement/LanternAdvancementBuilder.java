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
package org.lanternpowered.server.advancement;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.catalog.AbstractNamedCatalogBuilder;
import org.spongepowered.api.ResourceKey;
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
    protected Advancement build(ResourceKey key, Translation name) {
        return new LanternAdvancement(key, name, this.parent, this.displayInfo, this.criterion);
    }
}
