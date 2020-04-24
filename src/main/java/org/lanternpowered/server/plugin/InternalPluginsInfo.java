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
package org.lanternpowered.server.plugin;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.Platform;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class InternalPluginsInfo {

    public static final List<String> IDENTIFIERS = ImmutableList.of(
            Minecraft.IDENTIFIER, SpongePlatform.IDENTIFIER, Api.IDENTIFIER, Implementation.IDENTIFIER);

    public static final class Api {

        public static final String IDENTIFIER = Platform.API_ID;
        @Nullable public static final String VERSION = Platform.class.getPackage().getSpecificationVersion();

        private Api() {
        }
    }

    public static final class SpongePlatform {

        public static final String IDENTIFIER = "sponge";
        @Nullable public static final String VERSION = Api.VERSION;

        private SpongePlatform() {
        }
    }

    public static final class Implementation {

        public static final String IDENTIFIER = "lantern";
        @Nullable public static final String VERSION = Platform.class.getPackage().getImplementationVersion();

        private Implementation() {
        }
    }

    public static final class Minecraft {

        public static final String IDENTIFIER = "minecraft";
        public static final String VERSION = "20w17a";

        private Minecraft() {
        }
    }

    private InternalPluginsInfo() {
    }
}
