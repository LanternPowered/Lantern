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
package org.lanternpowered.server;

import org.junit.Test;
import org.lanternpowered.server.shards.event.Shardevent;
import org.lanternpowered.server.shards.event.ShardeventListener;
import org.lanternpowered.server.shards.internal.event.LanternShardeventBus;
import org.lanternpowered.server.util.FieldAccessFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ShardeventBusTest {

    @Test
    public void test() throws NoSuchFieldException {
        final LanternShardeventBus bus = new LanternShardeventBus();
        bus.register(this);
        bus.post(new TestShardevent());

        final TestShardevent event = new TestShardevent();
        System.out.println("myValue: " + event.myValue);

        final Field field = TestShardevent.class.getDeclaredField("myValue");
        field.setAccessible(true);
        try {
            final Field mField = Field.class.getDeclaredField("modifiers");
            mField.setAccessible(true);
            mField.set(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        final Function<TestShardevent, Integer> getterFunction = FieldAccessFactory.createGetter(field);
        final BiConsumer<TestShardevent, Integer> setterFunction = FieldAccessFactory.createSetter(field);

        System.out.println("myValue: " + getterFunction.apply(event));
        setterFunction.accept(event, 5);
        System.out.println("myValue: " + getterFunction.apply(event));
        setterFunction.accept(event, 10);
        System.out.println("myValue: " + getterFunction.apply(event));
        setterFunction.accept(event, 50);
        System.out.println("myValue: " + getterFunction.apply(event));
    }

    @ShardeventListener
    private void onTest(TestShardevent event) {
        System.out.println("DEBUG A!");
    }

    public final class TestShardevent implements Shardevent {

        private int myValue = 1;
    }
}
