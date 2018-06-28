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
package org.lanternpowered.server.data.persistence;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.HeaderMode;
import org.lanternpowered.server.data.translator.ConfigurateTranslator;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public final class HoconDataFormat extends AbstractStringDataFormat {

    public HoconDataFormat(String identifier) {
        super(identifier);
    }

    @Override
    public DataContainer readFrom(Reader input) throws InvalidDataException, IOException {
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setSource(() -> ensureBuffered(input))
                .build();
        final CommentedConfigurationNode node = loader.load();
        return ConfigurateTranslator.instance().translate(node);
    }

    @Override
    public void writeTo(Writer output, DataView data) throws IOException {
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setHeaderMode(HeaderMode.NONE)
                .setSink(() -> ensureBuffered(output))
                .build();
        final ConfigurationNode node = ConfigurateTranslator.instance().translate(data);
        loader.save(node);
    }
}
