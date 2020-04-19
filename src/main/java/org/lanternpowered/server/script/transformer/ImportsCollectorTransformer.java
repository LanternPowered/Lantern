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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This transformer collects all the {@link Import}s of the script source,
 * strips them from the source and adds them to the {@link ScriptTransformerContext}.
 */
public class ImportsCollectorTransformer implements Transformer {

    private static final Pattern PATTERN = Pattern.compile(
            "\\s*import\\s+(?:(static)\\s+)?((?:(?:[a-zA-z_][a-zA-z0-9_]*)\\s*\\.\\s*)*(?:\\*|[a-zA-z_$][a-zA-z0-9_$]*)\\s*);?");

    @Override
    public boolean transform(ScriptTransformerContext context) {
        String scriptBody = context.getScriptBody();
        final Matcher matcher = PATTERN.matcher(scriptBody);
        int last = 0;
        int offset = 0;
        while (matcher.find(last)) {
            final int start = matcher.start();
            final int end = matcher.end();
            // Don't loop over the same code
            last = matcher.end();
            // Strip the import from the code
            scriptBody = scriptBody.substring(0, start - offset) + scriptBody.substring(end - offset);
            // Increase the offset to replace chars
            offset += end - start;
            // Add the import to the builder
            final String name = matcher.group(2);
            final String isStatic = matcher.group(1);
            context.addImport(Import.of(name, isStatic != null && !isStatic.isEmpty()));
        }
        context.setScriptBody(scriptBody);
        return true;
    }
}
