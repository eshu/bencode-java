package bencode.impl;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import bencode.exception.IllegalDictionaryKeyException;
import bencode.exception.IntegerFormatException;
import bencode.exception.UnexpectedCharacterException;
import bencode.util.EmptyInputStream;

final class BEncodeInputStream implements AutoCloseable {
    private final InputStream in;

    BEncodeInputStream(final InputStream in) {
        this.in = in;
    }

    int nextRoot() throws IOException {
        return in.read();
    }

    int next() throws IOException {
        final int nextTag = in.read();
        if (nextTag == -1)
            throw new EOFException();
        if (nextTag == Tag.end)
            return -1;
        return nextTag;
    }

    byte[] nextDictionaryKey() throws IOException {
        int next = next();
        if (next == -1)
            return null;
        if ((next < '0') || (next > '9'))
            throw new IllegalDictionaryKeyException("Illegal key type");
        if (next == '0') {
            next = in.read();
            if (next == Tag.string)
                return new byte[0];
            else
                throw new IntegerFormatException();
        }
        long length = next - '0';
        while (true) {
            next = next();
            if (next == Tag.string)
                break;
            if ((next < '0') || (next > '9'))
                throw new UnexpectedCharacterException();
            length = length * 10 + next - '0';
            if (length > Integer.MAX_VALUE)
                throw new IllegalDictionaryKeyException("Too long key");
        }
        final byte[] data = new byte[(int) length];
        if (in.read(data) < length)
            throw new EOFException();
        return data;
    }

    private BigInteger nextInteger(final StringBuilder result, int next, final int end) throws IOException {
        do {
            if ((next < '0') || (next > '9'))
                throw new IntegerFormatException("Invalid digit: " + (char) next);
            result.append((char) next);
            next = next();
        } while (next != end);
        return new BigInteger(result.toString());
    }

    BigInteger nextInteger() throws IOException {
        int next = next();
        if (next == '0') {
            // После нуля должен следовать символ окончания блока
            if (next() == -1)
                return BigInteger.ZERO;
            throw new IntegerFormatException();
        }
        final StringBuilder result = new StringBuilder();
        if (next == '-') {
            result.append('-');
            next = next();
            // -0 недопустим по стандарту
            if (next == '0')
                throw new IntegerFormatException();
        }
        return nextInteger(result, next, -1);
    }

    ByteStringInputStream nextString(final int firstDigit) throws IOException {
        if (firstDigit == '0') {
            if (next() == Tag.string)
                return new ByteStringInputStream(BigInteger.ZERO, EmptyInputStream.instance);
            throw new IntegerFormatException();
        }
        final BigInteger length = nextInteger(new StringBuilder(), firstDigit, Tag.string);
        return new ByteStringInputStream(length, in);
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
