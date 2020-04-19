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
package org.lanternpowered.server.util;

import io.netty.util.concurrent.FastThreadLocalThread;
import org.lanternpowered.api.cause.CauseStack;

import org.checkerframework.checker.nullness.qual.Nullable;

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
     * A {@link Throwable} which points to where
     * this {@link Thread} is constructed.
     */
    private final Throwable constructionSite;

    /**
     * Constructs a new {@link LanternThread} with
     * the given {@link Runnable} task and name.
     *
     * @param target The task
     * @param name The name
     */
    public LanternThread(Runnable target, String name) {
        super(target, name);
        this.constructionSite = new Exception();
    }

    /**
     * Constructs a new {@link LanternThread} with
     * the given {@link Runnable} task.
     *
     * @param target The task
     */
    public LanternThread(Runnable target) {
        super(target);
        this.constructionSite = new Exception();
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

    /**
     * Gets the {@link Throwable} which points to where
     * this {@link Thread} is constructed.
     */
    public Throwable getConstructionSite() {
        return this.constructionSite;
    }
}
