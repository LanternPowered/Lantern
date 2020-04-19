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

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class FirstSuccessTransformer implements Transformer {

    public static FirstSuccessTransformer of(Iterable<Transformer> transformers) {
        return new FirstSuccessTransformer(ImmutableList.copyOf(transformers));
    }

    public static FirstSuccessTransformer of(Transformer... transformers) {
        return new FirstSuccessTransformer(ImmutableList.copyOf(transformers));
    }

    private final List<Transformer> transformers;

    private FirstSuccessTransformer(List<Transformer> transformers) {
        this.transformers = transformers;
    }

    @Override
    public boolean transform(ScriptTransformerContext context) throws TransformerException {
        for (Transformer transformer : this.transformers) {
            if (transformer.transform(context)) {
                return true;
            }
        }
        return false;
    }
}
