package org.lanternpowered.server.util;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.google.common.collect.Maps;

public class Sets2 {

    public static <T> Set<T> newWeakHashSet() {
        return Collections.newSetFromMap(new WeakHashMap<T, Boolean>());
    }

    public static <T> Set<T> newWeakHashSet(Iterable<? extends T> objects) {
        Map<T, Boolean> map = Maps.newHashMap();
        for (T object : objects) {
            map.put(object, true);
        }
        return Collections.newSetFromMap(new WeakHashMap<T, Boolean>(map));
    }
}
