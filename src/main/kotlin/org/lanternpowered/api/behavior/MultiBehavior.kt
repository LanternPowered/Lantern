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
package org.lanternpowered.api.behavior

import com.google.common.collect.LinkedListMultimap
import org.lanternpowered.lmbda.LambdaFactory
import org.lanternpowered.lmbda.kt.lambdaType
import org.lanternpowered.lmbda.kt.privateLookupIn
import java.lang.invoke.MethodHandles
import java.lang.reflect.Modifier
import java.util.HashSet
import kotlin.reflect.KClass

/**
 * Represents a [Behavior] which is usable for multiple [BehaviorType]s.
 *
 * In all extended classes will there be searched for [BehaviorMethod] annotations
 * on the methods, these methods will be called when a specific [BehaviorType] type
 * is getting executed, multiple [BehaviorMethod] annotations are allowed.
 *
 * The signature of the method must be:
 * <pre>
 * `public boolean handleBlockBreak(BehaviorType type, BehaviorContext ctx) {
 * return false;
 * }`
 * </pre>
 * OR (the behavior type parameter is optional)
 * <pre>
 * `public boolean handleBlockBreak(BehaviorContext ctx) {
 * return false;
 * }`
 * </pre> *
 * Multiple methods with the same behavior type are allowed and
 * will be executed sequentially, one of them needs to be successful
 * in order to return `true`.
 */
abstract class MultiBehavior protected constructor() : Behavior {

    private val behaviors = LinkedListMultimap.create<BehaviorType, InternalBehavior>()

    init {
        var theClass: Class<*> = javaClass
        val added = HashSet<String>()
        // Loop through all the superclasses until we reach this class
        while (theClass != MultiBehavior::class.java) {
            val target = theClass
            val lookup = MethodHandles.lookup().privateLookupIn(target)
            val methods = theClass.declaredMethods
            for (method in methods) {
                val behaviorMethod = method.getAnnotation(BehaviorMethod::class.java) ?: continue
                val params = method.parameterTypes
                if (!Modifier.isPrivate(method.modifiers)) {
                    val name = method.name + "$$" + params.size
                    if (!added.add(name)) { // Check if the method was overridden
                        continue
                    }
                }
                if (Modifier.isStatic(method.modifiers)) {
                    throw IllegalStateException("A behavior method cannot be static." +
                            "Found invalid method ${method.name} in class ${theClass.name}")
                }
                if (params.size != 1 && params.size != 2) {
                    throw IllegalStateException("The parameter count of a behavior method must be one or two. " +
                            "Found invalid method ${method.name} in class ${theClass.name}")
                }
                if (params.size == 2 && params[0] == BehaviorType::class.java) {
                    throw IllegalStateException("The first parameter must be BehaviorType when using the two parameter signature. " +
                            "Found invalid method ${method.name} in class ${theClass.name}")
                }
                if (params[if (params.size == 2) 1 else 0] == BehaviorType::class.java) {
                    throw IllegalStateException("The second (or first) parameter must be BehaviorContext. " +
                            "Found invalid method ${method.name} in class ${theClass.name}")
                }
                val methodHandle = lookup.unreflect(method)
                val internalBehavior = if (params.size == 1) {
                    LambdaFactory.create(simpleInternalBehaviorInterface, methodHandle)
                } else {
                    LambdaFactory.create(internalBehaviorInterface, methodHandle)
                }
                for (behaviorType in behaviorMethod.value) {
                    this.behaviors.put(behaviorType.getBehaviorType(), internalBehavior)
                }
            }
            theClass = theClass.superclass
        }
    }

    /**
     * A interface that will be implemented to call behavior methods. INTERNAL use only.
     */
    protected interface InternalBehavior {

        fun apply(target: MultiBehavior, type: BehaviorType, ctx: BehaviorContext): Boolean
    }

    protected interface SimpleInternalBehavior : InternalBehavior {

        override fun apply(target: MultiBehavior, type: BehaviorType, ctx: BehaviorContext): Boolean {
            return apply(target, ctx)
        }

        fun apply(target: MultiBehavior, ctx: BehaviorContext): Boolean
    }

    override fun apply(type: BehaviorType, ctx: BehaviorContext): Boolean {
        val behaviors = this.behaviors.get(type)
        if (behaviors.isEmpty()) {
            return true
        }
        var success = false
        for (behavior in behaviors) {
            success = behavior.apply(this, type, ctx)
        }
        return success
    }

    /**
     * Represents a multi behavior method.
     *
     * @property value The behavior types that this method represents.
     */
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    protected annotation class BehaviorMethod(vararg val value: KClass<out BehaviorType>)

    companion object {

        private val internalBehaviorInterface = lambdaType<InternalBehavior>()
        private val simpleInternalBehaviorInterface = lambdaType<SimpleInternalBehavior>()
    }
}
