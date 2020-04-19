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

import org.spongepowered.api.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RelocatedScriptTransformer implements Transformer {

    private static final Pattern PATTERN = Pattern.compile(
            "^\\s*" + SCRIPT_PREFIX + "?\\s*\\{\\s*(" + Plugin.ID_PATTERN + ")\\s*\\}\\s*$");

    @Override
    public boolean transform(ScriptTransformerContext context) {
        String scriptBody = context.getScriptBody();
        final Matcher matcher = PATTERN.matcher(scriptBody);
        if (!matcher.matches()) {
            return false;
        }
        final String scriptId = matcher.group(1);
        final Method method = context.getFunctionMethod().get().getMethod();
        context.setScriptBody(writeMethod(context, scriptId, method.getName(), method));
        return true;
    }
}
