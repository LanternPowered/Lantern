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
package org.lanternpowered.server.event.filter;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_6;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.lanternpowered.server.event.filter.delegate.AfterCauseFilterSourceDelegate;
import org.lanternpowered.server.event.filter.delegate.AllCauseFilterSourceDelegate;
import org.lanternpowered.server.event.filter.delegate.BeforeCauseFilterSourceDelegate;
import org.lanternpowered.server.event.filter.delegate.CancellationEventFilterDelegate;
import org.lanternpowered.server.event.filter.delegate.FilterDelegate;
import org.lanternpowered.server.event.filter.delegate.FirstCauseFilterSourceDelegate;
import org.lanternpowered.server.event.filter.delegate.GetterFilterSourceDelegate;
import org.lanternpowered.server.event.filter.delegate.HasDataFilterDelegate;
import org.lanternpowered.server.event.filter.delegate.LastCauseFilterSourceDelegate;
import org.lanternpowered.server.event.filter.delegate.ParameterFilterDelegate;
import org.lanternpowered.server.event.filter.delegate.ParameterFilterSourceDelegate;
import org.lanternpowered.server.event.filter.delegate.RootCauseFilterSourceDelegate;
import org.lanternpowered.server.event.filter.delegate.SupportsDataFilterDelegate;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.util.SystemProperties;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.After;
import org.spongepowered.api.event.filter.cause.All;
import org.spongepowered.api.event.filter.cause.Before;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Last;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.data.Has;
import org.spongepowered.api.event.filter.data.Supports;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.util.generator.GeneratorUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

final class FilterGenerator {

    private static final boolean FILTER_DEBUG = SystemProperties.get().getBooleanProperty("sponge.filter.debug");
    private static final FilterGenerator instance = new FilterGenerator();

    static FilterGenerator get() {
        return instance;
    }

    private FilterGenerator() {
    }

    byte[] generateClass(String name, Method method) {
        name = name.replace('.', '/');

        final Parameter[] params = method.getParameters();
        final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;

        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, name, null, "java/lang/Object", new String[] { Type.getInternalName(EventFilter.class) });

