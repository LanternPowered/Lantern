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
package org.lanternpowered.server.game.registry.type.data;

import org.lanternpowered.server.data.type.record.LanternRecordType;
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.sound.SoundTypeRegistryModule;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.effect.sound.record.RecordType;
import org.spongepowered.api.effect.sound.record.RecordTypes;
import org.spongepowered.api.registry.util.RegistrationDependency;

@RegistrationDependency(SoundTypeRegistryModule.class)
public class RecordTypeRegistryModule extends InternalPluginCatalogRegistryModule<RecordType> {

    private static final RecordTypeRegistryModule INSTANCE = new RecordTypeRegistryModule();

    public static RecordTypeRegistryModule get() {
        return INSTANCE;
    }

    private RecordTypeRegistryModule() {
        super(RecordTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternRecordType("minecraft", "thirteen", "item.record.13.desc", 0, SoundTypes.RECORD_13));
        register(new LanternRecordType("minecraft", "cat", "item.record.cat.desc", 1, SoundTypes.RECORD_CAT));
        register(new LanternRecordType("minecraft", "blocks", "item.record.blocks.desc", 2, SoundTypes.RECORD_BLOCKS));
        register(new LanternRecordType("minecraft", "chirp", "item.record.chirp.desc", 3, SoundTypes.RECORD_CHIRP));
        register(new LanternRecordType("minecraft", "far", "item.record.far.desc", 4, SoundTypes.RECORD_FAR));
        register(new LanternRecordType("minecraft", "mall", "item.record.mall.desc", 5, SoundTypes.RECORD_MALL));
        register(new LanternRecordType("minecraft", "mellohi", "item.record.mellohi.desc", 6, SoundTypes.RECORD_MELLOHI));
        register(new LanternRecordType("minecraft", "stal", "item.record.stal.desc", 7, SoundTypes.RECORD_STAL));
        register(new LanternRecordType("minecraft", "strad", "item.record.strad.desc", 8, SoundTypes.RECORD_STRAD));
        register(new LanternRecordType("minecraft", "ward", "item.record.ward.desc", 9, SoundTypes.RECORD_WARD));
        register(new LanternRecordType("minecraft", "eleven", "item.record.11.desc", 10, SoundTypes.RECORD_11));
        register(new LanternRecordType("minecraft", "wait", "item.record.wait.desc", 11, SoundTypes.RECORD_WAIT));
    }
}
