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
package org.lanternpowered.server.data.key;

import org.lanternpowered.server.event.LanternEventListener;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;

import java.util.function.Predicate;

public class KeyEventListener implements LanternEventListener<ChangeDataHolderEvent.ValueChange> {

    private final EventListener<ChangeDataHolderEvent.ValueChange> listener;
    private final Predicate<DataHolder> dataHolderPredicate;
    private final Key<?> key;

    KeyEventListener(EventListener<ChangeDataHolderEvent.ValueChange> listener,
            Predicate<DataHolder> dataHolderPredicate, Key<?> key) {
        this.dataHolderPredicate = dataHolderPredicate;
        this.listener = listener;
        this.key = key;
    }

    @Override
    public Object getHandle() {
        return this.listener;
    }

    @Override
    public void handle(ChangeDataHolderEvent.ValueChange event) throws Exception {
        this.listener.handle(event);
    }

    public Predicate<DataHolder> getDataHolderPredicate() {
        return this.dataHolderPredicate;
    }

    public Key<?> getKey() {
        return this.key;
    }
}
