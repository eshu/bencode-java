package bencode.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import bencode.DictionaryWriter;
import bencode.ListWriter;
import bencode.exception.ChildWriterNotClosedException;
import bencode.exception.InvalidDictionaryKeyOrderException;
import bencode.util.Blob;
import bencode.util.ByteArray;
import bencode.util.ByteArrayComparator;

final class BEncodeDictionaryWriter extends ContainerChildWriter implements DictionaryWriter {
    private byte[] previousKey;

    BEncodeDictionaryWriter(final BEncodeOutputStream out) {
        super(out);
    }

    void writeKey(final byte[] key) throws IOException {
        assertChildClosed();
        if ((previousKey != null) && (ByteArrayComparator.instance.compare(previousKey, key) >= 0))
            throw new InvalidDictionaryKeyOrderException();
        out.write(key);
        previousKey = key;
    }

    @Override
    public ListWriter list(final byte[] key) throws IOException {
        writeKey(key);
        out.list();
        final BEncodeListWriter list = new BEncodeListWriter(out);
        child = list;
        return list;
    }

    @Override
    public DictionaryWriter dictionary(final byte[] key) throws IOException {
        writeKey(key);
        out.dictionary();
        final BEncodeDictionaryWriter dictionary = new BEncodeDictionaryWriter(out);
        child = dictionary;
        return dictionary;
    }

    @Override
    public OutputStream string(final byte[] key, final BigInteger length) throws IOException {
        writeKey(key);
        final ByteStringOutputStream stream = out.string(length);
        child = stream;
        return stream;
    }

    @Override
    public DictionaryWriter write(final byte[] key, final BigInteger value) throws IOException {
        writeKey(key);
        out.write(value);
        return this;
    }

    @Override
    public DictionaryWriter write(final byte[] key, final byte[] string) throws IOException {
        writeKey(key);
        out.write(string);
        return this;
    }

    @Override
    public DictionaryWriter write(final byte[] key, final String string) throws IOException {
        writeKey(key);
        out.write(string);
        return this;
    }

    @Override
    public DictionaryWriter write(final byte[] key, final BigInteger length, final InputStream in) throws IOException {
        writeKey(key);
        out.write(length, in);
        return this;
    }

    @Override
    public DictionaryWriter write(final byte[] key, final Blob blob) throws IOException {
        writeKey(key);
        out.write(blob);
        return this;
    }

    @Override
    public DictionaryWriter write(final byte[] key, final List<?> list) throws IOException {
        writeKey(key);
        out.write(list);
        return this;
    }

    @Override
    public DictionaryWriter write(final byte[] key, final Map<ByteArray, ?> map) throws IOException {
        writeKey(key);
        out.write(map);
        return this;
    }

    @Override
    public void assertClosed() throws ChildWriterNotClosedException {
        if (out != null)
            throw new ChildWriterNotClosedException();
    }
}
