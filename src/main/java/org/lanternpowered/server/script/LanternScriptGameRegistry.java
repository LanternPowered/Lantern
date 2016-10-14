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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilationFailedException;
import org.lanternpowered.api.script.Script;
import org.lanternpowered.api.script.ScriptGameRegistry;
import org.lanternpowered.api.script.ScriptObjectTypes;
import org.lanternpowered.server.asset.AssetRepository;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.script.json.JsonSerializers;
import org.lanternpowered.server.script.transformer.AdditionalImportsScriptTransformer;
import org.lanternpowered.server.script.transformer.FirstSuccessTransformer;
import org.lanternpowered.server.script.transformer.ImportsCollectorTransformer;
import org.lanternpowered.server.script.transformer.ReferencedScriptTransformer;
import org.lanternpowered.server.script.transformer.RelocatedMethodScriptTransformer;
import org.lanternpowered.server.script.transformer.RelocatedScriptTransformer;
import org.lanternpowered.server.script.transformer.ScriptTransformerContext;
import org.lanternpowered.server.script.transformer.SequentialTransformer;
import org.lanternpowered.server.script.transformer.SimpleScriptTransformer;
import org.lanternpowered.server.script.transformer.StripPackageNameTransformer;
import org.lanternpowered.server.script.transformer.Transformer;
import org.lanternpowered.server.script.transformer.TransformerException;
import org.lanternpowered.server.script.transformer.TransformerUtil;
import org.lanternpowered.server.world.weather.WeatherBuilder;
import org.lanternpowered.server.world.weather.WeatherBuilderJsonSerializer;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.asset.Asset;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

public class LanternScriptGameRegistry implements ScriptGameRegistry {

    private static final LanternScriptGameRegistry instance = new LanternScriptGameRegistry();

    public static LanternScriptGameRegistry get() {
        return instance;
    }

    private final ScriptFunctionGenerator scriptFunctionGenerator = new ScriptFunctionGenerator();
    private final Transformer transformerPipeline = SequentialTransformer.of(
            FirstSuccessTransformer.of(
                    new RelocatedScriptTransformer(),
                    new RelocatedMethodScriptTransformer(),
                    new SimpleScriptTransformer()
            ),
            new StripPackageNameTransformer(),
            new ImportsCollectorTransformer(),
            new ReferencedScriptTransformer(),
            new AdditionalImportsScriptTransformer()
    );
    private final Map<String, LanternScript<Object>> assetScripts = new ConcurrentHashMap<>();
    private final Map<String, LanternScript<Object>> functionAssetScripts = new ConcurrentHashMap<>();
    private final GroovyClassLoader classLoader;
    private final Map<Class<?>, Class<?>> constructorClasses = ImmutableMap.<Class<?>, Class<?>>builder()
            .put(ScriptObjectTypes.WEATHER, WeatherBuilder.class)
            .build();
    private final Gson gson = JsonSerializers.register(new GsonBuilder())
            .registerTypeAdapter(WeatherBuilder.class, new WeatherBuilderJsonSerializer())
            .setPrettyPrinting()
            .create();

    private LanternScriptGameRegistry() {
        this.classLoader = new GroovyClassLoader(this.getClass().getClassLoader());
    }

