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
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.api.registry.MutableCatalogTypeRegistry;
import org.lanternpowered.server.catalog.AbstractNamedCatalogBuilder;
import org.lanternpowered.server.registry.type.advancement.AdvancementRegistry;
import org.lanternpowered.server.text.translation.TextTranslation;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.text.translation.FixedTranslation;
import org.spongepowered.api.text.translation.Translation;

@SuppressWarnings({"ConstantConditions"})
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
        final MutableCatalogTypeRegistry<Advancement> registry = AdvancementRegistry.get();
        checkState(rootAdvancement.equals(registry.get(rootAdvancement.getKey())),
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
    protected AdvancementTree build(ResourceKey key, Translation name) {
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
