package bencode.handler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import bencode.DictionaryParser;
import bencode.ListParser;
import bencode.handler.DictionaryEntryHandler;
import bencode.util.ByteArray;

public final class SimpleMapBuilder implements DictionaryEntryHandler {
    public final Map<ByteArray, Object> map;

    public SimpleMapBuilder() {
        this(new HashMap<>());
    }

    public SimpleMapBuilder(final HashMap<ByteArray, Object> map) {
        this.map = map;
    }

    @Override
    public void startDictionary(final byte[] key, final DictionaryParser parser) {
        parser.setHandler(new SimpleMapBuilder());
    }

    @Override
    public void endDictionary(final byte[] key, final DictionaryParser parser) {
        map.put(new ByteArray(key), ((SimpleMapBuilder) parser.getHandler().get()).map);
    }

    @Override
    public void startList(final byte[] key, final ListParser parser) {
        parser.setHandler(new SimpleListBuilder());
    }

    @Override
    public void endList(final byte[] key, final ListParser parser) {
        map.put(new ByteArray(key), ((SimpleListBuilder) parser.getHandler().get()).list);
    }

    @Override
    public void integer(final byte[] key, final BigInteger value) {
        map.put(new ByteArray(key), value);
    }

    @Override
    public void string(final byte[] key, final BigInteger length, final InputStream in) {
        final byte[] value = new byte[length.intValue()];
        try {
            in.read(value);
        } catch (final IOException e) {
            throw new SimpleParserException(e);
        }
        map.put(new ByteArray(key), value);
    }
}
