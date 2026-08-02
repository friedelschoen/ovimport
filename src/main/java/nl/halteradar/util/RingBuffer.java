package nl.halteradar.util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;

public final class RingBuffer<T> extends AbstractCollection<T> {
    private final T[] data;
    private int offset;
    private int size;

    public RingBuffer(T[] data) {
        this.data = data;
    }

    public boolean add(T value) {
        data[offset] = value;
        offset = (offset + 1) % data.length;
        if (size < data.length)
            size++;

        return true;
    }

    @Override
    public synchronized Iterator<T> iterator() {
        var snapshot = new ArrayList<T>(size);
        int start = size == data.length ? offset : 0;

        for (int i = 0; i < size; i++)
            snapshot.add(data[(start + i) % data.length]);

        return snapshot.iterator();
    }

    @Override
    public int size() {
        return size;
    }
}
