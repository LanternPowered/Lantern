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
package org.lanternpowered.server.network;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

public enum ProxyType {
    /**
     * The BungeeCord proxy.
     *
     * @see <a href="BungeeCord">https://github.com/SpigotMC/BungeeCord</a>
     */
    BUNGEE_CORD ("BungeeCord"),
    /**
     * The LilyPad proxy.
     *
     * @see <a href="LilyPad">https://github.com/LilyPad</a>
     */
    LILY_PAD    ("LilyPad"),
    /**
     * The Waterfall proxy.
     *
     * @see <a href="Waterfall">https://github.com/WaterfallMC/Waterfall</a>
     */
    WATERFALL   ("Waterfall"),
    /**
     * There is no proxy in use.
     */
    NONE        ("None"),
    ;

    private final String name;

    ProxyType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static Optional<ProxyType> getByName(String name) {
        checkNotNull(name, "name");
        for (ProxyType type : values()) {
            if (type.name().equalsIgnoreCase(name) || type.name.equalsIgnoreCase(name)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
