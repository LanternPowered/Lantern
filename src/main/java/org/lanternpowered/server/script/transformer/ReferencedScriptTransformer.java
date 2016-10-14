/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
