package bencode.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import bencode.DictionaryWriter;
import bencode.ListWriter;
import bencode.Writer;
import bencode.util.Blob;
import bencode.util.ByteArray;

public final class BEncodeWriter extends ParentWriter implements Writer {
    public BEncodeWriter(final OutputStream out) {
        super(new BEncodeOutputStream(out));
    }

    @Override
    public ListWriter list() throws IOException {
        assertChildClosed();
        out.list();
        final BEncodeListWriter list = new BEncodeListWriter(out);
        child = list;
        return list;
    }

    @Override
    public DictionaryWriter dictionary() throws IOException {
        assertChildClosed();
        out.dictionary();
        final BEncodeDictionaryWriter dictionary = new BEncodeDictionaryWriter(out);
        child = dictionary;
        return dictionary;
    }

    @Override
    public OutputStream string(final BigInteger length) throws IOException {
        assertChildClosed();
        final ByteStringOutputStream stream = out.string(length);
        child = stream;
        return stream;
    }

    @Override
    public ListWriter write(final BigInteger value) throws IOException {
        assertChildClosed();
        out.write(value);
        return this;
    }

    @Override
    public ListWriter write(final byte[] string) throws IOException {
        assertChildClosed();
        out.write(string);
        return this;
    }

    @Override
    public ListWriter write(final String string) throws IOException {
        assertChildClosed();
        out.write(string);
        return this;
    }

    @Override
    public ListWriter write(final BigInteger length, final InputStream in) throws IOException {
        assertChildClosed();
        out.write(length, in);
        return this;
    }

    @Override
    public ListWriter write(final Blob blob) throws IOException {
        assertChildClosed();
        out.write(blob);
        return this;
    }

    @Override
    public ListWriter write(final List<?> list) throws IOException {
        assertChildClosed();
        out.write(list);
        return this;
    }

    @Override
    public ListWriter write(final Map<ByteArray, ?> map) throws IOException {
        assertChildClosed();
        out.write(map);
        return this;
    }

    @Override
    public void close() throws IOException {
        super.close();
        out.close();
    }
}
