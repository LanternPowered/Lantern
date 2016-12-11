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
package org.lanternpowered.server.data.manipulator.mutable.item;

import org.lanternpowered.server.data.manipulator.mutable.AbstractData;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableHideData;
import org.spongepowered.api.data.manipulator.mutable.item.HideData;
import org.spongepowered.api.data.value.mutable.Value;

public class LanternHideData extends AbstractData<HideData, ImmutableHideData> implements HideData {

    public LanternHideData() {
        super(HideData.class, ImmutableHideData.class);
    }

    public LanternHideData(ImmutableHideData manipulator) {
        super(manipulator);
    }

    public LanternHideData(HideData manipulator) {
        super(manipulator);
    }

    @Override
    public void registerKeys() {
        registerKey(Keys.HIDE_ENCHANTMENTS, false).notRemovable();
        registerKey(Keys.HIDE_ATTRIBUTES, false).notRemovable();
        registerKey(Keys.HIDE_UNBREAKABLE, false).notRemovable();
        registerKey(Keys.HIDE_CAN_DESTROY, false).notRemovable();
        registerKey(Keys.HIDE_CAN_PLACE, false).notRemovable();
        registerKey(Keys.HIDE_MISCELLANEOUS, false).notRemovable();
    }

    @Override
    public Value<Boolean> hideEnchantments() {
        return getValue(Keys.HIDE_ENCHANTMENTS).get();
    }

    @Override
    public Value<Boolean> hideAttributes() {
        return getValue(Keys.HIDE_ATTRIBUTES).get();
    }

    @Override
    public Value<Boolean> hideUnbreakable() {
        return getValue(Keys.HIDE_UNBREAKABLE).get();
    }

    @Override
    public Value<Boolean> hideCanDestroy() {
        return getValue(Keys.HIDE_CAN_DESTROY).get();
    }

    @Override
    public Value<Boolean> hideCanPlace() {
        return getValue(Keys.HIDE_CAN_PLACE).get();
    }

    @Override
    public Value<Boolean> hideMiscellaneous() {
        return getValue(Keys.HIDE_MISCELLANEOUS).get();
    }
}
