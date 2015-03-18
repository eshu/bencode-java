package bencode.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

import bencode.ListParser;
import bencode.exception.UnexpectedCharacterException;
import bencode.handler.ListItemHandler;

final class BEncodeListParser implements ListParser {
    private final BEncodeInputStream in;
    private Optional<ListItemHandler> handler = Optional.empty();

    public BEncodeListParser(final BEncodeInputStream in) {
        this.in = in;
    }

    @Override
    public Optional<ListItemHandler> getHandler() {
        return handler;
    }

    @Override
    public void setHandler(final ListItemHandler handler) {
        this.handler = Optional.ofNullable(handler);
    }

    void parse() throws IOException {
        int tag;
        while ((tag = in.next()) != -1)
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
    }
}
