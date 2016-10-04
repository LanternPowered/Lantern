/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.HeaderMode;
import org.lanternpowered.server.data.translator.ConfigurateTranslator;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.InvalidDataFormatException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public final class HoconDataFormat extends AbstractDataFormat {

    public HoconDataFormat(String identifier) {
        super(identifier);
    }

    @Override
    public DataContainer readFrom(InputStream input) throws InvalidDataFormatException, IOException {
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setSource(() -> new BufferedReader(new InputStreamReader(input)))
                .build();
        final ConfigurationNode node = loader.load();
        return ConfigurateTranslator.instance().translate(node);
    }

    @Override
    public void writeTo(OutputStream output, DataView data) throws IOException {
        final ConfigurationNode node = ConfigurateTranslator.instance().translate(data);
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setSink(() -> new BufferedWriter(new OutputStreamWriter(output)))
                .setHeaderMode(HeaderMode.NONE)
                .build();
        loader.save(node);
    }

}
