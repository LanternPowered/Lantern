/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Joiner;
import org.lanternpowered.api.script.Parameter;
import org.lanternpowered.server.script.LanternScriptGameRegistry;
import org.spongepowered.api.plugin.Plugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RelocatedMethodScriptTransformer implements Transformer {

    private static final Pattern PATTERN = Pattern.compile(
            "^\\s*" + SCRIPT_PREFIX + "?\\s*\\{\\s*(" + Plugin.ID_PATTERN + ")\\s*,\\s*([a-zA-Z0-9\\-/]+)\\s*\\}\\s*$");

    @Override
    public boolean transform(ScriptTransformerContext context) {
        String scriptBody = context.getScriptBody();
        final Matcher matcher = PATTERN.matcher(scriptBody);
        if (!matcher.matches()) {
            return false;
        }
        final String scriptId = matcher.group(1);
        final String methodName = matcher.group(2);
        context.setScriptBody(writeMethod(context, scriptId, methodName, context.getFunctionMethod().get().getMethod()));
        return true;
    }

    static String writeMethod(ScriptTransformerContext context, String targetScriptId, String targetMethodName, Method method) {
        final StringBuilder builder = new StringBuilder();
        final String targetScriptClass = TransformerUtil.generateClassNameFromAssetPath(targetScriptId);
        context.addDependency(targetScriptId);
        writeMethod(builder, method, (theBuilder, parameterNames) ->
                theBuilder.append(String.format("((%s) %s.get().getScript(\"%s\").get().get()).%s(%s)",
                        targetScriptClass, LanternScriptGameRegistry.class.getName(), targetScriptId, targetMethodName,
                        Joiner.on(", ").join(parameterNames))));
        return builder.toString();
    }

    static String writeMethod(Method method, BiConsumer<StringBuilder, String[]> contentBuilder) {
        final StringBuilder builder = new StringBuilder();
        writeMethod(builder, method, contentBuilder);
        return builder.toString();
    }

    static void writeMethod(StringBuilder builder, Method method, BiConsumer<StringBuilder, String[]> contentBuilder) {
        builder.append("@Override\n");
        builder.append("public ").append(method.getReturnType().getName()).append(" ").append(method.getName()).append("(");
        final Type[] parameters = method.getGenericParameterTypes();
        final String[] parameterNames = getParameterNames(method);
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(parameters[i].getTypeName()).append(" ").append(parameterNames[i]);
        }
        builder.append(") {\n");
        final StringBuilder contentStringBuilder = new StringBuilder();
        contentBuilder.accept(contentStringBuilder, parameterNames);
        for (String line : contentStringBuilder.toString().split("\n")) {
            builder.append("    ").append(line).append("\n");
        }
        builder.append("}");
    }

    private static String[] getParameterNames(Method method) {
        final Class<?>[] parameters = method.getParameterTypes();
        final Annotation[][] annotations = method.getParameterAnnotations();
        final String[] parameterNames = new String[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            String name = null;
            final Set<String> usedNames = new HashSet<>();
            final Annotation[] parameterAnnotations = annotations[i];
            for (Annotation annotation : parameterAnnotations) {
                if (annotation instanceof Parameter) {
                    name = ((Parameter) annotation).value();
                }
            }
            checkState(!usedNames.contains(name), "There are multiple parameters annotated with the name %s", name);
            if (name == null) {
                int index = 0;
                do {
                    if (parameters[i].isPrimitive()) {
                        name = parameters[i].getName().substring(0, 1) + index;
                    } else {
                        name = parameters[i].getSimpleName();
                        name = name.substring(0, 1).toLowerCase() + name.substring(1);
                        if (index > 0) {
                            name += index;
                        }
                    }
                    index++;
                } while (usedNames.contains(name));
            }
            parameterNames[i] = name;
            usedNames.add(name);
        }
        return parameterNames;
    }
}
