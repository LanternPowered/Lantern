/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.component.injector.asm;

import java.util.concurrent.ExecutionException;

import org.lanternpowered.server.component.Component;
import org.lanternpowered.server.component.ComponentHolder;
import org.lanternpowered.server.component.injector.Injector;
import org.lanternpowered.server.component.injector.InjectorFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public final class AsmInjectorFactory implements InjectorFactory {

    private final LoadingCache<Class<? extends Component>, Injector> cache =
            CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Class<? extends Component>, Injector>() {
                @Override
                public Injector load(Class<? extends Component> key) throws Exception {
                    return new AsmInjector();
                }
            });

    @Override
    public Injector create(Class<? extends Component> type) {
        try {
            return this.cache.get(type);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static class AsmInjector implements Injector {

        @Override
        public Component create() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void inject(Component component, ComponentHolder holder) {
            ((IComponent) component).inject(holder);
        }

        @Override
        public void inject(Component component, Component componentToInject) {
            ((IComponent) component).inject(componentToInject);
        }

        @Override
        public void attach(Component component) {
            ((IComponent) component).attach();
        }

        @Override
        public void detach(Component component) {
            ((IComponent) component).detach();
        }
    }
}
