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
package org.lanternpowered.server.script.transformer;

public class TransformerException extends Exception {

    public TransformerException() {
        super();
    }

    public TransformerException(String message) {
        super(message);
    }

    public TransformerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformerException(Throwable cause) {
        super(cause);
    }
}
