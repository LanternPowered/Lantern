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
package org.lanternpowered.server.network.entity.vanilla;

import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.EntityProtocolInitContext;
import org.lanternpowered.server.network.entity.NetworkIdHolder;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.spongepowered.api.data.Keys;

public class EnderDragonEntityProtocol<E extends LanternEntity> extends CreatureEntityProtocol<E> {

    private static final int PART_HEAD = 0;
    private static final int PART_NECK = 1;
    private static final int PART_BODY = 2;
    private static final int PART_TAIL1 = 3;
    private static final int PART_TAIL2 = 4;
    private static final int PART_TAIL3 = 5;
    private static final int PART_WING1 = 6;
    private static final int PART_WING2 = 7;

    private final int[] partEntityIds = new int[8];

    public EnderDragonEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected String getMobType() {
        return "minecraft:ender_dragon";
    }

    @Override
    protected void init(EntityProtocolInitContext context) {
        checkState(!(this.entity instanceof NetworkIdHolder), "EnderDragons cannot have a predefined network id.");
        // A ender dragon uses 9 entity ids
        final int[] ids = new int[this.partEntityIds.length + 1];
        context.acquireRow(ids);
        this.initRootId(ids[0]);
        System.arraycopy(ids, 1, this.partEntityIds, 0, this.partEntityIds.length);
    }

    @Override
    protected void remove(EntityProtocolInitContext context) {
        super.remove(context);
        context.release(this.partEntityIds);
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        // TODO: Send phase
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);
        // TODO: Update phase
    }

    @Override
    boolean isSilent() {
        // Override the silent method, due the complexity of this
        // entity, it would be hard to play the sounds ourselves
        // The sounds have to be synced to the wing animations, etc.
        return this.entity.get(Keys.IS_SILENT).orElse(false);
    }
}
