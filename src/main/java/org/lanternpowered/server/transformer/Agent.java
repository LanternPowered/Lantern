/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.transformer;

import static org.lanternpowered.server.transformer.ClassTransformers.exclusions;
import static org.lanternpowered.server.transformer.ClassTransformers.transformers;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public final class Agent {

    public static void premain(String agentArgs, Instrumentation instr) {
        ClassTransformers.addExclusion(new Exclusion.Package(Agent.class.getPackage().getName()));
        ClassTransformers.init();
        instr.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                for (Exclusion exclusion : exclusions) {
                    if (exclusion.isApplicableFor(className)) {
                        return classfileBuffer;
                    }
                }
                for (ClassTransformer transformer : transformers) {
                    try {
                        classfileBuffer = transformer.transform(loader, className, classfileBuffer);
                    } catch (Exception e) {
                        System.err.println(String.format("Something went wrong while transforming %s with "
                                + "the transformer %s: ", className, transformer.getClass().getName(), e));
                    }
                }
                return classfileBuffer;
            }
        });
    }

    private Agent() {
    }
}
