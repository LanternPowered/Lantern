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
package org.lanternpowered.server.inject.option;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.PrivateBinder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.name.Names;
import joptsimple.OptionParser;
import org.lanternpowered.api.inject.option.Option;
import org.lanternpowered.server.inject.InjectionPointProvider;

import java.io.File;
import java.nio.file.Path;

/**
 * Requires the {@link InjectionPointProvider} to be installed.
 */
public abstract class OptionModule extends AbstractModule {

    public static final String ARGUMENTS = "launch-arguments";

    @Override
    protected final void configure() {
        bindOption(Integer.class)
                .toProvider(OptionObjectProvider.IntegerImpl.class);
        bindOption(Long.class)
                .toProvider(OptionObjectProvider.LongImpl.class);
        bindOption(Double.class)
                .toProvider(OptionObjectProvider.DoubleImpl.class);
        bindOption(Short.class)
                .toProvider(OptionObjectProvider.ShortImpl.class);
        bindOption(String.class)
                .toProvider(OptionObjectProvider.StringImpl.class);
        bindOption(Boolean.class)
                .toProvider(OptionObjectProvider.BooleanImpl.class);
        bindOption(Byte.class)
                .toProvider(OptionObjectProvider.ByteImpl.class);
        bindOption(Float.class)
                .toProvider(OptionObjectProvider.FloatImpl.class);
        bindOption(File.class)
                .toProvider(OptionObjectProvider.FileImpl.class);
        bindOption(Path.class)
                .toProvider(OptionObjectProvider.PathImpl.class);

        configure0();
    }

    protected void exposeOptions() {
        final Binder binder = binder();
        if (!(binder instanceof PrivateBinder)) {
            return;
        }
        exposeOption(Integer.class);
        exposeOption(Long.class);
        exposeOption(Double.class);
        exposeOption(Short.class);
        exposeOption(String.class);
        exposeOption(Boolean.class);
        exposeOption(Byte.class);
        exposeOption(Float.class);
        exposeOption(Integer.class);
        exposeOption(File.class);
        exposeOption(Path.class);
    }

    private <T> void exposeOption(Class<T> type) {
        ((PrivateBinder) binder()).expose(type).annotatedWith(Option.class);
    }

    private <T> LinkedBindingBuilder<T> bindOption(Class<T> type) {
        return bind(type).annotatedWith(Option.class);
    }

    protected final LinkedBindingBuilder<OptionParser> bindParser() {
        return bind(OptionParser.class);
    }

    protected final LinkedBindingBuilder<String[]> bindArguments() {
        return bind(String[].class).annotatedWith(Names.named(ARGUMENTS));
    }

    protected abstract void configure0();
}
