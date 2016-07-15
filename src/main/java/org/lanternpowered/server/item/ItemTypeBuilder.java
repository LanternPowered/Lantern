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
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline;
import org.lanternpowered.server.data.value.AbstractValueContainer;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.text.translation.Translation;

import java.util.function.Consumer;

public interface ItemTypeBuilder {

    ItemTypeBuilder keysProvider(Consumer<AbstractValueContainer> consumer);

    ItemTypeBuilder blockType(BlockType blockType);

    ItemTypeBuilder behaviors(BehaviorPipeline<Behavior> behaviorPipeline);

    ItemTypeBuilder behaviors(Consumer<MutableBehaviorPipeline<Behavior>> consumer);

    ItemTypeBuilder translation(String translation);

    ItemTypeBuilder translation(Translation translation);

    ItemTypeBuilder translation(TranslationProvider translationProvider);

    ItemTypeBuilder maxStackQuantity(int quantity);

    LanternItemType build(String pluginId, String name);
}
