package org.lanternpowered.server.block.trait;

import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.spongepowered.api.block.trait.BooleanTrait;

import com.google.common.collect.ImmutableSet;

public final class LanternBooleanTrait extends LanternBlockTrait<Boolean> implements BooleanTrait {

    private final static ImmutableSet<Boolean> STATES = ImmutableSet.of(true, false);

    private LanternBooleanTrait(String name) {
        super(name, Boolean.class, STATES);
    }

    /**
     * Creates a new boolean trait with the specified name.
     * 
     * @param name the name
     * @return the boolean trait
     */
    public static BooleanTrait of(String name) {
        return new LanternBooleanTrait(checkNotNullOrEmpty(name, "name"));
    }
}
