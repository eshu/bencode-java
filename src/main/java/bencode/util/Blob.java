package bencode.util;

import java.io.InputStream;
import java.math.BigInteger;

public final class Blob {
    public final BigInteger length;
    public final InputStream bytes;

    public Blob(final BigInteger length, final InputStream bytes) {
        this.length = length;
        this.bytes = bytes;
    }
}
