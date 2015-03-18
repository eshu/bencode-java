package bencode.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

import bencode.DictionaryParser;
import bencode.exception.InvalidDictionaryKeyOrderException;
import bencode.exception.UnexpectedCharacterException;
import bencode.handler.DictionaryEntryHandler;
import bencode.util.ByteArray;

final class BEncodeDictionaryParser implements DictionaryParser {
    private final BEncodeInputStream in;
    private Optional<DictionaryEntryHandler> handler = Optional.empty();

    public BEncodeDictionaryParser(final BEncodeInputStream in) {
        this.in = in;
    }

    @Override
    public Optional<DictionaryEntryHandler> getHandler() {
        return handler;
    }

    @Override
    public void setHandler(final DictionaryEntryHandler handler) {
        this.handler = Optional.ofNullable(handler);
    }

    void parse() throws IOException {
        ByteArray previousKey = null;
        while (true) {
            final byte[] key = in.nextDictionaryKey();
            if (key == null)
                break;
            final ByteArray currentKey = new ByteArray(key);
            if (previousKey != null) {
                if (previousKey.compareTo(currentKey) >= 0)
                    throw new InvalidDictionaryKeyOrderException();
            }
            previousKey = currentKey;
            final int tag = in.next();
            switch (tag) {
                case Tag.integer:
                    final BigInteger value = in.nextInteger();
                    handler.ifPresent(h -> h.integer(key, value));
                    break;
                case Tag.dictionary:
                    final BEncodeDictionaryParser dictionaryParser = new BEncodeDictionaryParser(in);
                    handler.ifPresent(h -> h.startDictionary(key, dictionaryParser));
                    dictionaryParser.parse();
                    handler.ifPresent(h -> h.endDictionary(key, dictionaryParser));
                    break;
                case Tag.list:
                    final BEncodeListParser listParser = new BEncodeListParser(in);
                    handler.ifPresent(h -> h.startList(key, listParser));
                    listParser.parse();
                    handler.ifPresent(h -> h.endList(key, listParser));
                    break;
                default:
                    if ((tag >= '0') || (tag <= '9')) {
                        final ByteStringInputStream bytes = in.nextString(tag);
                        handler.ifPresent(h -> h.string(key, bytes.length, bytes));
                        bytes.close();
                        break;
                    }
                    throw new UnexpectedCharacterException();
            }
        }
    }
}
