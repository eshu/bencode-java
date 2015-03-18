package bencode;

import java.io.InputStream;
import java.io.OutputStream;

import bencode.impl.BEncodeParser;
import bencode.impl.BEncodeWriter;

public final class BEncode {
    public Writer getWriter(final OutputStream out) {
        return new BEncodeWriter(out);
    }

    public Parser getParser(final InputStream in) {
        return new BEncodeParser(in);
    }
}
