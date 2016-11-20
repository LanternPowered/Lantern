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
package org.lanternpowered.server.data.manipulator.immutable;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.ImmutableCommandData;
import org.spongepowered.api.data.manipulator.mutable.CommandData;
import org.spongepowered.api.data.value.immutable.ImmutableOptionalValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class LanternImmutableCommandData extends AbstractImmutableData<ImmutableCommandData, CommandData> implements ImmutableCommandData {

    public LanternImmutableCommandData() {
        super(ImmutableCommandData.class, CommandData.class);
    }

    @Override
    public void registerKeys() {
        registerKey(Keys.COMMAND, "").notRemovable();
        registerKey(Keys.SUCCESS_COUNT, 0).notRemovable();
        registerKey(Keys.TRACKS_OUTPUT, true).notRemovable();
        registerKey(Keys.LAST_COMMAND_OUTPUT, Optional.empty()).notRemovable();
    }

    @Override
    public ImmutableValue<String> storedCommand() {
        return getImmutableValue(Keys.COMMAND).get();
    }

    @Override
    public ImmutableValue<Integer> successCount() {
        return getImmutableValue(Keys.SUCCESS_COUNT).get();
    }

    @Override
    public ImmutableValue<Boolean> doesTrackOutput() {
        return getImmutableValue(Keys.TRACKS_OUTPUT).get();
    }

    @Override
    public ImmutableOptionalValue<Text> lastOutput() {
        return (ImmutableOptionalValue<Text>) getImmutableValue(Keys.LAST_COMMAND_OUTPUT).get();
    }
}
