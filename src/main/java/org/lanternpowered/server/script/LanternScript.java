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
