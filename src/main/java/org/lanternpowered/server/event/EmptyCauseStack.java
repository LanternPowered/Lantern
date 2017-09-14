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
package org.lanternpowered.server.event;

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKey;

import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
final class EmptyCauseStack implements CauseStack {

    static final EmptyCauseStack instance = new EmptyCauseStack();
    private static final EmptyStackFrame frameInstance = new EmptyStackFrame(instance);

    private final Object object = new Object();
    @Nullable private Cause cause;

    @Override
    public Cause getCurrentCause() {
        // Lazily construct the singleton instance
        if (this.cause == null) {
            this.cause = Cause.of(getCurrentContext(), Lantern.getGame());
        }
        return this.cause;
    }

    @Override
    public EventContext getCurrentContext() {
        return EventContext.empty();
    }

    @Override
    public CauseStack pushCause(Object obj) {
        return this;
    }

    @Override
    public Object popCause() {
        return this.object;
    }

    @Override
    public void popCauses(int n) {
    }

    @Override
    public Object peekCause() {
        return this.object;
    }

    @Override
    public <T> Optional<T> first(Class<T> target) {
        return Game.class.isAssignableFrom(target) ? Optional.of((T) Lantern.getGame()) : Optional.empty();
    }

    @Override
    public <T> Optional<T> last(Class<T> target) {
        return Game.class.isAssignableFrom(target) ? Optional.of((T) Lantern.getGame()) : Optional.empty();
    }

    @Override
    public boolean containsType(Class<?> target) {
        return Game.class.isAssignableFrom(target);
    }

    @Override
    public boolean contains(Object object) {
        return object == Lantern.getGame();
    }

    @Override
    public Frame pushCauseFrame() {
        return frameInstance;
    }

    @Override
    public void popCauseFrame(StackFrame handle) {
    }

    @Override
    public <T> CauseStack addContext(EventContextKey<T> key, T value) {
        return this;
    }

    @Override
    public <T> Optional<T> getContext(EventContextKey<T> key) {
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> removeContext(EventContextKey<T> key) {
        return Optional.empty();
    }

    private static final class EmptyStackFrame implements Frame {

        private final EmptyCauseStack stack;

        private EmptyStackFrame(EmptyCauseStack stack) {
            this.stack = stack;
        }

        @Override
        public void close() {
        }

        @Override
        public Cause getCurrentCause() {
            return this.stack.getCurrentCause();
        }

        @Override
        public EventContext getCurrentContext() {
            return EventContext.empty();
        }

        @Override
        public Frame pushCause(Object obj) {
            return this;
        }

        @Override
        public Object popCause() {
            return this.stack.popCause();
        }

        @Override
        public <T> Frame addContext(EventContextKey<T> key, T value) {
            return this;
        }

        @Override
        public <T> Optional<T> removeContext(EventContextKey<T> key) {
            return this.stack.removeContext(key);
        }
    }
}
