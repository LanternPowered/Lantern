package org.lanternpowered.server.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class NonNullArrayList<E> extends ArrayList<E> {

    private static final long serialVersionUID = 6567438878579505932L;

    @Override
    public boolean add(E element) {
        return super.add(checkNotNull(element, "Element cannot be null"));
    }

    @Override
    public void add(int index, E element) {
        super.add(index, checkNotNull(element, "Element cannot be null"));
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        for (Iterator<? extends E> it = collection.iterator(); it.hasNext();) {
            checkNotNull(it.next(), "Element cannot be null");
        }
        return super.addAll(collection);
    }

    @Override
    public E set(int index, E element) {
        return super.set(index, checkNotNull(element, "Element cannot be null"));
    }
}