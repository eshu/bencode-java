package bencode.impl;

import bencode.exception.ChildWriterNotClosedException;

interface ChildWriter {
    void assertClosed() throws ChildWriterNotClosedException;
}
