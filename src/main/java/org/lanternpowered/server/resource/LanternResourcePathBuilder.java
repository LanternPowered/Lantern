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
package org.lanternpowered.server.resource;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.resource.LanternResourcePath.NAMESPACE_PATH_PATTERN;
import static org.lanternpowered.server.resource.LanternResourcePath.NAMESPACE_PATTERN;
import static org.lanternpowered.server.resource.LanternResourcePath.PATH_PATTERN;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import org.spongepowered.api.resource.ResourcePath;

import java.util.regex.Matcher;

@SuppressWarnings({"ConstantConditions", "NullableProblems"})
public final class LanternResourcePathBuilder implements ResourcePath.Builder {

    private String namespace;
    private String path;

    @Override
    public ResourcePath.Builder namespace(String namespace) throws IllegalArgumentException {
        checkNotNull(namespace, "namespace");
        checkState(NAMESPACE_PATTERN.matcher(namespace).matches(),
                "%s doesn't match the namespace pattern: %s", namespace, NAMESPACE_PATTERN.pattern());
        this.namespace = namespace;
        return this;
    }

    @Override
    public ResourcePath.Builder plugin(Object plugin) {
        this.namespace = checkPlugin(plugin, "plugin").getId();
        return this;
    }

    @Override
    public ResourcePath.Builder path(String path) throws IllegalArgumentException {
        checkNotNull(path, "path");
        final Matcher matcher = PATH_PATTERN.matcher(path);
        checkState(matcher.matches(),
                "%s doesn't match the path pattern: %s", path, PATH_PATTERN.pattern());
        this.namespace = matcher.group(1);
        return this;
    }

    @Override
    public ResourcePath.Builder parse(String path) throws IllegalArgumentException {
        checkNotNull(path, "path");
        final Matcher matcher = NAMESPACE_PATH_PATTERN.matcher(path);
        checkState(matcher.matches(),
                "%s doesn't match the namespaced path pattern: %s", path, NAMESPACE_PATH_PATTERN.pattern());
        this.namespace = matcher.group(1);
        this.path = matcher.group(2);
        return this;
    }

    @Override
    public ResourcePath build() throws IllegalStateException {
        checkState(this.namespace != null, "The namespace must be set");
        checkState(this.path != null, "The path must be set");
        return new LanternResourcePath(this.namespace, this.path);
    }

    @Override
    public ResourcePath.Builder from(ResourcePath value) {
        this.namespace = value.getNamespace();
        this.path = value.getPath();
        return this;
    }

    @Override
    public ResourcePath.Builder reset() {
        this.namespace = null;
        this.path = null;
        return this;
    }
}
