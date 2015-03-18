package bencode.util;

import java.io.IOException;
import java.io.InputStream;

public class EmptyInputStream extends InputStream {
    public static final InputStream instance = new EmptyInputStream();

    @Override
    public int read() throws IOException {
        return -1;
    }
}
