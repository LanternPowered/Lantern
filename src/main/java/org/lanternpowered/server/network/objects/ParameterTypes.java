/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.network.objects;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;

import java.util.Optional;
import java.util.UUID;

/**
 * These are already for 1.9, but we probably won't use them anyway before it's released.
 */
public final class ParameterTypes {

    public static final ParameterType<Byte> BYTE = new ParameterType<>(TypeToken.of(Byte.class));
    public static final ParameterType<Integer> INTEGER = new ParameterType<>(TypeToken.of(Integer.class));
    public static final ParameterType<Float> FLOAT = new ParameterType<>(TypeToken.of(Float.class));
    public static final ParameterType<String> STRING = new ParameterType<>(TypeToken.of(String.class));
    public static final ParameterType<Text> TEXT = new ParameterType<>(TypeToken.of(Text.class));
    public static final ParameterType<ItemStack> ITEM_STACK = new ParameterType<>(TypeToken.of(ItemStack.class));
    public static final ParameterType<Boolean> BOOLEAN = new ParameterType<>(TypeToken.of(Boolean.class));
    public static final ParameterType<Vector3f> VECTOR_F = new ParameterType<>(TypeToken.of(Vector3f.class));
    public static final ParameterType<Vector3i> VECTOR_I = new ParameterType<>(TypeToken.of(Vector3i.class));
    public static final ParameterType<Optional<Vector3i>> OPTIONAL_VECTOR_I = new ParameterType<>(new TypeToken<Optional<Vector3i>>() {});
    public static final ParameterType<Direction> DIRECTION = new ParameterType<>(TypeToken.of(Direction.class));
    public static final ParameterType<Optional<UUID>> OPTIONAL_UUID = new ParameterType<>(new TypeToken<Optional<UUID>>() {});
    public static final ParameterType<BlockState> BLOCK_STATE = new ParameterType<>(TypeToken.of(BlockState.class));

}
