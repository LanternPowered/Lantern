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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StripPackageNameTransformer implements Transformer {

    private static final Pattern PATTERN = Pattern.compile(
            "^(\\s*package\\s+(?:(?:(?:[a-zA-z_][a-zA-z0-9_]*)\\s*\\.\\s*)*(?:[a-zA-z_][a-zA-z0-9_]*)\\s*);?).*");

    @Override
    public boolean transform(ScriptTransformerContext context) throws TransformerException {
        String scriptBody = context.getScriptBody();
        final Matcher matcher = PATTERN.matcher(scriptBody);
        if (matcher.find()) {
            scriptBody = scriptBody.substring(matcher.end(1));
            context.setScriptBody(scriptBody);
        }
        return true;
    }
}
