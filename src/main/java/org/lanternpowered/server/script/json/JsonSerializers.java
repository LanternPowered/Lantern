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
package org.lanternpowered.server.script.json;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.lanternpowered.api.script.Script;
import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.api.script.function.action.ConditionalAction;
import org.lanternpowered.api.script.function.action.EmptyAction;
import org.lanternpowered.api.script.function.action.MultiAction;
import org.lanternpowered.api.script.function.condition.AndCondition;
import org.lanternpowered.api.script.function.condition.Condition;
import org.lanternpowered.api.script.function.condition.OrCondition;
import org.lanternpowered.api.script.function.value.DoubleValueProvider;
import org.lanternpowered.api.script.function.value.FloatValueProvider;
import org.lanternpowered.api.script.function.value.IntValueProvider;
import org.lanternpowered.server.script.ScriptFunction;
import org.lanternpowered.server.script.function.action.ActionTypeRegistryModule;
import org.lanternpowered.server.script.function.action.json.ActionTypeAdapterFactory;
import org.lanternpowered.server.script.function.action.json.ConditionalActionJsonSerializer;
import org.lanternpowered.server.script.function.action.json.EmptyActionJsonSerializer;
import org.lanternpowered.server.script.function.action.json.MultiActionJsonSerializer;
import org.lanternpowered.server.script.function.condition.ConditionTypeRegistryModule;
import org.lanternpowered.server.script.function.condition.json.AndConditionJsonSerializer;
import org.lanternpowered.server.script.function.condition.json.ConditionTypeAdapterFactory;
import org.lanternpowered.server.script.function.condition.json.OrConditionJsonSerializer;
import org.lanternpowered.server.script.function.value.DoubleValueProviderTypeRegistryModule;
import org.lanternpowered.server.script.function.value.FloatValueProviderTypeRegistryModule;
import org.lanternpowered.server.script.function.value.IntValueProviderTypeRegistryModule;
import org.lanternpowered.server.script.function.value.json.ConstantDoubleValueProviderJsonSerializer;
import org.lanternpowered.server.script.function.value.json.ConstantFloatValueProviderJsonSerializer;
import org.lanternpowered.server.script.function.value.json.ConstantIntValueProviderJsonSerializer;
import org.lanternpowered.server.script.function.value.json.DoubleValueProviderTypeAdapterFactory;
import org.lanternpowered.server.script.function.value.json.FloatValueProviderTypeAdapterFactory;
import org.lanternpowered.server.script.function.value.json.IntValueProviderTypeAdapterFactory;
import org.lanternpowered.server.script.function.value.json.RangeDoubleValueProviderJsonSerializer;
import org.lanternpowered.server.script.function.value.json.RangeFloatValueProviderJsonSerializer;
import org.lanternpowered.server.script.function.value.json.RangeIntValueProviderJsonSerializer;
import org.lanternpowered.server.util.option.SimpleOptionValueMap;
import org.lanternpowered.server.util.option.SimpleOptionValueMapJsonSerializer;
import org.lanternpowered.server.util.option.UnmodifiableOptionValueMap;
import org.lanternpowered.server.util.option.UnmodifiableOptionValueMapJsonSerializer;

public final class JsonSerializers {

    /**
     * Registers all the json serializers to the {@link GsonBuilder}.
     *
     * @param gsonBuilder The gson builder
     * @return The gson builder
     */
    public static GsonBuilder register(GsonBuilder gsonBuilder) {
        return gsonBuilder
                .registerTypeAdapter(Script.class, new ScriptJsonSerializer())
                .registerTypeAdapter(ScriptFunction.class, new ScriptFunctionJsonSerializer())
                // DoubleValueProvider
                .registerTypeAdapter(DoubleValueProvider.Constant.class, new ConstantDoubleValueProviderJsonSerializer())
                .registerTypeAdapter(DoubleValueProvider.Range.class, new RangeDoubleValueProviderJsonSerializer())
                .registerTypeAdapterFactory(new DoubleValueProviderTypeAdapterFactory(
                        DoubleValueProviderTypeRegistryModule.get(), TypeToken.get(DoubleValueProvider.class)))
                // FloatValueProvider
                .registerTypeAdapter(FloatValueProvider.Constant.class, new ConstantFloatValueProviderJsonSerializer())
                .registerTypeAdapter(FloatValueProvider.Range.class, new RangeFloatValueProviderJsonSerializer())
                .registerTypeAdapterFactory(new FloatValueProviderTypeAdapterFactory(
                        FloatValueProviderTypeRegistryModule.get(), TypeToken.get(FloatValueProvider.class)))
                // IntValueProvider
                .registerTypeAdapter(IntValueProvider.Constant.class, new ConstantIntValueProviderJsonSerializer())
                .registerTypeAdapter(IntValueProvider.Range.class, new RangeIntValueProviderJsonSerializer())
                .registerTypeAdapterFactory(new IntValueProviderTypeAdapterFactory(
                        IntValueProviderTypeRegistryModule.get(), TypeToken.get(IntValueProvider.class)))
                // Action
                .registerTypeAdapter(MultiAction.class, new MultiActionJsonSerializer())
                .registerTypeAdapter(ConditionalAction.class, new ConditionalActionJsonSerializer())
                .registerTypeAdapter(EmptyAction.class, new EmptyActionJsonSerializer())
                .registerTypeAdapterFactory(new ActionTypeAdapterFactory(
                        ActionTypeRegistryModule.get(), TypeToken.get(Action.class)))
                // Condition
                .registerTypeAdapter(AndCondition.class, new AndConditionJsonSerializer())
                .registerTypeAdapter(OrCondition.class, new OrConditionJsonSerializer())
                .registerTypeAdapterFactory(new ConditionTypeAdapterFactory(
                        ConditionTypeRegistryModule.get(), TypeToken.get(Condition.class)))
                // Catalog Types
                .registerTypeAdapterFactory(new CatalogTypeTypeAdapterFactory())
                .registerTypeAdapter(SimpleOptionValueMap.class, new SimpleOptionValueMapJsonSerializer())
                .registerTypeAdapter(UnmodifiableOptionValueMap.class, new UnmodifiableOptionValueMapJsonSerializer());
    }
}
