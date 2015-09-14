package org.lanternpowered.server.block.trait;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.spongepowered.api.block.trait.IntegerTrait;

import com.google.common.collect.ImmutableSet;

public final class LanternIntegerTrait extends LanternBlockTrait<Integer> implements IntegerTrait {

    private LanternIntegerTrait(String name, ImmutableSet<Integer> possibleValues) {
        super(name, Integer.class, possibleValues);
    }

    /**
     * Creates a new integer trait with the specified name and the possible values.
     * 
     * <p>The possible values array may not be empty.</p>
     * 
     * @param name the name
     * @param possibleValues the possible values
     * @return the integer trait
     */
    public static IntegerTrait of(String name, Iterable<Integer> possibleValues) {
        checkNotNullOrEmpty(name, "name");
        checkNotNull(possibleValues, "possibleValues");
        checkState(possibleValues.iterator().hasNext(), "possibleValues may not be empty");
        return new LanternIntegerTrait(name, ImmutableSet.copyOf(possibleValues));
    }

    /**
     * Creates a new integer trait with the specified name and the values between
     * the minimum (inclusive) and the maximum (exclusive) value.
     * 
     * <p>The difference between the minimum and the maximum value must
     * be greater then zero.</p>
     * 
     * @param name the name
     * @param min the minimum value
     * @param max the maximum value
     * @return the integer trait
     */
    public static IntegerTrait ofRange(String name, int min, int max) {
        checkNotNullOrEmpty(name, "name");
        checkState(max - min > 0, "difference between min and max must be greater then zero");
        ImmutableSet.Builder<Integer> set = ImmutableSet.builder();
        for (int i = min; i <= max; i++) {
            set.add(i);
        }
        return new LanternIntegerTrait(name, set.build());
    }
}
