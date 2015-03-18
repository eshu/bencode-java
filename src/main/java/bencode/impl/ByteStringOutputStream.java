package bencode.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

import bencode.exception.ByteStringOverflowException;
import bencode.exception.ByteStringUnderflowException;
import bencode.exception.ChildWriterNotClosedException;

final class ByteStringOutputStream extends OutputStream implements ChildWriter {
    private final BigInteger length;
    private final OutputStream out;
    private BigInteger index;

    ByteStringOutputStream(final BigInteger length, final OutputStream out) {
        this.length = length;
        this.out = out;
        index = BigInteger.ZERO;
    }

    @Override
    public void assertClosed() throws ChildWriterNotClosedException {
        if (index.compareTo(length) < 0)
            throw new ChildWriterNotClosedException();
    }

    @Override
    public void write(final int b) throws IOException {
        if (index.compareTo(length) < 0) {
            out.write(b);
            index = index.add(BigInteger.ONE);
        } else
            throw new ByteStringOverflowException();
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (b == null)
            throw new NullPointerException();
        if (len == 0)
            return;
        if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0))
            throw new IndexOutOfBoundsException();
        final BigInteger newIndex = index.add(BigInteger.valueOf(len));
        if (length.compareTo(newIndex) >= 0) {
            out.write(b, off, len);
            index = newIndex;
        } else
            throw new ByteStringOverflowException();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        if (index.compareTo(length) < 0)
            throw new ByteStringUnderflowException();
        flush();
    }
}
