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
package org.lanternpowered.server.inject;

import static org.junit.Assert.assertEquals;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import joptsimple.OptionParser;
import org.junit.Test;
import org.lanternpowered.server.inject.option.Option;
import org.lanternpowered.server.inject.option.OptionModule;
import org.spongepowered.math.GenericMath;

public class InjectionTest {

    public static class TestObject {

        @Inject @Option("my-option") private int myOption;

        private double myOtherOption;

        @Inject
        public void testOtherOption(@Option("my-other-option") double myOtherOption) {
            this.myOtherOption = myOtherOption;
        }
    }

    @Test
    public void test() {
        final double valueA = 10.684;
        final int valueB = 500;
        final AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                install(new InjectionPointProvider());
                final OptionParser optionParser = new OptionParser();
                optionParser.allowsUnrecognizedOptions();
                install(new OptionModule() {
                    @Override
                    protected void configure0() {
                        bindArguments().toInstance(new String[] {
                                "--my-option=" + valueB,
                                "--my-other-option=" + valueA
                        });
                        bindParser().toInstance(optionParser);
                    }
                });
            }
        };
        final Injector injector = Guice.createInjector(module);
        final TestObject testObject = injector.getInstance(TestObject.class);
        assertEquals(testObject.myOtherOption, valueA, GenericMath.DBL_EPSILON);
        assertEquals(testObject.myOption, valueB);
    }
}
