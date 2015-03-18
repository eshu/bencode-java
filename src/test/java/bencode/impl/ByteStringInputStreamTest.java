package bencode.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class ByteStringInputStreamTest {
    @Test
    public void testByteRead() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {1, 2, 3});
        final ByteStringInputStream bytes = new ByteStringInputStream(BigInteger.valueOf(3), in);
        assertEquals(bytes.read(), 1);
        assertEquals(bytes.read(), 2);
        assertEquals(bytes.read(), 3);
        assertEquals(bytes.read(), -1);
        bytes.close();
    }

    @Test
    public void testByteArrayRead() throws IOException {
        final byte[] source = new byte[3];
        new Random().nextBytes(source);
        final ByteArrayInputStream in = new ByteArrayInputStream(source);
        final ByteStringInputStream bytes = new ByteStringInputStream(BigInteger.valueOf(3), in);
        final byte[] data = new byte[3];
        bytes.read(data);
        bytes.close();
        assertArrayEquals(source, data);
    }

    @Test(expected = EOFException.class)
    public void tesrEOFOnCloseFullRead() throws IOException {
        final byte[] source = new byte[3];
        new Random().nextBytes(source);
        final ByteArrayInputStream in = new ByteArrayInputStream(source);
        final ByteStringInputStream bytes = new ByteStringInputStream(BigInteger.valueOf(4), in);
        final byte[] data = new byte[3];
        bytes.read(data);
        assertArrayEquals(source, data);
        bytes.close();
    }

    @Test(expected = EOFException.class)
    public void testEOFOnClosePartialRead() throws IOException {
        final byte[] source = new byte[3];
        new Random().nextBytes(source);
        final ByteArrayInputStream in = new ByteArrayInputStream(source);
        final ByteStringInputStream bytes = new ByteStringInputStream(BigInteger.valueOf(4), in);
        final byte[] data = new byte[2];
        bytes.read(data);
        final byte[] expected = Arrays.copyOf(source, 2);
        assertArrayEquals(expected, data);
        bytes.close();
    }

    @Test(expected = EOFException.class)
    public void testEOFOnCloseOverflowRead() throws IOException {
        final byte[] source = new byte[3];
        new Random().nextBytes(source);
        final ByteArrayInputStream in = new ByteArrayInputStream(source);
        final ByteStringInputStream bytes = new ByteStringInputStream(BigInteger.valueOf(4), in);
        final byte[] data = new byte[4];
        bytes.read(data);
        fail();
        bytes.close();
    }
}
