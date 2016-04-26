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
package org.lanternpowered.server.command.targeted;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.command.element.DelegateCompleterElement;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

public class TargetedVector3dElement {

    public static DelegateCompleterElement of(Text key) {
        return of(key, null);
    }

    public static DelegateCompleterElement of(Text key, @Nullable Integer defaultValue) {
        return of(key, defaultValue, defaultValue, defaultValue);
    }

    public static DelegateCompleterElement of(Text key, @Nullable Integer defaultXValue,
            @Nullable Integer defaultYValue, @Nullable Integer defaultZValue) {
        return DelegateCompleterElement.vector3d(GenericArguments.vector3d(key),
                (src, args, context) -> apply(src, Vector3i::getX, defaultXValue),
                (src, args, context) -> apply(src, Vector3i::getY, defaultYValue),
                (src, args, context) -> apply(src, Vector3i::getZ, defaultZValue));
    }

    private static List<String> apply(CommandSource src, Function<Vector3i, Integer> mapper, @Nullable Integer defaultValue) {
        Integer value = src instanceof TargetingCommandSource ? ((TargetingCommandSource) src).getTargetBlock()
                .map(mapper).orElse(defaultValue) : defaultValue;
        return value == null ? Collections.emptyList() : Collections.singletonList(value.toString());
    }
}
