package bencode.impl;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

final class ByteStringInputStream extends InputStream {
    private static final int maxSkipBufferSize = 2048;

    final BigInteger length;
    private final InputStream in;
    private BigInteger index;

    public ByteStringInputStream(final BigInteger length, final InputStream in) {
        this.length = length;
        this.in = in;
        index = BigInteger.ZERO;
    }

    @Override
    public int read() throws IOException {
        if (index.compareTo(length) >= 0)
            return -1;
        final int value = in.read();
        if (value == -1)
            throw new EOFException();
        index = index.add(BigInteger.ONE);
        return value;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (b == null)
            throw new NullPointerException();
        if (len == 0)
            return 0;
        if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0))
            throw new IndexOutOfBoundsException();

        final BigInteger rest = length.subtract(index);
        int n;
        if (rest.compareTo(BigInteger.valueOf(len)) >= 0)
            n = len;
        else
            n = rest.intValueExact();
        if (in.read(b, 0, n) < n)
            throw new EOFException();
        index = index.add(BigInteger.valueOf(n));
        return n;
    }

    @Override
    public void close() throws IOException {
        if (index.equals(length))
            return;
        final byte[] buffer = new byte[maxSkipBufferSize];
        while (read(buffer) == maxSkipBufferSize);
        if (index.equals(length))
            return;
        throw new Error("Implementation error: index (" + index + ") != length (" + length + ")");
    }
}
