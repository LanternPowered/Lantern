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
package org.lanternpowered.server.script;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.lanternpowered.api.script.Script;
import org.lanternpowered.api.script.function.condition.Condition;
import org.lanternpowered.api.script.function.value.DoubleValueProvider;
import org.lanternpowered.api.script.function.value.FloatValueProvider;
import org.spongepowered.math.GenericMath;

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
