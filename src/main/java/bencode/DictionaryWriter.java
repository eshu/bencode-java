package bencode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import bencode.util.Blob;
import bencode.util.ByteArray;

public interface DictionaryWriter extends AutoCloseable {
    ListWriter list(byte[] key) throws IOException;
    DictionaryWriter dictionary(byte[] key) throws IOException;

    OutputStream string(byte[] key, BigInteger length) throws IOException;

    DictionaryWriter write(byte[] key, BigInteger value) throws IOException;
    DictionaryWriter write(byte[] key, byte[] string) throws IOException;
    DictionaryWriter write(byte[] key, String string) throws IOException;
    DictionaryWriter write(byte[] key, BigInteger length, InputStream in) throws IOException;
    DictionaryWriter write(byte[] key, Blob blob) throws IOException;
    DictionaryWriter write(byte[] key, List<?> list) throws IOException;
    DictionaryWriter write(byte[] key, Map<ByteArray, ?> map) throws IOException;

    default ListWriter list(final String key) throws IOException { return list(key.getBytes()); }
    default DictionaryWriter dictionary(final String key) throws IOException { return dictionary(key.getBytes()); }

    default OutputStream string(final String key, final BigInteger length) throws IOException { return string(key.getBytes(), length); }

    default DictionaryWriter write(final String key, final BigInteger value) throws IOException { return write(key.getBytes(), value); }
    default DictionaryWriter write(final String key, final byte[] string) throws IOException { return write(key.getBytes(), string); }
    default DictionaryWriter write(final String key, final String string) throws IOException { return write(key.getBytes(), string); }
    default DictionaryWriter write(final String key, final BigInteger length, final InputStream in) throws IOException { return write(key.getBytes(), length, in); }
    default DictionaryWriter write(final String key, final Blob blob) throws IOException { return write(key.getBytes(), blob); }
    default DictionaryWriter write(final String key, final List<?> list) throws IOException  { return write(key.getBytes(), list); }
    default DictionaryWriter write(final String key, final Map<ByteArray, ?> map) throws IOException  { return write(key.getBytes(), map); }

    @Override
    void close() throws IOException;
}
