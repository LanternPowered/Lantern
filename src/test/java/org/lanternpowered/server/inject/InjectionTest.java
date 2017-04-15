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
package org.lanternpowered.server.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import joptsimple.OptionParser;
import org.junit.Test;
import org.lanternpowered.server.inject.option.OptionModule;

import javax.inject.Named;

public class InjectionTest {

    public static class TestObject {

        @Inject @Option("my-option") private int myOption;

        @Inject
        public void testOtherOption(@Option("my-other-option") double myOtherOption) {
            System.out.println("MyOtherOptionValue: " + myOtherOption);
        }
    }

    @Test
    public void test() {
        final AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                install(new InjectionPointProvider());
                final OptionParser optionParser = new OptionParser();
                optionParser.allowsUnrecognizedOptions();
                install(new OptionModule.Simple(new String[] { "--my-option=500", "--my-other-option=10.684" }, optionParser));
            }
        };
        final Injector injector = Guice.createInjector(module);
        final TestObject testObject = injector.getInstance(TestObject.class);
        System.out.println("MyOptionValue: " + testObject.myOption);
    }
}
