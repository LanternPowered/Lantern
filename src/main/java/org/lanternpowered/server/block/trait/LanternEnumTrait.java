package org.lanternpowered.server.block.trait;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.spongepowered.api.block.trait.EnumTrait;

import com.google.common.collect.ImmutableSet;

@SuppressWarnings("unchecked")
public final class LanternEnumTrait<E extends Enum<E>> extends LanternBlockTrait<E> implements EnumTrait<E> {

    private LanternEnumTrait(String name, Class<E> valueClass, ImmutableSet<E> possibleValues) {
        super(name, valueClass, possibleValues);
    }

    /**
     * Creates a new enum trait with the specified name and the possible values.
     * 
     * <p>The possible values array may not be empty.</p>
     * 
     * @param name the name
     * @param possibleValues the possible values
     * @return the enum trait
     */
    public static <E extends Enum<E>> EnumTrait<E> of(String name, Iterable<E> possibleValues) {
        checkNotNullOrEmpty(name, "name");
        checkNotNull(possibleValues, "possibleValues");
        checkState(possibleValues.iterator().hasNext(), "possibleValues may not be empty");

        return new LanternEnumTrait<E>(name, (Class<E>) possibleValues.iterator().getClass(),
                ImmutableSet.copyOf(possibleValues));
    }

    /**
     * Creates a new enum trait with the specified name and all the values from the enum.
     * 
     * <p>The enum must contain values.</p>
     * 
     * @param name the name
     * @param enumClass the enum class
     * @return the enum trait
     */
    public static <E extends Enum<E>> EnumTrait<E> of(String name, Class<E> enumClass) {
        checkNotNullOrEmpty(name, "name");
        checkNotNull(enumClass, "enumClass");
        checkState(enumClass.getEnumConstants().length != 0, "enumClass must contain values");

        return new LanternEnumTrait<E>(name, enumClass, ImmutableSet.copyOf(enumClass.getEnumConstants()));
    }
}
