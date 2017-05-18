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
package org.lanternpowered.server.data.manipulator.gen.dummy;

import org.lanternpowered.server.data.manipulator.AbstractDataManipulatorRegistration;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableAttachedData;
import org.spongepowered.api.data.manipulator.mutable.block.AttachedData;
import org.spongepowered.api.plugin.PluginContainer;

public class AttachedDataManipulatorRegistration extends AbstractDataManipulatorRegistration<AttachedData, ImmutableAttachedData> {

    public AttachedDataManipulatorRegistration(PluginContainer plugin, String id, String name) {
        super(plugin, id, name, AttachedData.class, ImmutableAttachedData.class);
    }

    @Override
    public AttachedData createMutable() {
        return new LanternAttachedDataImpl();
    }

    @Override
    public ImmutableAttachedData createImmutable() {
        return new LanternImmutableAttachedDataImpl();
    }

    @Override
    public AttachedData copyMutable(AttachedData manipulator) {
        return new LanternAttachedDataImpl(manipulator);
    }

    @Override
    public AttachedData toMutable(ImmutableAttachedData manipulator) {
        return new LanternAttachedDataImpl(manipulator);
    }

    @Override
    public ImmutableAttachedData toImmutable(AttachedData manipulator) {
        return new LanternImmutableAttachedDataImpl(manipulator);
    }
}
