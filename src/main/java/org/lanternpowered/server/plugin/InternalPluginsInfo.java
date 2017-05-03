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

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.Platform;

import java.util.List;

import javax.annotation.Nullable;

public final class InternalPluginsInfo {

    public static final List<String> IDENTIFIERS = ImmutableList.of(
            Minecraft.IDENTIFIER, SpongePlatform.IDENTIFIER, Api.IDENTIFIER, Implementation.IDENTIFIER);

    public static final class Api {

        public static final String NAME = "SpongeAPI";
        public static final String IDENTIFIER = Platform.API_ID;
        @Nullable public static final String VERSION = Platform.class.getPackage().getSpecificationVersion();

        private Api() {
        }
    }

    public static final class SpongePlatform {

        public static final String NAME = "Sponge";
        public static final String IDENTIFIER = "sponge";
        @Nullable public static final String VERSION = Api.VERSION;

        private SpongePlatform() {
        }
    }

    public static final class Implementation {

        public static final String NAME = "Lantern";
        public static final String IDENTIFIER = "lantern";
        @Nullable public static final String VERSION = Platform.class.getPackage().getImplementationVersion();

        private Implementation() {
        }
    }

    public static final class Minecraft {

        public static final String NAME = "Minecraft";
        public static final String IDENTIFIER = "minecraft";
        public static final String VERSION = "17w18a";

        private Minecraft() {
        }
    }

    private InternalPluginsInfo() {
    }
}
