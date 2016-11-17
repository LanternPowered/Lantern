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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline;
import org.lanternpowered.server.behavior.pipeline.impl.MutableBehaviorPipelineImpl;
import org.lanternpowered.server.data.value.AbstractValueContainer;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.function.Consumer;

import javax.annotation.Nullable;

public class ItemTypeBuilderImpl implements ItemTypeBuilder {

    @Nullable private PropertyProviderCollection.Builder propertiesBuilder;
    @Nullable private MutableBehaviorPipeline<Behavior> behaviorPipeline;
    @Nullable private TranslationProvider translationProvider;
    private Consumer<AbstractValueContainer> keysProvider = valueContainer -> {};
    @Nullable private BlockType blockType;
    private int maxStackQuantity = 64;

    @Override
    public ItemTypeBuilderImpl keysProvider(Consumer<AbstractValueContainer> consumer) {
        checkNotNull(consumer, "consumer");
        this.keysProvider = this.keysProvider.andThen(consumer);
        return this;
    }

    @Override
    public ItemTypeBuilderImpl blockType(BlockType blockType) {
        checkNotNull(blockType, "blockType");
        this.blockType = blockType;
        return this;
    }

    @Override
    public ItemTypeBuilderImpl properties(PropertyProviderCollection collection) {
        checkNotNull(collection, "collection");
        this.propertiesBuilder = collection.toBuilder();
        return this;
    }

    @Override
    public ItemTypeBuilderImpl properties(Consumer<PropertyProviderCollection.Builder> consumer) {
        checkNotNull(consumer, "consumer");
        if (this.propertiesBuilder == null) {
            this.propertiesBuilder = PropertyProviderCollection.builder();
        }
        consumer.accept(this.propertiesBuilder);
        return this;
    }

    @Override
    public ItemTypeBuilderImpl behaviors(BehaviorPipeline<Behavior> behaviorPipeline) {
        checkNotNull(behaviorPipeline, "behaviorPipeline");
        this.behaviorPipeline = new MutableBehaviorPipelineImpl<>(Behavior.class, behaviorPipeline.getBehaviors());
        return this;
    }

    @Override
    public ItemTypeBuilderImpl behaviors(Consumer<MutableBehaviorPipeline<Behavior>> consumer) {
        checkNotNull(consumer, "consumer");
        if (this.behaviorPipeline == null) {
            this.behaviorPipeline = new MutableBehaviorPipelineImpl<>(Behavior.class, new ArrayList<>());
        }
        consumer.accept(this.behaviorPipeline);
        return this;
    }

    @Override
    public ItemTypeBuilderImpl translation(String translation) {
        checkNotNull(translation, "translation");
        return translation(tr(translation));
    }

    @Override
    public ItemTypeBuilderImpl translation(Translation translation) {
        checkNotNull(translation, "translation");
        this.translationProvider = TranslationProvider.of(translation);
        return this;
    }

    @Override
    public ItemTypeBuilderImpl translation(TranslationProvider translationProvider) {
        checkNotNull(translationProvider, "translationProvider");
        this.translationProvider = translationProvider;
        return this;
    }

    @Override
    public ItemTypeBuilder maxStackQuantity(int quantity) {
        checkArgument(quantity > 0, "Quantity must be greater them zero");
        this.maxStackQuantity = quantity;
        return this;
    }

    @Override
    public LanternItemType build(String pluginId, String name) {
        MutableBehaviorPipeline<Behavior> behaviorPipeline = this.behaviorPipeline;
        if (behaviorPipeline == null) {
            behaviorPipeline = new MutableBehaviorPipelineImpl<>(Behavior.class, new ArrayList<>());
        } else {
            behaviorPipeline = new MutableBehaviorPipelineImpl<>(Behavior.class, new ArrayList<>(behaviorPipeline.getBehaviors()));
        }
        TranslationProvider translationProvider = this.translationProvider;
        if (translationProvider == null) {
            String path = "tile." + name + ".name";
            if (!pluginId.equals("minecraft")) {
                path = pluginId + '.' + path;
            }
            translationProvider = TranslationProvider.of(tr(path));
        }
        PropertyProviderCollection properties;
        if (this.propertiesBuilder != null) {
            properties = this.propertiesBuilder.build();
        } else {
            properties = PropertyProviderCollection.builder().build();
        }
        return new LanternItemType(pluginId, name, properties, behaviorPipeline, translationProvider,
                this.keysProvider, this.blockType, this.maxStackQuantity);
    }
}
