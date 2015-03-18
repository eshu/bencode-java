package bencode.handler;

import java.io.InputStream;
import java.math.BigInteger;

import bencode.DictionaryParser;
import bencode.ListParser;

public interface ListItemHandler {
    default void startDictionary(final DictionaryParser parser) {}
    default void endDictionary(final DictionaryParser parser) {}

    default void startList(final ListParser parser) {}
    default void endList(final ListParser parser) {}

    default void integer(final BigInteger value) {}
    default void string(final BigInteger length, final InputStream in) {}
}
