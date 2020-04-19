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
package org.lanternpowered.server.data.persistence;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.HeaderMode;
import org.lanternpowered.server.data.translator.ConfigurateTranslator;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.persistence.InvalidDataFormatException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.Callable;

public final class HoconDataFormat extends AbstractStringDataFormat {

    public HoconDataFormat(CatalogKey key) {
        super(key);
    }

    @Override
    public DataContainer read(String input) throws InvalidDataException, IOException {
        return readFrom(() -> new BufferedReader(new StringReader(input)));
    }

    @Override
    public DataContainer readFrom(Reader input) throws InvalidDataException, IOException {
        return readFrom(() -> createBufferedReader(input));
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
    public String write(DataView data) throws IOException {
        final StringWriter writer = new StringWriter();
        writeTo(() -> new BufferedWriter(writer), data);
        return writer.toString();
    }

    @Override
    public void writeTo(Writer output, DataView data) throws IOException {
        writeTo(() -> createBufferedWriter(output), data);
    }

    @Override
    public void writeTo(OutputStream output, DataView data) throws IOException {
        writeTo(() -> new BufferedWriter(new OutputStreamWriter(output)), data);
    }

    private static DataContainer readFrom(Callable<BufferedReader> source) throws IOException {
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setSource(source)
                .build();
        final CommentedConfigurationNode node = loader.load();
        return ConfigurateTranslator.instance().translate(node);
    }

    private static void writeTo(Callable<BufferedWriter> sink, DataView data) throws IOException {
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setHeaderMode(HeaderMode.NONE)
                .setSink(sink)
                .build();
        final ConfigurationNode node = ConfigurateTranslator.instance().translate(data);
        loader.save(node);
    }

    private static BufferedReader createBufferedReader(Reader reader) {
        if (reader instanceof BufferedReader) {
            return (BufferedReader) reader;
        }
        return new BufferedReader(reader);
    }

    private static BufferedWriter createBufferedWriter(Writer writer) {
        if (writer instanceof BufferedWriter) {
            return (BufferedWriter) writer;
        }
        return new BufferedWriter(writer);
    }
}
