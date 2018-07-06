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
package org.lanternpowered.server.data;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.util.ToStringHelper;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nullable;

public class LanternDataRegistration<M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>>
        extends DefaultCatalogType implements DataRegistration<M, I>, Comparable<LanternDataRegistration<?, ?>> {

    private static String fixId(PluginContainer plugin, String id) {
        final int index = id.indexOf(':');
        if (index == -1) {
            return id;
        }
        final String p = id.substring(0, index);
        checkArgument(p.equals(plugin.getId()), "The full ID '%s' starts with a plugin ID '%s' that mismatches the target PluginContainer '%s'",
                id, p, plugin.getId());
        return id.substring(index + 1);
    }

    private final Class<M> manipulatorClass;
    private final Class<I> immutableClass;
    private final DataManipulatorBuilder<M, I> manipulatorBuilder;
    private final PluginContainer plugin;
    protected Class<? extends M> implementationClass;
    protected Class<? extends I> immutableImplementationClass;

    protected LanternDataRegistration(PluginContainer plugin, String id, String name, Class<M> manipulatorClass, Class<I> immutableClass,
            @Nullable DataManipulatorBuilder<M, I> manipulatorBuilder) {
        super(CatalogKeys.of(plugin.getId(), fixId(plugin, id), name));
        this.plugin = checkNotNull(plugin, "plugin");
        this.manipulatorClass = checkNotNull(manipulatorClass, "manipulatorClass");
        this.immutableClass = checkNotNull(immutableClass, "immutableClass");
        this.manipulatorBuilder = manipulatorBuilder == null ? createDataManipulatorBuilder() : manipulatorBuilder;
    }

    LanternDataRegistration(LanternDataRegistrationBuilder<M, I> builder) {
        super(CatalogKeys.of(checkNotNull(builder.plugin, "PluginContainer is null!").getId(),
                checkNotNull(builder.id, "Data ID is null!"),
                checkNotNull(builder.name, "Data name is null!")));
        this.manipulatorClass = checkNotNull(builder.manipulatorClass, "DataManipulator class is null!");
        this.immutableClass = checkNotNull(builder.immutableClass, "ImmutableDataManipulator class is null!");
        this.manipulatorBuilder = checkNotNull(builder.manipulatorBuilder, "DataManipulatorBuilder is null!");
        this.implementationClass = builder.implementationClass == null ? this.manipulatorClass : builder.implementationClass;
        this.immutableImplementationClass = builder.immutableImplementationClass == null ? this.immutableClass : builder.immutableImplementationClass;
        this.plugin = builder.plugin;
    }

    protected void validate() {
        Lantern.getGame().getDataManager().validateRegistration(this);
    }

    protected void register() {
        Lantern.getGame().getDataManager().register(this);
    }

    protected DataManipulatorBuilder<M, I> createDataManipulatorBuilder() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<M> getManipulatorClass() {
        return this.manipulatorClass;
    }

    @Override
    public Class<? extends M> getImplementationClass() {
        return this.implementationClass;
    }

    @Override
    public Class<I> getImmutableManipulatorClass() {
        return this.immutableClass;
    }

    @Override
    public Class<? extends I> getImmutableImplementationClass() {
        return this.immutableImplementationClass;
    }

    @Override
    public DataManipulatorBuilder<M, I> getDataManipulatorBuilder() {
        return this.manipulatorBuilder;
    }

    @Override
    public PluginContainer getPluginContainer() {
        return this.plugin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LanternDataRegistration<?, ?> that = (LanternDataRegistration<?, ?>) o;
        return Objects.equal(this.manipulatorClass, that.manipulatorClass)
                && Objects.equal(this.manipulatorClass, that.manipulatorClass)
                && Objects.equal(this.immutableClass, that.immutableClass)
                && Objects.equal(this.manipulatorBuilder, that.manipulatorBuilder)
                && Objects.equal(this.plugin, that.plugin)
                && Objects.equal(getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.manipulatorClass, this.immutableClass, this.manipulatorBuilder, getKey());
    }

    @Override
    public ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("manipulatorClass", this.manipulatorClass)
                .add("immutableClass", this.immutableClass)
                .add("manipulatorBuilder", this.manipulatorBuilder);
    }

    @Override
    public int compareTo(LanternDataRegistration<?, ?> o) {
        return getKey().compareTo(o.getKey());
    }
}
