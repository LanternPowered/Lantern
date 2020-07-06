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
@file:JvmName("COnfigKt")

package org.lanternpowered.server.config

import com.google.common.base.CaseFormat
import java.util.Collections
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class NamedConfigObjectFactory<T : ConfigObject>(val factory: Factory<T>, val name: String, val description: String)

/**
 * @property name The name of the config spec, by default it will be inferred from the class name
 * @property caseFormat The case format, this is the format that will be used for
 *                      property names that are inferred from the property field name
 * @property description The description of the config spec
 */
abstract class ConfigObject(
        name: String? = null,
        val caseFormat: CaseFormat = CaseFormat.LOWER_HYPHEN,
        val description: String = ""
) {

    private var parent: ConfigObject? = null
    private val mutableSettings = mutableListOf<ConfigSetting<*>>()

    /**
     * All the settings that are registered to this spec.
     */
    val settings: List<ConfigSetting<*>> = Collections.unmodifiableList(this.mutableSettings)

    /**
     * The name of the config spec.
     */
    val name = name ?: resolveName()

    fun <T : ConfigObject> Factory<T>.with(name: String? = null, description: String? = null): NamedConfigObjectFactory<T> {
        TODO()
    }

    operator fun <T : ConfigObject> NamedConfigObjectFactory<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T> {
        TODO()
    }

    operator fun <T : ConfigObject> Factory<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T> {
        TODO()
    }

    /**
     * Resolves the default name of the config.
     */
    private fun resolveName(): String {
        val name = this::class.simpleName ?: this.javaClass.simpleName
        return CaseFormat.UPPER_CAMEL.to(this.caseFormat, name.removeSuffix("Spec"))
    }

    /**
     * Creates a new config property.
     */
    inline fun <reified T> setting(default: T, name: String, description: String = ""): ConfigSetting<T> =
            setting(default, genericTypeOf(), name, description)

    /**
     * Creates a new config property.
     */
    inline fun <reified T> setting(default: T, description: String = ""): ConfigSetting<T> =
            setting(default, genericTypeOf(), description)

    /**
     * Creates a new config property.
     */
    fun <T> setting(default: T, type: GenericType<T>, name: String, description: String = ""): ConfigSetting<T> =
            ConfigSetting(this, name, default, type, description, this.caseFormat)

    /**
     * Creates a new config property.
     */
    fun <T> setting(default: T, type: GenericType<T>, description: String = ""): ConfigSetting<T> =
            ConfigSetting(this, null, default, type, description, this.caseFormat)

    /**
     * Adds the setting.
     */
    internal fun addSetting(setting: ConfigSetting<*>) {
        this.mutableSettings += setting
    }
}

class ConfigProperty<T> : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        TODO("Not yet implemented")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        TODO("Not yet implemented")
    }
}

/**
 * Represents a property that will be used to create a [ConfigSetting].
 */
class ConfigSetting<T> internal constructor(
        private val configObject: ConfigObject,
        private val name: String?,
        private val default: T,
        private val type: GenericType<T>,
        private val description: String,
        private val caseFormat: CaseFormat
) {

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ConfigProperty<T> {
        val name = this.name ?: run {
            // Convert from lower camel property naming to the expected naming
            CaseFormat.LOWER_CAMEL.to(this.caseFormat, property.name)
        }
        return ConfigProperty()
    }
}

/**
 * Represents a generic type.
 */
inline class GenericType<T>(val type: KType)

/**
 * Constructs a new [GenericType].
 */
inline fun <reified T> genericTypeOf(): GenericType<T> = GenericType(typeOf<T>())
