package bencode.util;

import java.util.Arrays;

public final class ByteArray implements Comparable<ByteArray> {
    public final byte[] data;

    public ByteArray(final byte[] data) {
        this.data = data;
    }

    public ByteArray(final String data) {
        this.data = data.getBytes();
    }

    public int length() {
        return data.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }

    @Override
    public int compareTo(final ByteArray o) {
        return ByteArrayComparator.instance.compare(data, o.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof ByteArray) && Arrays.equals(data, ((ByteArray) o).data);
    }
}
