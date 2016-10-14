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
