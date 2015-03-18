package bencode.handler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import bencode.DictionaryParser;
import bencode.ListParser;
import bencode.handler.StreamHandler;

public final class SimpleListBuilder implements StreamHandler {
    public List<Object> list;

    public SimpleListBuilder() {
        this(new ArrayList<>());
    }

    public SimpleListBuilder(final ArrayList<Object> list) {
        this.list = list;
    }

    @Override
    public void startDictionary(final DictionaryParser parser) {
        parser.setHandler(new SimpleMapBuilder());
    }

    @Override
    public void endDictionary(final DictionaryParser parser) {
        list.add(((SimpleMapBuilder) parser.getHandler().get()).map);
    }

    @Override
    public void startList(final ListParser parser) {
        parser.setHandler(new SimpleListBuilder());
    }

    @Override
    public void endList(final ListParser parser) {
        list.add(((SimpleListBuilder) parser.getHandler().get()).list);
    }

    @Override
    public void integer(final BigInteger value) {
        list.add(value);
    }

    @Override
    public void string(final BigInteger length, final InputStream in) {
        final byte[] value = new byte[length.intValue()];
        try {
            in.read(value);
        } catch (final IOException e) {
            throw new SimpleParserException(e);
        }
        list.add(value);
    }
}
