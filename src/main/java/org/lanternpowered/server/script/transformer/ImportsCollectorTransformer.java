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
