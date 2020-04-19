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
package org.lanternpowered.server.script;

import org.lanternpowered.api.asset.Asset;
import org.lanternpowered.api.script.Script;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LanternScript<T> implements Script<T> {

    @Nullable
    private Object function;
    @Nullable
    private ScriptFunction proxyFunction;
    @Nullable
    private Asset asset;
    @Nullable
    private ScriptFunctionMethod<T> functionMethod;

    private final String code;

    public LanternScript(String code) {
        this.code = code;
    }

    @Override
    public T get() {
        //noinspection unchecked,ConstantConditions
        return (T) (this.proxyFunction == null ? this.function : this.proxyFunction);
    }

    public Object getRaw() {
        //noinspection ConstantConditions
        return this.function;
    }

    @Override
    public Optional<Asset> getAsset() {
        return Optional.ofNullable(this.asset);
    }

    @Nullable
    Object getFunction() {
        return this.function;
    }

    void setFunction(@Nullable Object function) {
        this.function = function;
    }

    @Nullable
    ScriptFunction getProxyFunction() {
        return this.proxyFunction;
    }

    void setProxyFunction(@Nullable ScriptFunction function) {
        this.proxyFunction = function;
    }

    void setAsset(@Nullable Asset asset) {
        this.asset = asset;
    }

    public String getCode() {
        return this.code;
    }

    @Nullable
    ScriptFunctionMethod<T> getFunctionMethod() {
        return this.functionMethod;
    }

    void setFunctionMethod(@Nullable ScriptFunctionMethod<T> functionMethod) {
        this.functionMethod = functionMethod;
    }
}