    /**
     * Gets the {@link LanternScript} instance for the
     * specified {@link Asset} id.
     *
     * @param id The asset id
     * @return The script
     */
    public Optional<LanternScript<Object>> getScript(String id) {
        return Optional.ofNullable(this.assetScripts.get(id.toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public <T extends CatalogType> T register(Asset asset, Class<T> objectType) {
        return null;
    }

    @Override
    public <T extends CatalogType> T register(Asset asset, String id, Class<T> objectType) {
        return null;
    }

    @Override
    public <T extends CatalogType> T register(Object plugin, String asset, Class<T> objectType) {
        return null;
    }

    @Override
    public <T extends CatalogType> T register(Object plugin, String asset, String id, Class<T> objectType) {
        return null;
    }

    public <T extends CatalogType> T construct(Object plugin, String asset, String id, Class<T> objectType) {
        final AssetRepository assetRepository = Lantern.getAssetRepository();
        final Asset theAsset = assetRepository.get(plugin, asset).orElseThrow(
                () -> new IllegalArgumentException("There is no asset with the specified id: " + asset));
        return this.construct(theAsset, id, objectType);
    }

    public <T extends CatalogType> T construct(Asset asset, String id, Class<T> objectType) {
        checkNotNull(asset, "asset");
        checkNotNull(objectType, "objectType");
        checkArgument(this.constructorClasses.containsKey(objectType),
                "The object type %s isn't supported!", objectType.getName());
        final Class<?> constructorClass = this.constructorClasses.get(objectType);
        final CatalogTypeConstructor<T> constructor;
        try (final InputStream is = asset.getUrl().openStream()) {
            final InputStreamReader reader = new InputStreamReader(is);
            constructor = (CatalogTypeConstructor<T>) this.gson.fromJson(reader, constructorClass);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        final String pluginId = asset.getOwner().getId();
        return constructor.create(pluginId, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Script<T> compile(Asset asset, Class<T> function) {
        final String id = ((org.lanternpowered.api.asset.Asset) asset).getId();
        return (Script<T>) this.functionAssetScripts.computeIfAbsent(id, id0 -> {
            try {
                return this.compileScript(Joiner.on('\n').join(asset.readLines()),
                        (ScriptFunctionMethod) ScriptFunctionMethod.of(function), asset, null);
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to read the asset data: " + id0);
            }
        });
    }

    @Override
    public <T> Script<T> compile(String scriptSource, Class<T> function) {
        checkNotNull(scriptSource, "scriptSource");
        return this.compileScript(scriptSource, ScriptFunctionMethod.of(function), null, null);
    }

    @Override
    public <T> Script<T> compile(Object plugin, String asset, Class<T> function) {
        final AssetRepository assetRepository = Lantern.getAssetRepository();
        final Asset theAsset = assetRepository.get(plugin, asset).orElseThrow(
                () -> new IllegalArgumentException("There is no asset with the specified id: " + asset));
        return this.compile(theAsset, function);
    }

    @Override
    public Script<Object> compile(Asset asset) {
        final String id = ((org.lanternpowered.api.asset.Asset) asset).getId();
        return this.assetScripts.computeIfAbsent(id, id0 -> {
            try {
                return this.compileScript(Joiner.on('\n').join(asset.readLines()), null, asset, null);
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to read the asset data: " + id0);
            }
        });
    }

    @Override
    public Script<Object> compile(String scriptSource) {
        checkNotNull(scriptSource, "scriptSource");
        return this.compileScript(scriptSource, null, null, null);
    }

    @Override
    public Script<Object> compile(Object plugin, String asset) {
        final AssetRepository assetRepository = Lantern.getAssetRepository();
        final Asset theAsset = assetRepository.get(plugin, asset).orElseThrow(
                () -> new IllegalArgumentException("There is no asset with the specified id: " + asset +
                        " for the specified plugin:" + plugin));
        return this.compile(theAsset);
    }

    private Script<Object> compile0(String assetId) {
        final AssetRepository assetRepository = Lantern.getAssetRepository();
        final Asset theAsset = assetRepository.get(assetId).orElseThrow(
                () -> new IllegalArgumentException("There is no asset with the specified id: " + assetId));
        return this.compile(theAsset);
    }

    private <F> LanternScript<F> compileScript(String code,
            @Nullable ScriptFunctionMethod<F> functionMethod, @Nullable Asset asset, @Nullable Script<F> script) {
        final TransformedScript transformedScript = this.transformScript(code, functionMethod,
                asset == null ? null : ((org.lanternpowered.api.asset.Asset) asset).getId());
        transformedScript.getDependencies().forEach(this::compile0);
        final Class<?> theClass;
        try {
            theClass = this.classLoader.parseClass(transformedScript.getCode(),
                    transformedScript.getClassName());
        } catch (CompilationFailedException e) {
            throw new IllegalArgumentException("Failed to compile the script source.\nOriginal code:\n``\n" + code +
                    "\n``\nTransformed code:\n``\n" + transformedScript.getCode() + "\n``", e);
        }
        LanternScript<F> script1 = (LanternScript<F>) script;
        if (script1 == null) {
            script1 = new LanternScript<>(code);
            script1.setAsset((org.lanternpowered.api.asset.Asset) asset);
            if (functionMethod != null) {
                script1.setProxyFunction((ScriptFunction) this.scriptFunctionGenerator.get(functionMethod).get(script1));
            }
            script1.setFunctionMethod(functionMethod);
        }
        try {
            script1.setFunction(theClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to instantiate the script object", e);
        }
        return script1;
    }

    /**
     * Transforms the source into something compilable.
     *
     * @param code The code
     * @return The transformed script
     */
    private TransformedScript transformScript(String code, @Nullable ScriptFunctionMethod functionMethod, @Nullable String asset) {
        final String className;
        if (asset != null) {
            className = TransformerUtil.generateClassNameFromAssetPath(asset);
        } else {
            final String name = LanternScript.class.getName();
            className = name.substring(0, name.lastIndexOf('.')) + ".gen.UnknownScript" + UUID.randomUUID().toString().replace("-", "");
        }
        final ScriptTransformerContext context = new ScriptTransformerContext(className, code, functionMethod, asset);
        try {
            this.transformerPipeline.transform(context);
        } catch (TransformerException e) {
            throw new IllegalArgumentException("Failed to transform the script source.\nCode:\n``" + code + "``", e);
        }
        // TODO: Move to pipeline
        if (functionMethod != null) {
            if (functionMethod.getFunctionClass().isInterface()) {
                context.addInterface(functionMethod.getFunctionClass());
            }
        }
        return new TransformedScript(context.compile(), className, ImmutableSet.copyOf(context.getDependencies()));
    }

    private final class TransformedScript {

        private final String code;
        private final String className;
        private final Set<String> dependencies;

        private TransformedScript(String code, String className, Set<String> dependencies) {
            this.className = className;
            this.dependencies = dependencies;
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

        public Set<String> getDependencies() {
            return this.dependencies;
        }

        public String getClassName() {
            return this.className;
        }
    }
}
