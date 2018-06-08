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
package org.lanternpowered.server.network.buffer.contextual;

import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.entity.parameter.ParameterListContextualValueType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

public final class ContextualValueTypes {

    /**
     * A serializer for {@link Text} objects,
     * NULL {@code null} values are NOT SUPPORTED.
     * <p>
     * Text -> JSON -> UTF-8 encoded string prefixed by the length as a var-int.
     */
    public static final ContextualValueType<Text> TEXT = new TextContextualValueType();

    /**
     * A serializer for {@link ItemStack} objects,
     * NULL {@code null} values are SUPPORTED.
     */
    public static final ContextualValueType<ItemStack> ITEM_STACK = new ItemStackContextualValueType();

    /**
     * A serializer for {@link ParameterList} objects.
     */
    public static final ContextualValueType<ParameterList> PARAMETER_LIST = new ParameterListContextualValueType();

    private ContextualValueTypes() {
    }
}
