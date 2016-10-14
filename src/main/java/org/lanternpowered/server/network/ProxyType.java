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
