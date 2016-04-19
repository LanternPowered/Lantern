/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.network.message.codec.serializer;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.network.objects.LocalizedText;
import org.lanternpowered.server.network.objects.Parameter;
import org.lanternpowered.server.network.objects.RawItemStack;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.UUID;

public final class Types {

    /**
     * A var-int encoded int value.
     */
    public static final Type<Integer> VAR_INT = Type.create(Integer.class);

    /**
     * A var-long encoded long value.
     */
    public static final Type<Long> VAR_LONG = Type.create(Long.class);

    /**
     * A vector3i (position) encoded for minecraft protocol.
     */
    public static final Type<Vector3i> POSITION = Type.create(Vector3i.class);

    /**
     * A utf-8 encoded string prefixed by the length in var-int.
     */
    public static final Type<String> STRING = Type.create(String.class);

    /**
     * A utf-8 encoded text prefixed by the length in var-int.
     */
    public static final Type<Text> TEXT = Type.create(Text.class);

    /**
     * A unique id encoded in two long values.
     */
    public static final Type<UUID> UNIQUE_ID = Type.create(UUID.class);

    /**
     * A data view encoded in nbt format.
     */
    public static final Type<DataView> DATA_VIEW = Type.create(DataView.class);

    /**
     * A localized text object.
     */
    public static final Type<LocalizedText> LOCALIZED_TEXT = Type.create(LocalizedText.class);

    /**
     * A item stack.
     */
    public static final Type<ItemStack> ITEM_STACK = Type.create(ItemStack.class);

    /**
     * A parameter list.
     */
    public static final Type<List<Parameter<?>>> PARAMETERS = Type.create(new TypeToken<List<Parameter<?>>>() {});

    /**
     * A raw item stack.
     */
    public static final Type<RawItemStack> RAW_ITEM_STACK = Type.create(RawItemStack.class);

    private Types() {
    }
}
