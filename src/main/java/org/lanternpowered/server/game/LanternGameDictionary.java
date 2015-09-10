package org.lanternpowered.server.game;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import java.util.Map;
import java.util.Set;

import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.item.ItemType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class LanternGameDictionary implements GameDictionary {

    private final Map<String, Set<ItemType>> map = Maps.newConcurrentMap();

    @Override
    public void register(String key, ItemType type) {
        checkNotNullOrEmpty(key, "key");
        checkNotNull(type, "type");

        Set<ItemType> set = this.map.get(key);
        if (set == null) {
            set = Sets.newConcurrentHashSet();
            this.map.put(key, set);
        }
        set.add(type);
    }

    @Override
    public Set<ItemType> get(String key) {
        checkNotNullOrEmpty(key, "key");

        Set<ItemType> set = this.map.get(key);
        if (set != null) {
            return ImmutableSet.copyOf(set);
        }

        return ImmutableSet.of();
    }

    @Override
    public Map<String, Set<ItemType>> getAllItems() {
        return ImmutableMap.copyOf(this.map);
    }

}
