package bencode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import bencode.util.Blob;
import bencode.util.ByteArray;

public interface ListWriter extends AutoCloseable {
    ListWriter list() throws IOException;
    DictionaryWriter dictionary() throws IOException;

    OutputStream string(BigInteger length) throws IOException;

    ListWriter write(BigInteger value) throws IOException;
    ListWriter write(byte[] string) throws IOException;
    ListWriter write(String string) throws IOException;
    ListWriter write(BigInteger length, InputStream in) throws IOException;
    ListWriter write(Blob blob) throws IOException;
    ListWriter write(List<?> list) throws IOException;
    ListWriter write(Map<ByteArray, ?> map) throws IOException;

    @Override
    void close() throws IOException;
}
