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

import com.google.inject.Inject;
import com.google.inject.Provider;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import org.lanternpowered.server.inject.Flag;
import org.lanternpowered.server.inject.InjectionPoint;
import org.lanternpowered.server.inject.Option;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;

abstract class OptionObjectProvider<T> implements Provider<T> {

    static class IntegerImpl extends OptionObjectProvider<Integer> {
        @Inject
        public IntegerImpl() {
            super(Integer.class);
        }
    }

    static class DoubleImpl extends OptionObjectProvider<Double> {
        @Inject
        public DoubleImpl() {
            super(Double.class);
        }
    }

    static class FloatImpl extends OptionObjectProvider<Float> {
        @Inject
        public FloatImpl() {
            super(Float.class);
        }
    }

    static class LongImpl extends OptionObjectProvider<Long> {
        @Inject
        public LongImpl() {
            super(Long.class);
        }
    }

    static class ShortImpl extends OptionObjectProvider<Short> {
        @Inject
        public ShortImpl() {
            super(Short.class);
        }
    }

    static class BooleanImpl extends OptionObjectProvider<Boolean> {
        @Inject
        public BooleanImpl() {
            super(Boolean.class);
        }
    }

    static class StringImpl extends OptionObjectProvider<String> {
        @Inject
        public StringImpl() {
            super(String.class);
        }
    }

    static class ByteImpl extends OptionObjectProvider<Byte> {
        @Inject
        public ByteImpl() {
            super(Byte.class);
        }
    }

    static class FileImpl extends OptionObjectProvider<File> {
        @Inject
        public FileImpl() {
            super(File.class);
        }
    }

    static class PathImpl extends OptionObjectProvider<Path> {
        @Inject
        public PathImpl() {
            super(File.class);
        }

        @Override
        public Path get() {
            final File file = (File) get0();
            return file == null ? null : file.toPath();
        }
    }

    @Inject @Named(OptionModule.ARGUMENTS) private String[] arguments;
    @Inject private InjectionPoint injectionPoint;
    @Inject private OptionParser optionParser;

    private final Class<?> valueType;

    OptionObjectProvider(Class<?> valueType) {
        this.valueType = valueType;
    }

    @Nullable
    @Override
    public T get() {
        //noinspection unchecked
        return (T) get0();
    }

    @Nullable
    protected Object get0() {
        final Option option = this.injectionPoint.getAnnotation(Option.class);
        if (option == null) {
            throw new IllegalStateException("Missing @Option annotation.");
        }
        final Flag flag = this.injectionPoint.getAnnotation(Flag.class);
        if (flag != null && !this.valueType.equals(Boolean.class)) {
            throw new IllegalStateException("The @Flag annotation can only be used for boolean options.");
        }

        final List<String> arguments = Arrays.asList(option.value());
        final OptionSpecBuilder builder = this.optionParser.acceptsAll(arguments, option.description());
        final OptionSpec optionSpec;
        if (flag != null) {
            optionSpec = builder;
        } else {
            optionSpec = builder.withRequiredArg().ofType(this.valueType);
        }
        final OptionSet optionSet = this.optionParser.parse(this.arguments);
        if (flag != null) {
            //noinspection unchecked
            return optionSet.has(optionSpec);
        } else {
            //noinspection unchecked
            final Object object = optionSet.valueOf(optionSpec);
            if (this.injectionPoint.getType().getRawType() == boolean.class && object == null) {
                return false;
            } else {
                return object;
            }
        }
    }
}
