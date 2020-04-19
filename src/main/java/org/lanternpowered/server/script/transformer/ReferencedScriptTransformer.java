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

import org.lanternpowered.api.asset.Asset;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This transformer replaced the referenced scripts with actual compiled
 * script classes.<p>
 * For example:
 * '$<minecraft:scripts/my-script-name.sc>'
 * to
 * 'scripts.minecraft.scripts.MyScriptName'
 */
public class ReferencedScriptTransformer implements Transformer {

    private static final Pattern PATTERN = Pattern.compile("\\$<\\s*(" + Asset.ID_PATTERN + ")\\s*>\\s*");

    @Override
    public boolean transform(ScriptTransformerContext context) {
        String scriptBody = context.getScriptBody();
        final Matcher matcher = PATTERN.matcher(scriptBody);
        int last = 0;
        int offset = 0;
        while (matcher.find(last)) {
            final int start = matcher.start();
            final int end = matcher.end();
            // Get the script id the code is referencing to
            final String scriptId = matcher.group(1);
            context.addDependency(scriptId);
            // Don't loop over the same code
            last = matcher.end();
            // Replace the id with the class name
            final String className = TransformerUtil.generateClassNameFromAssetPath(scriptId);
            final String insert = String.format(
                    "((%s) org.lanternpowered.server.script.LanternScriptGameRegistry.get().getScript(\"%s\").get())", className, scriptId);
            // Replace the script reference with the class name
            scriptBody = scriptBody.substring(0, start - offset) + insert + scriptBody.substring(end - offset);
            // Increase the offset to replace chars
            offset += end - start - insert.length();
        }
        context.setScriptBody(scriptBody);
        return true;
    }
}
