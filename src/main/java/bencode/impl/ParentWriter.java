package bencode.impl;

import java.io.IOException;

import bencode.exception.ChildWriterNotClosedException;

abstract class ParentWriter implements AutoCloseable {
    protected ChildWriter child;
    protected BEncodeOutputStream out;

    ParentWriter(final BEncodeOutputStream out) {
        this.out = out;
    }

    void assertChildClosed() throws ChildWriterNotClosedException {
        if (child != null)
            child.assertClosed();
    }

    @Override
    public void close() throws IOException {
        assertChildClosed();
    }
}
