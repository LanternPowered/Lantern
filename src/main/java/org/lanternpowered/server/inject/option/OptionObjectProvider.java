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
package org.lanternpowered.server.inject.option;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Provider;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import joptsimple.ValueConverter;
import joptsimple.internal.Reflection;
import joptsimple.util.PathConverter;
import org.lanternpowered.api.inject.InjectionPoint;
import org.lanternpowered.api.inject.option.Flag;
import org.lanternpowered.api.inject.option.Option;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;

@SuppressWarnings("unchecked")
abstract class OptionObjectProvider<T> implements Provider<T> {

    static class PathImpl extends OptionObjectProvider<Path> {
        @Inject
        public PathImpl() {
        }

        @Override
        public Path get() {
            final File file = (File) get0();
            return file == null ? null : file.toPath();
        }

        @Override
        protected ValueConverter<Path> typeConverter() {
            return new PathConverter();
        }
    }

    static class BooleanImpl extends OptionObjectProvider<Boolean> {}
    static class ByteImpl extends OptionObjectProvider<Byte> {}
    static class DoubleImpl extends OptionObjectProvider<Double> {}
    static class FileImpl extends OptionObjectProvider<File> {}
    static class FloatImpl extends OptionObjectProvider<Float> {}
    static class IntegerImpl extends OptionObjectProvider<Integer> {}
    static class LongImpl extends OptionObjectProvider<Long> {}
    static class ShortImpl extends OptionObjectProvider<Short> {}
    static class StringImpl extends OptionObjectProvider<String> {}

    protected final TypeToken<T> type = new TypeToken<T>(this.getClass()) {};
    @Inject @Named(OptionModule.ARGUMENTS) private String[] arguments;
    @Inject private InjectionPoint injectionPoint;
    @Inject private OptionParser optionParser;

    @Nullable
    @Override
    public T get() {
        return (T) get0();
    }

    @Nullable
    protected Object get0() {
        final Option option = this.injectionPoint.getAnnotation(Option.class);
        if (option == null) {
            throw new IllegalStateException("Missing @Option annotation.");
        }
        final Flag flag = this.injectionPoint.getAnnotation(Flag.class);
        if (flag != null && !this.type.isSupertypeOf(Boolean.class)) {
            throw new IllegalStateException("The @Flag annotation can only be used for boolean options.");
        }

        final List<String> arguments = Arrays.asList(option.value());
        final OptionSpecBuilder builder = this.optionParser.acceptsAll(arguments, option.description());
        final OptionSpec optionSpec;
        if (flag != null) {
            optionSpec = builder;
        } else {
            optionSpec = builder.withRequiredArg().withValuesConvertedBy(this.typeConverter());
        }
        final OptionSet optionSet = this.optionParser.parse(this.arguments);
        if (flag != null) {
            return optionSet.has(optionSpec);
        } else {
            final Object object = optionSet.valueOf(optionSpec);
            if (this.injectionPoint.getType().getRawType() == boolean.class && object == null) {
                return false;
            } else {
                return object;
            }
        }
    }

    protected ValueConverter<T> typeConverter() {
        return Reflection.findConverter((Class<T>) this.type.getRawType());
    }
}
