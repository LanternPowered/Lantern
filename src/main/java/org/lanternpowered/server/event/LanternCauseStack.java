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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Strings;
import io.netty.util.concurrent.FastThreadLocal;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.util.PrettyPrinter;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKey;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public final class LanternCauseStack implements CauseStack {

    static final FastThreadLocal<LanternCauseStack> causeStacks = new FastThreadLocal<>();

    /**
     * Whether the debug mode of cause frames should be enabled.
     */
    private static final boolean DEBUG_CAUSE_FRAMES = Boolean.valueOf(System.getProperty("sponge.debugcauseframes", "false"));

    private final Deque<Object> cause = new ArrayDeque<>();
    private final Deque<CauseStackFrameImpl> frames = new ArrayDeque<>();
    private final Map<EventContextKey<?>, Object> ctx = new HashMap<>();

    private int minDepth = 0;
    @Nullable private Cause cachedCause;
    @Nullable private EventContext cachedCtx;

    public LanternCauseStack() {
    }

    @Override
    public Cause getCurrentCause() {
        if (this.cachedCause == null || this.cachedCtx == null) {
            if (this.cause.isEmpty()) {
                this.cachedCause = Cause.of(getCurrentContext(), Lantern.getGame());
            } else {
                this.cachedCause = Cause.of(getCurrentContext(), this.cause);
            }
        }
        return this.cachedCause;
    }

    @Override
    public EventContext getCurrentContext() {
        if (this.cachedCtx == null) {
            this.cachedCtx = EventContext.of(this.ctx);
        }
        return this.cachedCtx;
    }

    @Override
    public CauseStack pushCause(Object obj) {
        checkNotNull(obj, "obj");
        this.cachedCause = null;
        this.cause.push(obj);
        return this;
    }

    @Override
    public Object popCause() {
        if (this.cause.size() <= this.minDepth) {
            throw new IllegalStateException("Cause stack corruption, tried to pop more objects off than were pushed since last frame (Size was "
                    + this.cause.size() + " but mid depth is " + this.minDepth + ")");
        }
        this.cachedCause = null;
        return this.cause.pop();
    }

    @Override
    public void popCauses(int n) {
        for (int i = 0; i < n; i++) {
            popCause();
        }
    }

    @Override
    public Object peekCause() {
        return this.cause.peek();
    }

    @Override
    public <T> Optional<T> first(Class<T> target) {
        return firstIn(this.cause.iterator(), target);
    }

    @Override
    public <T> Optional<T> last(Class<T> target) {
        return firstIn(this.cause.descendingIterator(), target);
    }

    @Override
    public boolean containsType(Class<?> target) {
        for (Object aCause : this.cause) {
            if (target.isInstance(aCause)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(Object object) {
        for (Object aCause : this.cause) {
            if (aCause.equals(object)) {
                return true;
            }
        }
        return false;
    }

    private <T> Optional<T> firstIn(Iterator<Object> iterator, Class<T> target) {
        checkNotNull(target, "target");
        while (iterator.hasNext()) {
            final Object aCause = iterator.next();
            if (target.isInstance(aCause)) {
                return Optional.of((T) aCause);
            }
        }
        return Optional.empty();
    }

    @Override
    public Frame pushCauseFrame() {
        final CauseStackFrameImpl frame = new CauseStackFrameImpl(this, this.minDepth);
        this.frames.push(frame);
        this.minDepth = this.cause.size();
        if (DEBUG_CAUSE_FRAMES) {
            // Attach an exception to the frame so that if there is any frame
            // corruption we can print out the stack trace of when the frames
            // were created.
            frame.debugStack = new Exception("");
        }
        return frame;
    }

    @Override
    public void popCauseFrame(StackFrame oldFrame) {
        checkNotNull(oldFrame, "oldFrame");
        final CauseStackFrameImpl frame = this.frames.peek();
        if (frame != oldFrame) {
            if (frame.stack != this) {
                throw new IllegalStateException("Cause Stack Frame Corruption! Attempted to pop a frame from a different stack.");
            }
            // If the given frame is not the top frame then some form of
            // corruption of the stack has occurred and we do our best to correct
            // it.

            // If the target frame is still in the stack then we can pop frames
            // off the stack until we reach it, otherwise we have no choice but
            // to simply throw an error.
            int offset = -1;
            int i = 0;
            for (CauseStackFrameImpl f : this.frames) {
                if (f == oldFrame) {
                    offset = i;
                    break;
                }
                i++;
            }
            final String name = Thread.currentThread().getName();
            if (!DEBUG_CAUSE_FRAMES && offset == -1) {
                // if we're not debugging the cause frames then throw an error
                // immediately otherwise let the pretty printer output the frame
                // that was erroneously popped.
                throw new IllegalStateException("Cause Stack Frame Corruption on the Thread \"" + name +
                        "\"! Attempted to pop a frame that was not on the stack.");
            }
            final PrettyPrinter printer = new PrettyPrinter(100)
                    .add("Cause Stack Frame Corruption on the Thread \"%s\"!", name).centre().hr()
                    .add("Found %d frames left on the stack. Clearing them all.", new Object[] { offset + 1 });
            if (!DEBUG_CAUSE_FRAMES) {
                printer.add().add("Please add -Dsponge.debugcauseframes=true to your startup flags to enable further debugging output.");
                Lantern.getLogger().warn("  Add -Dsponge.debugcauseframes=true to your startup flags to enable further debugging output.");
            } else {
                printer.add();
                printer.add("> Attempting to pop frame:");
                printStack(printer, frame.debugStack);
                printer.add();
                printer.add("> Frames being popped are:");
                printStack(printer, ((CauseStackFrameImpl) oldFrame).debugStack);
            }

            while (offset >= 0) {
                CauseStackFrameImpl f = this.frames.peek();
                if (DEBUG_CAUSE_FRAMES && offset > 0) {
                    printer.add();
                    printer.add(String.format("> Stack frame in position %s:", offset));
                    printStack(printer, f.debugStack);
                }
                popCauseFrame(f);
                offset--;
            }
            printer.trace(System.err);
            if (offset == -1) {
                // Popping a frame that was not on the stack is not recoverable
                // so we throw an exception.
                throw new IllegalStateException("Cause Stack Frame Corruption! Attempted to pop a frame that was not on the stack.");
            }
            return;
        }
        this.frames.pop();
        // Remove new values
        boolean ctxInvalid = false;
        if (frame.hasNew()) {
            frame.getNew().forEach(this.ctx::remove);
            ctxInvalid = true;
        }
        // Restore old values
        if (frame.hasStoredValues()) {
            for (Map.Entry<EventContextKey<?>, Object> e : frame.getStoredValues()) {
                this.ctx.put(e.getKey(), e.getValue());
            }
            ctxInvalid = true;
        }
        if (ctxInvalid) {
            this.cachedCtx = null;
        }
        // If there were any objects left on the stack then we pop them off
        while (this.cause.size() > this.minDepth) {
            this.cause.pop();
        }
        this.minDepth = frame.oldMinDepth;
    }

    /**
     * A custom print {@link Throwable} method to ignore
     * the first entry in the stack.
     *
     * @param printer The printer
     * @param th The throwable
     */
    private static void printStack(PrettyPrinter printer, Throwable th) {
        final String margin = Strings.repeat(" ", 4);
        final StackTraceElement[] stackTrace = th.getStackTrace();
        printer.add("%s: %s", th.getClass().getName(), th.getMessage());
        for (int i = 1; i < stackTrace.length; i++) {
            printer.add("%s%s", margin, stackTrace[i]);
        }
    }

    @Override
    public <T> CauseStack addContext(EventContextKey<T> key, T value) {
        checkNotNull(key, "key");
        checkNotNull(value, "value");
        this.cachedCtx = null;
        final Object existing = this.ctx.put(key, value);
        if (!this.frames.isEmpty()) {
            final CauseStackFrameImpl frame = this.frames.peek();
            if (existing == null) {
                frame.markNew(key);
            } else if (!frame.isNew(key) && !frame.isStored(key)) {
                frame.store(key, existing);
            }
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getContext(EventContextKey<T> key) {
        checkNotNull(key, "key");
        return Optional.ofNullable((T) this.ctx.get(key));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> removeContext(EventContextKey<T> key) {
        checkNotNull(key, "key");
        this.cachedCtx = null;
        final Object existing = this.ctx.remove(key);
        if (existing != null && !this.frames.isEmpty()) {
            final CauseStackFrameImpl frame = this.frames.peek();
            if (!frame.isNew(key)) {
                frame.store(key, existing);
            }
        }
        return Optional.ofNullable((T) existing);
    }

    // TODO could pool these for more fasts
    private static class CauseStackFrameImpl implements Frame {

        private final LanternCauseStack stack;

        // lazy loaded
        @Nullable private Map<EventContextKey<?>, Object> storedCtxValues;
        @Nullable private Set<EventContextKey<?>> newCtxValues;
        int oldMinDepth;

        @Nullable Exception debugStack;

        CauseStackFrameImpl(LanternCauseStack stack, int oldDepth) {
            this.oldMinDepth = oldDepth;
            this.stack = stack;
        }

        boolean isStored(EventContextKey<?> key) {
            return this.storedCtxValues != null && this.storedCtxValues.containsKey(key);
        }

        Set<Map.Entry<EventContextKey<?>, Object>> getStoredValues() {
            return this.storedCtxValues.entrySet();
        }

        boolean hasStoredValues() {
            return this.storedCtxValues != null && !this.storedCtxValues.isEmpty();
        }

        public void store(EventContextKey<?> key, Object existing) {
            if (this.storedCtxValues == null) {
                this.storedCtxValues = new HashMap<>();
            }
            this.storedCtxValues.put(key, existing);
        }

        boolean isNew(EventContextKey<?> key) {
            return this.newCtxValues != null && this.newCtxValues.contains(key);
        }

        Set<EventContextKey<?>> getNew() {
            return this.newCtxValues;
        }

        boolean hasNew() {
            return this.newCtxValues != null && !this.newCtxValues.isEmpty();
        }

        void markNew(EventContextKey<?> key) {
            if (this.newCtxValues == null) {
                this.newCtxValues = new HashSet<>();
            }
            this.newCtxValues.add(key);
        }

        @Override
        public void close() {
            this.stack.popCauseFrame(this);
        }

        @Override
        public Cause getCurrentCause() {
            return this.stack.getCurrentCause();
        }

        @Override
        public EventContext getCurrentContext() {
            return this.stack.getCurrentContext();
        }

        @Override
        public Frame pushCause(Object obj) {
            this.stack.pushCause(obj);
            return this;
        }

        @Override
        public Object popCause() {
            return this.stack.popCause();
        }

        @Override
        public <T> Frame addContext(EventContextKey<T> key, T value) {
            this.stack.addContext(key, value);
            return this;
        }

        @Override
        public <T> Optional<T> removeContext(EventContextKey<T> key) {
            return this.stack.removeContext(key);
        }
    }
}
