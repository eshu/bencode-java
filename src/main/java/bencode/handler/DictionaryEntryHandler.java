package bencode.handler;

import java.io.InputStream;
import java.math.BigInteger;

import bencode.DictionaryParser;
import bencode.ListParser;

public interface DictionaryEntryHandler {
    default void startDictionary(final byte[] key, final DictionaryParser parser) {}
    default void endDictionary(final byte[] key, final DictionaryParser parser) {}

    default void startList(final byte[] key, final ListParser parser) {}
    default void endList(final byte[] key, final ListParser parser) {}

    default void integer(final byte[] key, final BigInteger value) {}
    default void string(final byte[] key, final BigInteger length, final InputStream in) {}
}
