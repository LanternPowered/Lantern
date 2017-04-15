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
package org.lanternpowered.server.plugin;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;

import java.util.Optional;

import javax.annotation.Nullable;

public abstract class InternalPluginContainer extends AbstractPluginContainer {

    public InternalPluginContainer(String id, @Nullable String name, @Nullable String version) {
        super(id, name, version);
    }

    @Inject
    void init(LanternPluginManager pluginManager) {
        pluginManager.registerPlugin(this);
    }

    @Singleton
    public static final class Api extends InternalPluginContainer {

        @Inject private Game game;

        public Api() {
            super(InternalPluginsInfo.Api.IDENTIFIER,
                    InternalPluginsInfo.Api.NAME,
                    InternalPluginsInfo.Api.VERSION);
        }

        @Override
        public Optional<?> getInstance() {
            return Optional.of(this.game);
        }
    }

    @Singleton
    public static final class SpongePlatform extends InternalPluginContainer {

        @Inject private Game game;

        public SpongePlatform() {
            super(InternalPluginsInfo.SpongePlatform.IDENTIFIER,
                    InternalPluginsInfo.SpongePlatform.NAME,
                    InternalPluginsInfo.SpongePlatform.VERSION);
        }

        @Override
        public Optional<?> getInstance() {
            return Optional.of(this.game);
        }
    }

    @Singleton
    public static final class Minecraft extends InternalPluginContainer {

        @Inject private Server server;

        public Minecraft() {
            super(InternalPluginsInfo.Minecraft.IDENTIFIER,
                    InternalPluginsInfo.Minecraft.NAME,
                    InternalPluginsInfo.Minecraft.VERSION);
        }

        @Override
        public Optional<?> getInstance() {
            return Optional.of(this.server);
        }
    }

    @Singleton
    public static final class Implementation extends InternalPluginContainer {

        @Inject private Server server;

        public Implementation() {
            super(InternalPluginsInfo.Implementation.IDENTIFIER,
                    InternalPluginsInfo.Implementation.NAME,
                    InternalPluginsInfo.Implementation.VERSION);
        }

        @Override
        public Optional<?> getInstance() {
            return Optional.of(this.server);
        }
    }
}
