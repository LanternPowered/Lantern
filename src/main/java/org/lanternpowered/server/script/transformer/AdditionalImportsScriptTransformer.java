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

import org.lanternpowered.api.script.Import;
import org.lanternpowered.api.script.context.Parameters;
import org.lanternpowered.api.world.World;
import org.lanternpowered.server.script.LanternRandom;

public class AdditionalImportsScriptTransformer implements Transformer {

    @Override
    public boolean transform(ScriptTransformerContext context) throws TransformerException {
        context.addImport(Import.ofField(LanternRandom.class, "$random"));
        context.addImport(Import.ofClass(World.class));
        context.addImport(Import.ofClass(Parameters.class));
        return true;
    }
}
