package bencode.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Optional;

import bencode.Parser;
import bencode.exception.UnexpectedCharacterException;
import bencode.handler.StreamHandler;

public final class BEncodeParser implements Parser {
    private final BEncodeInputStream in;
    private Optional<StreamHandler> handler = Optional.empty();

    public BEncodeParser(final InputStream in) {
        this.in = new BEncodeInputStream(in);
    }

    @Override
    public void parse() throws IOException {
        handler.ifPresent(h -> h.start());
        int tag;
        while ((tag = in.nextRoot()) != -1)
            switch (tag) {
                case Tag.integer:
                    final BigInteger value = in.nextInteger();
                    handler.ifPresent(h -> h.integer(value));
                    break;
                case Tag.dictionary:
                    final BEncodeDictionaryParser dictionaryParser = new BEncodeDictionaryParser(in);
                    handler.ifPresent(h -> h.startDictionary(dictionaryParser));
                    dictionaryParser.parse();
                    handler.ifPresent(h -> h.endDictionary(dictionaryParser));
                    break;
                case Tag.list:
                    final BEncodeListParser listParser = new BEncodeListParser(in);
                    handler.ifPresent(h -> h.startList(listParser));
                    listParser.parse();
                    handler.ifPresent(h -> h.endList(listParser));
                    break;
                default:
                    if ((tag >= '0') || (tag <= '9')) {
                        final ByteStringInputStream bytes = in.nextString(tag);
                        handler.ifPresent(h -> h.string(bytes.length, bytes));
                        bytes.close();
                        break;
                    }
                    throw new UnexpectedCharacterException();
            }
        handler.ifPresent(h -> h.end());
    }

    @Override
    public Optional<StreamHandler> getHandler() {
        return handler;
    }

    @Override
    public void setHandler(final StreamHandler handler) {
        this.handler = Optional.ofNullable(handler);
    }
}
