package bencode.impl;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import bencode.exception.ByteStringOverflowException;
import bencode.exception.ByteStringUnderflowException;

public class ByteStringOutputStreamTest {
    private ByteArrayOutputStream out;
    private ByteStringOutputStream bytes;

    @Before
    public void setUp() {
        out = new ByteArrayOutputStream();
        bytes = new ByteStringOutputStream(BigInteger.valueOf(3), out);
    }

    @Test
    public void testByteWrite() throws IOException {
        bytes.write(1);
        bytes.write(2);
        bytes.write(3);
        bytes.close();
        assertArrayEquals(new byte[] {1, 2, 3},  out.toByteArray());
    }

    @Test
    public void testByteArrayWrite() throws IOException {
        final byte[] data = new byte[3];
        new Random().nextBytes(data);
        bytes.write(data);
        bytes.close();
        assertArrayEquals(data, out.toByteArray());
    }

    @Test(expected = ByteStringOverflowException.class)
    public void testOverflowByteWrite() throws IOException {
        bytes.write(1);
        bytes.write(2);
        bytes.write(3);
        bytes.write(4);
        bytes.close();
    }

    @Test(expected = ByteStringOverflowException.class)
    public void testOverflowByteArrayWrite() throws IOException {
        final byte[] data = new byte[4];
        new Random().nextBytes(data);
        bytes.write(data);
        bytes.close();
    }

    @Test(expected = ByteStringUnderflowException.class)
    public void testUnderflowByteWrite() throws IOException {
        bytes.write(1);
        bytes.write(2);
        bytes.close();
    }

    @Test(expected = ByteStringUnderflowException.class)
    public void testUnderflowByteArrayWrite() throws IOException {
        final byte[] data = new byte[2];
        new Random().nextBytes(data);
        bytes.write(data);
        bytes.close();
    }
}
