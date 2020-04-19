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

public interface Transformer {

    String SCRIPT_PREFIX = "@";

    /**
     * Transforms the script source.
     *
     * @param context The script builder context
     * @return Whether the script has been transformed
     */
    boolean transform(ScriptTransformerContext context) throws TransformerException;
}
