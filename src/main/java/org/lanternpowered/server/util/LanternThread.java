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
package org.lanternpowered.server.util;

import io.netty.util.concurrent.FastThreadLocalThread;
import org.lanternpowered.server.event.CauseStack;

import javax.annotation.Nullable;

/**
 * A {@link FastThreadLocalThread} which directly stores some objects
 * that will be accessed a lot, like the {@link CauseStack}.
 */
public class LanternThread extends FastThreadLocalThread {

    /**
     * The {@link CauseStack} assigned to this thread, if any.
     */
    @Nullable private CauseStack causeStack;

    /**
     * Constructs a new {@link LanternThread} with
     * the given {@link Runnable} task and name.
     *
     * @param target The task
     * @param name The name
     */
    public LanternThread(Runnable target, String name) {
        super(target, name);
    }

    /**
     * Constructs a new {@link LanternThread} with
     * the given {@link Runnable} task.
     *
     * @param target The task
     */
    public LanternThread(Runnable target) {
        super(target);
    }

    /**
     * Gets the {@link CauseStack} that is
     * assigned to this thread, if any.
     *
     * @return The cause stack, or {@code null} if not present
     */
    @Nullable
    public CauseStack getCauseStack() {
        return this.causeStack;
    }

    /**
     * Sets the {@link CauseStack} that is assigned to this thread.
     *
     * @param causeStack The cause stack
     */
    public void setCauseStack(CauseStack causeStack) {
        this.causeStack = causeStack;
    }
}
