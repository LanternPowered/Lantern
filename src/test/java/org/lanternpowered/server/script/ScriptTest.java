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
package org.lanternpowered.server.script;

import static org.junit.Assert.assertEquals;

import com.flowpowered.math.GenericMath;
import org.junit.Test;
import org.lanternpowered.api.script.Script;
import org.lanternpowered.api.script.function.condition.Condition;
import org.lanternpowered.api.script.function.value.DoubleValueProvider;
import org.lanternpowered.api.script.function.value.FloatValueProvider;

public class ScriptTest {

    @Test
    public void testA() {
        final LanternScriptGameRegistry scriptGameRegistry = LanternScriptGameRegistry.get();
        final Script<FloatValueProvider> script = scriptGameRegistry.compile("return 0.5f", FloatValueProvider.class);
        //noinspection ConstantConditions
        assertEquals(0.5, script.get().get(null), GenericMath.FLT_EPSILON);
    }

    @Test
    public void testB() {
        final LanternScriptGameRegistry scriptGameRegistry = LanternScriptGameRegistry.get();
        final Script<DoubleValueProvider> script = scriptGameRegistry.compile("return 0.999999d", DoubleValueProvider.class);
        //noinspection ConstantConditions
        assertEquals(0.999999d, script.get().get(null), GenericMath.DBL_EPSILON);
    }

    @Test
    public void testC() {
        final LanternScriptGameRegistry scriptGameRegistry = LanternScriptGameRegistry.get();
        final Script<Condition> script = scriptGameRegistry.compile("return false", Condition.class);
        //noinspection ConstantConditions
        assertEquals(false, script.get().test(null));
    }
}
