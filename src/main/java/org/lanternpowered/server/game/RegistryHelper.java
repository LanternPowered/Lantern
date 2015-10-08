package org.lanternpowered.server.game;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class RegistryHelper {

    public static boolean mapFields(Class<?> apiClass, Map<String, ?> mapping, Collection<String> ignoredFields) {
        boolean mappingSuccess = true;
        for (Field field : apiClass.getDeclaredFields()) {
            if (ignoredFields.contains(field.getName())) {
                continue;
            }
            try {
                if (!mapping.containsKey(field.getName().toLowerCase())) {
                    continue;
                }
                setField(field, null, mapping.get(field.getName().toLowerCase()));
            } catch (Exception e) {
                e.printStackTrace();
                mappingSuccess = false;
            }
        }
        return mappingSuccess;
    }

    public static boolean mapFields(Class<?> apiClass, Function<String, ?> mapFunction) {
        boolean mappingSuccess = true;
        for (Field field : apiClass.getDeclaredFields()) {
            try {
                setField(field, null, mapFunction.apply(field.getName()));
            } catch (Exception e) {
                e.printStackTrace();
                mappingSuccess = false;
            }
        }
        return mappingSuccess;
    }

    private static void setField(Field field, Object target, Object object) throws Exception {
        int modifiers = field.getModifiers();

        if (Modifier.isFinal(modifiers)) {
            Field mfield = Field.class.getDeclaredField("modifiers");
            mfield.setAccessible(true);
            mfield.set(field, modifiers & ~Modifier.FINAL);
        }

        field.setAccessible(true);
        field.set(target, object);
    }

    public static boolean mapFields(Class<?> apiClass, Map<String, ?> mapping) {
        return mapFields(apiClass, mapping, Collections.emptyList());
    }

    public static boolean setFactory(Class<?> apiClass, Object factory) {
        try {
            setField(apiClass.getDeclaredField("factory"), null, factory);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
