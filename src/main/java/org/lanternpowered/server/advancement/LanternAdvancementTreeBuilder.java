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
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.catalog.AbstractNamedCatalogBuilder;
import org.lanternpowered.server.game.registry.type.advancement.AdvancementRegistryModule;
import org.lanternpowered.server.text.translation.TextTranslation;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.text.translation.FixedTranslation;
import org.spongepowered.api.text.translation.Translation;

@SuppressWarnings({"NullableProblems", "ConstantConditions"})
public class LanternAdvancementTreeBuilder extends AbstractNamedCatalogBuilder<AdvancementTree, AdvancementTree.Builder>
        implements AdvancementTree.Builder {

    private Advancement rootAdvancement;
    private String background;

    public LanternAdvancementTreeBuilder() {
        reset();
    }

    @Override
    protected Translation getFinalName() {
        if (getName() != null) {
            return getName();
        }
        return this.rootAdvancement.getDisplayInfo()
                .<Translation>map(displayInfo -> TextTranslation.of(displayInfo.getTitle()))
                .orElseGet(() -> new FixedTranslation(getKey().getValue()));
    }

    @Override
    public AdvancementTree.Builder rootAdvancement(Advancement rootAdvancement) {
        checkNotNull(rootAdvancement, "rootAdvancement");
        final AdvancementRegistryModule registry = AdvancementRegistryModule.get();
        checkState(rootAdvancement.equals(registry.get(rootAdvancement.getKey()).orElse(null)),
                "The root advancement must be registered.");
        checkState(!rootAdvancement.getParent().isPresent(),
                "The root advancement cannot have a parent.");
        checkState(rootAdvancement.getDisplayInfo().isPresent(),
                "The root advancement must have display info.");
        checkState(!rootAdvancement.getTree().isPresent(),
                "The root advancement is already used by a different Advancement Tree.");
        this.rootAdvancement = rootAdvancement;
        return this;
    }

    @Override
    public AdvancementTree.Builder background(String background) {
        checkNotNull(background, "background");
        this.background = background;
        return this;
    }

    @Override
    protected AdvancementTree build(CatalogKey key, Translation name) {
        checkState(this.rootAdvancement != null, "The root advancement must be set");
        checkState(!this.rootAdvancement.getTree().isPresent(),
                "The root advancement is already used by a different Advancement Tree.");
        return new LanternAdvancementTree(key, name, this.rootAdvancement, this.background);
    }

    @Override
    public AdvancementTree.Builder reset() {
        super.reset();
        this.background = "minecraft:textures/gui/advancements/backgrounds/stone.png";
        this.rootAdvancement = null;
        return this;
    }
}
