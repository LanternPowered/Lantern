/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.plugin;

import java.lang.annotation.Annotation;

import com.google.common.base.MoreObjects;

import org.spongepowered.api.config.DefaultConfig;

@SuppressWarnings("all")
public class ConfigFileAnnotation implements DefaultConfig {

    private final boolean shared;

    public ConfigFileAnnotation(boolean shared) {
        this.shared = shared;
    }

    @Override
    public boolean sharedRoot() {
        return this.shared;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return DefaultConfig.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultConfig)) {
            return false;
        }
        DefaultConfig that = (DefaultConfig) o;
        return this.sharedRoot() == that.sharedRoot();
    }

    @Override
    public int hashCode() {
        return (127 * "sharedRoot".hashCode()) ^ Boolean.valueOf(sharedRoot()).hashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper('@' + getClass().getName())
                .add("shared", this.shared)
                .toString();
    }

}