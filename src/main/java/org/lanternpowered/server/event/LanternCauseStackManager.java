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

import static org.lanternpowered.server.event.CauseStack.current;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKey;

import java.util.Optional;

/**
 * A {@link CauseStackManager} that manages the {@link LanternCauseStack}s for all
 * the supported {@link Thread}s. (main, world threads, etc.)
 */
@Singleton
public class LanternCauseStackManager implements CauseStackManager {

    @Inject
    private LanternCauseStackManager() {
    }

    @Override
    public Cause getCurrentCause() {
        return current().getCurrentCause();
    }

    @Override
    public EventContext getCurrentContext() {
        return current().getCurrentContext();
    }

    @Override
    public CauseStackManager pushCause(Object obj) {
        return current().pushCause(obj);
    }

    @Override
    public Object popCause() {
        return current().popCause();
    }

    @Override
    public void popCauses(int n) {
        current().popCauses(n);
    }

    @Override
    public Object peekCause() {
        return current().peekCause();
    }

    @Override
    public StackFrame pushCauseFrame() {
        return current().pushCauseFrame();
    }

    @Override
    public void popCauseFrame(StackFrame handle) {
        current().popCauseFrame(handle);
    }

    @Override
    public <T> CauseStackManager addContext(EventContextKey<T> key, T value) {
        return current().addContext(key, value);
    }

    @Override
    public <T> Optional<T> getContext(EventContextKey<T> key) {
        return current().getContext(key);
    }

    @Override
    public <T> Optional<T> removeContext(EventContextKey<T> key) {
        return current().removeContext(key);
    }
}
