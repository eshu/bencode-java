package bencode.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import bencode.exception.ByteStringUnderflowException;
import bencode.util.Blob;
import bencode.util.ByteArray;

final class BEncodeOutputStream implements AutoCloseable {
    private static final int bufferSize = 1024;

    private final OutputStream out;

    BEncodeOutputStream(final OutputStream out) {
        this.out = out;
    }

    void write(final BigInteger value) throws IOException {
        out.write(Tag.integer);
        out.write(value.toString().getBytes());
        out.write(Tag.end);
    }

    void write(final Number value) throws IOException {
        out.write(Tag.integer);
        out.write(Long.toString(value.longValue()).getBytes());
        out.write(Tag.end);
    }

    private void newString(final BigInteger length) throws IOException {
        out.write(length.toString().getBytes());
        out.write(Tag.string);
    }

    void write(final byte[] bytes) throws IOException {
        out.write(Integer.toString(bytes.length).getBytes());
        out.write(Tag.string);
        if (bytes.length == 0)
            return;
        out.write(bytes);
    }

    void write(final String str) throws IOException {
        write(str.getBytes());
    }

    void write(BigInteger length, final InputStream in) throws IOException {
        newString(length);
        if (length.signum() == 0)
            return;
        final byte[] buffer = new byte[bufferSize];
        while (true) {
            final int read = in.read(buffer);
            if (read <= 0)
                throw new ByteStringUnderflowException();
            final BigInteger newLength = length.subtract(BigInteger.valueOf(read));
            if (newLength.signum() <= 0) {
                out.write(buffer, 0, length.intValueExact());
                break;
            } else {
                out.write(buffer, 0, read);
            }
            length = newLength;
        }
    }

    void write(final Blob blob) throws IOException {
        write(blob.length, blob.bytes);
    }

    ByteStringOutputStream string(final BigInteger length) throws IOException {
        newString(length);
        return new ByteStringOutputStream(length, out);
    }

    @SuppressWarnings("unchecked")
    private void write(final Object obj) throws IOException {
        if (obj == null)
            throw new NullPointerException();
        if (obj instanceof BigInteger)
            write((BigInteger) obj);
        else if (obj instanceof Number)
            write((Number) obj);
        else if (obj instanceof byte[])
            write((byte[]) obj);
        else if (obj instanceof String)
            write((String) obj);
        else if (obj instanceof Blob)
            write((Blob) obj);
        else if (obj instanceof List)
            write((List<?>) obj);
        else if (obj instanceof Map)
            write((Map<ByteArray, ?>) obj);
        else
            throw new IllegalArgumentException(obj.getClass().getName());
    }

    void list() throws IOException {
        out.write(Tag.list);
    }

    void write(final List<?> list) throws IOException {
        list();
        for (final Object item : list)
            write(item);
        end();
    }

    void dictionary() throws IOException {
        out.write(Tag.dictionary);
    }

    void write(final Map<ByteArray, ?> map) throws IOException {
        dictionary();
        if ((map instanceof NavigableMap) && (((NavigableMap<ByteArray, ?>) map).comparator() == null))
            for (final Map.Entry<ByteArray, ?> entry : map.entrySet()) {
                write(entry.getKey().data);
                write(entry.getValue());
            }
        else {
            final ArrayList<ByteArray> keys = new ArrayList<ByteArray>(map.keySet());
            Collections.sort(keys);
            for (final ByteArray key : keys) {
                write(key.data);
                write(map.get(key));
            }
        }
        end();
    }

    void end() throws IOException {
        out.write(Tag.end);
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