        final List<FilterDelegate> additional = new ArrayList<>();
        boolean cancellation = false;
        for (Annotation anno : method.getAnnotations()) {
            Object obj = filterFromAnnotation(anno.annotationType());
            if (obj == null) {
                continue;
            }
            if (obj instanceof EventTypeFilter) {
                final EventTypeFilter etf = (EventTypeFilter) obj;
                additional.add(etf.getDelegate(anno));
                if (etf == EventTypeFilter.CANCELLATION) {
                    cancellation = true;
                }
            }
        }
        if (!cancellation && Cancellable.class.isAssignableFrom(method.getParameterTypes()[0])) {
            additional.add(new CancellationEventFilterDelegate(Tristate.FALSE));
        }

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "filter", "(" + Type.getDescriptor(Event.class) + ")[Ljava/lang/Object;", null, null);
            mv.visitCode();
            // index of the next available local variable
            int local = 2;
            for (FilterDelegate eventFilter : additional) {
                local = eventFilter.write(name, cw, mv, method, local);
            }

            // local var indices of the parameters values
            final int[] plocals = new int[params.length - 1];
            for (int i = 1; i < params.length; i++) {
                final Parameter param = params[i];
                ParameterFilterSourceDelegate source = null;
                final List<ParameterFilterDelegate> paramFilters = new ArrayList<>();
                for (Annotation anno : param.getAnnotations()) {
                    Object obj = filterFromAnnotation(anno.annotationType());
                    if (obj == null) {
                        continue;
                    }
                    if (obj instanceof ParameterSource) {
                        if (source != null) {
                            throw new IllegalStateException("Cannot have multiple parameter filter source annotations (for " + param.getName() + ")");
                        }
                        source = ((ParameterSource) obj).getDelegate(anno);
                    } else if (obj instanceof ParameterFilter) {
                        paramFilters.add(((ParameterFilter) obj).getDelegate(anno));
                    }
                }
                if (source == null) {
                    throw new IllegalStateException("Cannot have additional parameters filters without a source (for " + param.getName() + ")");
                }
                if (source instanceof AllCauseFilterSourceDelegate && !paramFilters.isEmpty()) {
                    // TODO until better handling for filtering arrays is added
                    throw new IllegalStateException(
                            "Cannot have additional parameters filters without an array source (for " + param.getName() + ")");
                }
                final Tuple<Integer, Integer> localState = source.write(cw, mv, method, param, local);
                local = localState.getFirst();
                plocals[i - 1] = localState.getSecond();

                for (ParameterFilterDelegate paramFilter : paramFilters) {
                    paramFilter.write(cw, mv, method, param, plocals[i - 1]);
                }
            }

            // create the return array
            if (params.length == 1) {
                mv.visitInsn(ICONST_1);
            } else {
                mv.visitIntInsn(BIPUSH, params.length);
            }
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            // load the event into the array
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(AASTORE);
            // load all the params into the array
            for (int i = 1; i < params.length; i++) {
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, i);
                Type paramType = Type.getType(params[i].getType());
                mv.visitVarInsn(paramType.getOpcode(ILOAD), plocals[i - 1]);
                GeneratorUtils.visitBoxingMethod(mv, paramType);
                mv.visitInsn(AASTORE);
            }
            mv.visitInsn(ARETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        cw.visitEnd();
        final byte[] data = cw.toByteArray();

        if (FILTER_DEBUG) {
            final Path outDir = Paths.get(".sponge.debug.out");
            final Path outFile = outDir.resolve(name + ".class");
            if (!Files.exists(outFile.getParent())) {
                try {
                    Files.createDirectories(outFile.getParent());
                } catch (IOException e) {
                    Lantern.getLogger().warn("Unable to create the filter debug directory.", e);
                }
            }
            try (final OutputStream out = Files.newOutputStream(outFile)) {
                out.write(data);
            } catch (IOException e) {
                Lantern.getLogger().warn("Unable to create the filter debug class.", e);
            }
        }

        return data;
    }

    @Nullable
    private static Object filterFromAnnotation(Class<? extends Annotation> cls) {
        Object filter;
        if ((filter = EventTypeFilter.valueOf(cls)) != null)
            return filter;
        if ((filter = ParameterSource.valueOf(cls)) != null)
            return filter;
        if ((filter = ParameterFilter.valueOf(cls)) != null)
            return filter;
        return null;
    }

    private enum EventTypeFilter {
        CANCELLATION(IsCancelled.class),
        ;

        private final Class<? extends Annotation> cls;

        EventTypeFilter(Class<? extends Annotation> cls) {
            this.cls = cls;
        }

        public FilterDelegate getDelegate(Annotation anno) {
            if (this == CANCELLATION) {
                return new CancellationEventFilterDelegate(((IsCancelled) anno).value());
            }
            throw new UnsupportedOperationException();
        }

        @Nullable
        public static EventTypeFilter valueOf(Class<? extends Annotation> cls) {
            for (EventTypeFilter value : values()) {
                if (value.cls.equals(cls)) {
                    return value;
                }
            }
            return null;
        }
    }

    private enum ParameterSource {
        CAUSE_FIRST(First.class),
        CAUSE_LAST(Last.class),
        CAUSE_BEFORE(Before.class),
        CAUSE_AFTER(After.class),
        CAUSE_ALL(All.class),
        CAUSE_ROOT(Root.class),
        GETTER(Getter.class),
        ;

        private final Class<? extends Annotation> cls;

        ParameterSource(Class<? extends Annotation> cls) {
            this.cls = cls;
        }

        public ParameterFilterSourceDelegate getDelegate(Annotation anno) {
            if (this == CAUSE_FIRST) {
                return new FirstCauseFilterSourceDelegate((First) anno);
            }
            if (this == CAUSE_LAST) {
                return new LastCauseFilterSourceDelegate((Last) anno);
            }
            if (this == CAUSE_BEFORE) {
                return new BeforeCauseFilterSourceDelegate((Before) anno);
            }
            if (this == CAUSE_AFTER) {
                return new AfterCauseFilterSourceDelegate((After) anno);
            }
            if (this == CAUSE_ALL) {
                return new AllCauseFilterSourceDelegate((All) anno);
            }
            if (this == CAUSE_ROOT) {
                return new RootCauseFilterSourceDelegate((Root) anno);
            }
            if (this == GETTER) {
                return new GetterFilterSourceDelegate((Getter) anno);
            }
            throw new UnsupportedOperationException();
        }

        @Nullable
        public static ParameterSource valueOf(Class<? extends Annotation> cls) {
            for (ParameterSource value : values()) {
                if (value.cls.equals(cls)) {
                    return value;
                }
            }
            return null;
        }
    }

    private enum ParameterFilter {
        SUPPORTS(Supports.class),
        HAS(Has.class),
        ;

        private final Class<? extends Annotation> cls;

        ParameterFilter(Class<? extends Annotation> cls) {
            this.cls = cls;
        }

        public ParameterFilterDelegate getDelegate(Annotation anno) {
            if (this == SUPPORTS) {
                return new SupportsDataFilterDelegate((Supports) anno);
            }
            if (this == HAS) {
                return new HasDataFilterDelegate((Has) anno);
            }
            throw new UnsupportedOperationException();
        }

        @Nullable
        public static ParameterFilter valueOf(Class<? extends Annotation> cls) {
            for (ParameterFilter value : values()) {
                if (value.cls.equals(cls)) {
                    return value;
                }
            }
            return null;
        }
    }
}
