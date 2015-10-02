package org.lanternpowered.server.util;

import java.util.Arrays;

public class NibbleArray {

    private final int length;
    private final int backingArraySize;
    private final byte[] backingArray;

    /**
     * Creates a new {@link NibbleArray} of the given length, with all
     * elements initially zero.
     *
     * @param length the length of the array
     */
    public NibbleArray(int length) {
        this.length = length;
        this.backingArraySize = (length & 0x1) + (length >> 1);
        this.backingArray = new byte[this.backingArraySize];
    }

    /**
     * Creates a new {@link NibbleArray} of the given length, with all
     * elements copied from the initial content array. The lengths don't have
     * to match, if it's shorter then the remaining content be set to zero,
     * and if it's longer then will the rest of the content be ignored.
     * 
     * When {@code packed} is true, then each byte will contain two nibbles
     * and if false just one. (Stored in the 4 least significant bits.)
     *
     * @param length the length of the array
     * @param initialContent the initial content
     * @param packed whether the initial content is packed
     */
    public NibbleArray(int length, byte[] initialContent, boolean packed) {
        this.length = length;
        this.backingArraySize = (length & 0x1) + (length >> 1);
        if (packed && initialContent.length == this.backingArraySize) {
            this.backingArray = initialContent.clone();
        } else {
            this.backingArray = new byte[this.backingArraySize];
            for (int i = 0; i < this.backingArraySize; i++) {
                byte value;
                if (packed) {
                    if (i >= initialContent.length) {
                        break;
                    }
                    value = initialContent[i];
                } else {
                    int j = i << 1;
                    if (j >= initialContent.length) {
                        break;
                    }
                    value = (byte) (initialContent[j] & 0x0f);
                    if (++j < initialContent.length) {
                        value |= (initialContent[j] & 0x0f) << 4;
                    }
                }
                this.backingArray[i] = value;
            }
        }
    }

    private NibbleArray(byte[] content, int length) {
        this.backingArraySize = content.length;
        this.backingArray = content;
        this.length = length;
    }

    /**
     * Gets the length of the array.
     *
     * @return the length
     */
    public int length() {
        return this.length;
    }

    /**
     * Gets an element from the array at a given index.
     *
     * @param index the index
     * @return the element
     */
    public byte get(int index) {
        if (index >= this.length) {
            throw new IndexOutOfBoundsException();
        }

        byte value = this.backingArray[index >> 1];

        if ((index & 0x1) == 0) {
            return (byte) (value & 0x0f);
        } else {
            return (byte) ((value & 0xf0) >> 4);
        }
    }

    /**
     * Sets an element to the given value.
     *
     * @param index the index
     * @param value the new value
     */
    public void set(int index, byte value) {
        if (index >= this.length) {
            throw new IndexOutOfBoundsException();
        }

        value &= 0x0f;

        int index0 = index >> 1;
        byte previous = this.backingArray[index0];

        if ((index & 0x1) == 0) {
            this.backingArray[index0] = (byte) ((previous & 0xf0) | value);
        } else {
            this.backingArray[index0] = (byte) ((previous & 0x0f) | (value << 4));
        }
    }

    /**
     * Fills the array with the specified value.
     * 
     * @param value the value to fill with
     */
    public void fill(byte value) {
        value &= 0x0f;
        Arrays.fill(this.backingArray, (byte) ((value << 4) | value));
    }

    /**
     * Gets an array containing all the values in the array.
     *
     * If an array is provided and it is the correct length, then
     * that array will be used as the destination array.
     *
     * @param array the provided array
     * @return an array containing the values in the array
     */
    public byte[] getArray(byte[] array) {
        if (array == null || array.length != this.length) {
            array = new byte[this.length];
        }
        for (int i = 0; i < this.backingArraySize; i++) {
            byte packed = this.backingArray[i];
            int j = i << 1;
            array[j] = (byte) (packed & 0x0f);
            if (++j < this.length) {
                array[j] = (byte) (packed & 0xf0);
            }
        }
        return array;
    }

    /**
     * Gets an array containing all the values in the array.
     *
     * @return an array containing the values in the array
     */
    public byte[] getArray() {
        return this.getArray(null);
    }

    /**
     * Gets an array containing all the values in the array but packed with in each
     * byte two nibbles. If the array length isn't even, the length will be rounded
     * up and the last value will only contain one value. This means that it is
     * possible that the length and the array length may be different.
     *
     * If an array is provided and it is the correct length, then
     * that array will be used as the destination array.
     *
     * @param array the provided array
     * @return an array containing the values in the array
     */
    public byte[] getPackedArray(byte[] array) {
        if (array == null || array.length != this.backingArraySize) {
            array = new byte[this.backingArraySize];
        }
        System.arraycopy(this.backingArray, 0, array, 0, this.backingArraySize);
        return array;
    }

    /**
     * Gets an array containing all the values in the array but packed with in each
     * byte two nibbles. If the array length isn't even, the length will be rounded
     * up and the last value will only contain one value. This means that it is
     * possible that the length and the array length may be different.
     * 
     * @return an array containing the values in the array
     */
    public byte[] getPackedArray() {
        return this.getPackedArray(null);
    }

    @Override
    public NibbleArray clone() {
        return new NibbleArray(this.backingArray.clone(), this.length);
    }

}
