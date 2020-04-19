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

public final class SequentialTransformer implements Transformer {

    public static SequentialTransformer of(Iterable<Transformer> transformers) {
        return new SequentialTransformer(ImmutableList.copyOf(transformers));
    }

    public static SequentialTransformer of(Transformer... transformers) {
        return new SequentialTransformer(ImmutableList.copyOf(transformers));
    }

    private final List<Transformer> transformers;

    private SequentialTransformer(List<Transformer> transformers) {
        this.transformers = transformers;
    }

    @Override
    public boolean transform(ScriptTransformerContext context) throws TransformerException {
        for (Transformer transformer : this.transformers) {
            transformer.transform(context);
        }
        return true;
    }
}
