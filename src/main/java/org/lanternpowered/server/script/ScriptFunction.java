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
package org.lanternpowered.server.script;

import com.google.gson.Gson;
import org.lanternpowered.api.script.Script;
import org.lanternpowered.api.script.function.condition.Condition;

/**
 * This function is the base interface for all the generated
 * {@link Script#get()} instances. The implementation classes will be automatically
 * generated based on the function class, and the function method will
 * be delegated.
 * The actual generated object is never directly passed through in the {@link Script#get()}
 * method, but this proxy function instead to allow dynamic reloading of scripts. This makes
 * to also possible to serialize the function with {@link Gson}.
 * <p>
 * The script will be generated in the following way, for example is the
 * {@link Condition} used.
 * <pre>
 * {@code
 *  public class ConditionScriptFunctionImpl implements ScriptFunction, Condition {
 *
 *      private final LanternScript script;
 *
 *      public ConditionScriptFunctionImpl(LanternScript script) {
 *          this.script = script;
 *      }
 *
 *      @Override
 *      public LanternScript getScript() {
 *          return this.script;
 *      }
 *
 *      @Override
 *      public boolean test(ScriptContext context) {
 *          return ((Condition) this.script.getFunction()).test(context);
 *      }
 *  }
 * }
 * </pre>
 */
public interface ScriptFunction {

    /**
     * Gets the {@link LanternScript}.
     *
     * @return The script
     */
    LanternScript getScript();
}
