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

import static org.lanternpowered.server.script.transformer.RelocatedMethodScriptTransformer.writeMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleScriptTransformer implements Transformer {

    private static final Pattern PATTERN = Pattern.compile(
            "^\\s*" + SCRIPT_PREFIX + "?");

    @Override
    public boolean transform(ScriptTransformerContext context) {
        String scriptBody = context.getScriptBody();
        // TODO: Replace this by method pattern checking, etc.
        // Is there a chance that there would be multiline
        // scripts in json objects?
        if (scriptBody.contains("\n")) {
            return false;
        }
        final Matcher matcher = PATTERN.matcher(scriptBody);
        if (matcher.find()) {
            scriptBody = scriptBody.substring(matcher.end());
        }
        final String scriptBody0 = scriptBody;
        context.setScriptBody(writeMethod(context.getFunctionMethod().get().getMethod(),
                (theBuilder, parameterNames) -> theBuilder.append(scriptBody0)));
        return true;
    }
}
