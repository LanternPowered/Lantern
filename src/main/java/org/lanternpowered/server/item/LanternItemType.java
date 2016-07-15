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
package org.lanternpowered.server.item;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.data.value.AbstractValueContainer;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.translation.Translation;

import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nullable;

public class LanternItemType extends PluginCatalogType.Base implements ItemType {

    private final MutableBehaviorPipeline<Behavior> behaviorPipeline;
    private final TranslationProvider translationProvider;
    private final Consumer<AbstractValueContainer> keysProvider;

    @Nullable private final BlockType blockType;

    private final int maxStackQuantity;

    LanternItemType(String pluginId, String name, MutableBehaviorPipeline<Behavior> behaviorPipeline,
            TranslationProvider translationProvider, Consumer<AbstractValueContainer> keysProvider,
            @Nullable BlockType blockType, int maxStackQuantity) {
        super(pluginId, name);
        this.keysProvider = keysProvider;
        this.maxStackQuantity = maxStackQuantity;
        this.behaviorPipeline = behaviorPipeline;
        this.translationProvider = translationProvider;
        this.blockType = blockType;
    }

    @Override
    public int getMaxStackQuantity() {
        return this.maxStackQuantity;
    }

    public TranslationProvider getTranslationProvider() {
        return this.translationProvider;
    }

    @Override
    public Translation getTranslation() {
        return this.translationProvider.get(this, null);
    }

    @Override
    public Optional<BlockType> getBlock() {
        return Optional.ofNullable(this.blockType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Property<?, ?>> Optional<T> getDefaultProperty(Class<T> propertyClass) {
        // TODO
        return Optional.empty();
    }

    public MutableBehaviorPipeline<Behavior> getPipeline() {
        return this.behaviorPipeline;
    }

    public Consumer<AbstractValueContainer> getKeysProvider() {
        return this.keysProvider;
    }
}
