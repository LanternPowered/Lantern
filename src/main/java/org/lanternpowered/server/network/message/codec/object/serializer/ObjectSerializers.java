package org.lanternpowered.server.network.message.codec.object.serializer;

import javax.annotation.Nullable;

public interface ObjectSerializers {

    /**
     * Registers a new object serializer.
     * 
     * @param type the object type
     * @param serializer the serializer
     */
    <T> void register(Class<T> type, ObjectSerializer<? super T> serializer);

    /**
     * Searches a codec serializer with exact the same type.
     * 
     * @param type the type
     * @return the codec serializer
     */
    @Nullable
    <T> ObjectSerializer<T> findExact(Class<T> type);

    /**
     * Searches a codec serializer with a type that matches, this can
     * be either a interface or superclass.
     * 
     * @param type the type
     * @return the codec serializer
     */
    @Nullable
    <T> ObjectSerializer<? super T> find(Class<T> type);
}
