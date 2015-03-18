package bencode.impl;

import java.io.IOException;

import bencode.exception.BEncodeException;
import bencode.exception.ChildWriterNotClosedException;

abstract class ContainerChildWriter extends ParentWriter implements ChildWriter, AutoCloseable {
    ContainerChildWriter(final BEncodeOutputStream out) {
        super(out);
    }

    @Override
    public void assertClosed() throws ChildWriterNotClosedException {
        if (out != null)
            throw new ChildWriterNotClosedException();
    }

    @Override
    public void close() throws IOException, BEncodeException {
        super.close();
        out.end();
        out = null;
    }
}
