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
