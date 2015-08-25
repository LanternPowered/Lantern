package org.lanternpowered.server.network.message.codec.object.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.CodecException;

import javax.annotation.Nullable;

public interface ObjectSerializerContext {

    /**
     * Gets the byte buf allocator.
     * 
     * @return the byte buf allocator
     */
    ByteBufAllocator byteBufAlloc();

    /**
     * Attempts to write a object with the specified type.
     * 
     * @param buf the byte buffer
     * @param type the type of the object
     * @param object the object instance, can be null depending on the type
     * @return the byte buffer for chaining
     */
    <T> ByteBuf write(ByteBuf buf, Class<T> type, @Nullable T object) throws CodecException;

    /**
     * Attempts to write a object with the specified type at the specified buffer index.
     * 
     * @param buf the byte buffer
     * @param index the index to write the object at
     * @param type the type of the object
     * @param object the object instance, can be null depending on the type
     * @return the byte buffer for chaining
     */
    <T> ByteBuf writeAt(ByteBuf buf, int index, Class<T> type, @Nullable T object) throws CodecException;

    /**
     * Attempts to write a variable integer.
     * 
     * @param buf the byte buffer
     * @param value the value
     * @return the byte buffer for chaining
     */
    <T> ByteBuf writeVarInt(ByteBuf buf, int value) throws CodecException;

    /**
     * Attempts to write a variable integer at the specified buffer index.
     * 
     * @param buf the byte buffer
     * @param index the index to write the object at
     * @param value the value
     * @return the byte buffer for chaining
     */
    <T> ByteBuf writeVarIntAt(ByteBuf buf, int index, int value) throws CodecException;

    /**
     * Attempts to write a variable long.
     * 
     * @param buf the byte buffer
     * @param value the value
     * @return the byte buffer for chaining
     */
    <T> ByteBuf writeVarLong(ByteBuf buf, long value) throws CodecException;

    /**
     * Attempts to write a variable long at the specified buffer index.
     * 
     * @param buf the byte buffer
     * @param index the index to write the object at
     * @param value the value
     * @return the byte buffer for chaining
     */
    <T> ByteBuf writeVarLongAt(ByteBuf buf, int index, long value) throws CodecException;

    /**
     * Attempts to read a object with the specified type.
     * 
     * @param buf the byte buffer
     * @param type the type of the object
     * @return the object instance, can be null depending on the type
     */
    @Nullable
    <T> T read(ByteBuf buf, Class<T> type) throws CodecException;

    /**
     * Attempts to read a object with the specified type at the specified buffer index.
     * 
     * @param buf the byte buffer
     * @param index the index to read the object at
     * @param type the type of the object
     * @return the object instance, can be null depending on the type
     */
    @Nullable
    <T> T readAt(ByteBuf buf, int index, Class<T> type) throws CodecException;

    /**
     * Attempts to read a variable integer.
     * 
     * @param buf the byte buffer
     * @return the integer value
     */
    int readVarInt(ByteBuf buf) throws CodecException;

    /**
     * Attempts to read a variable integer at the specified buffer index.
     * 
     * @param buf the byte buffer
     * @param index the index to read the object at
     * @return the integer value
     */
    int readVarIntAt(ByteBuf buf, int index) throws CodecException;

    /**
     * Attempts to read a variable long.
     * 
     * @param buf the byte buffer
     * @return the integer value
     */
    long readVarLong(ByteBuf buf) throws CodecException;

    /**
     * Attempts to read a variable long at the specified buffer index.
     * 
     * @param buf the byte buffer
     * @param index the index to read the object at
     * @return the integer value
     */
    long readVarLongAt(ByteBuf buf, int index) throws CodecException;
}
