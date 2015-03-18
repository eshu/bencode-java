package bencode.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import bencode.DictionaryWriter;
import bencode.ListWriter;
import bencode.exception.ByteStringOverflowException;
import bencode.exception.ByteStringUnderflowException;
import bencode.exception.InvalidDictionaryKeyOrderException;
import bencode.util.ByteArray;

public class BEncodeWriterTest {
    private ByteArrayOutputStream out;
    private BEncodeWriter writer;

    @Before
    public void setUp() {
        out = new ByteArrayOutputStream();
        writer = new BEncodeWriter(out);
    }

    @Test
    public void testPlainByteStringWrite() throws IOException {
        writer.write("123");
        writer.close();
        assertArrayEquals("3:123".getBytes(), out.toByteArray());
    }

    @Test
    public void testPlainEmptyByteStringWrite() throws IOException {
        writer.write(new byte[0]);
        writer.close();
        assertArrayEquals("0:".getBytes(), out.toByteArray());
    }

    @Test
    public void testInputStreamByteStringWrite() throws IOException {
        final byte[] source = new byte[10009]; // 10009 - простое число, не влезет в целое количество буферов
        new Random().nextBytes(source);
        final ByteArrayInputStream in = new ByteArrayInputStream(source);
        writer.write(BigInteger.valueOf(10007), in); // 10007 - тоже простое число
        writer.close();
        final byte[] prefix = "10007:".getBytes();
        final byte[] expected = new byte[prefix.length + 10007];
        System.arraycopy(prefix, 0, expected, 0, prefix.length);
        System.arraycopy(source, 0, expected, prefix.length, 10007);
        assertArrayEquals(expected, out.toByteArray());
    }

    @Test
    public void testInputStreamEmptyByteStringWrite() throws IOException {
        final byte[] source = new byte[10009];
        new Random().nextBytes(source);
        final ByteArrayInputStream in = new ByteArrayInputStream(source);
        writer.write(BigInteger.valueOf(0), in);
        writer.close();
        assertArrayEquals("0:".getBytes(), out.toByteArray());
    }

    @Test(expected = ByteStringUnderflowException.class)
    public void testInputStreamByteStringWriteUnderflowException() throws IOException {
        final byte[] source = new byte[10007];
        new Random().nextBytes(source);
        final ByteArrayInputStream in = new ByteArrayInputStream(source);
        writer.write(BigInteger.valueOf(10009), in);
        fail();
        writer.close();
    }

    @Test
    public void testOutputStreamByteStringWrite() throws IOException {
        final OutputStream string = writer.string(BigInteger.valueOf(10009));
        final byte[] source = new byte[10009];
        new Random().nextBytes(source);
        string.write(source);
        string.close();
        writer.close();
        final byte[] prefix = "10009:".getBytes();
        final byte[] expected = new byte[prefix.length + 10009];
        System.arraycopy(prefix, 0, expected, 0, prefix.length);
        System.arraycopy(source, 0, expected, prefix.length, 10009);
        assertArrayEquals(expected, out.toByteArray());
    }

    @Test(expected = ByteStringUnderflowException.class)
    public void testOutputStreamByteStringWriteUnderflowException() throws IOException {
        final OutputStream string = writer.string(BigInteger.valueOf(10009));
        final byte[] source = new byte[10007];
        new Random().nextBytes(source);
        string.write(source);
        string.close();
        fail();
        writer.close();
    }

    @Test(expected = ByteStringOverflowException.class)
    public void testOutputStreamByteStringWriteOverflowException() throws IOException {
        final OutputStream string = writer.string(BigInteger.valueOf(10007));
        final byte[] source = new byte[10009];
        new Random().nextBytes(source);
        string.write(source);
        fail();
        string.close();
        writer.close();
    }

    @Test
    public void testIntegerWrite() throws IOException {
        writer.write(new BigInteger("-1234567890"));
        writer.close();
        assertArrayEquals("i-1234567890e".getBytes(), out.toByteArray());
    }

    @Test
    public void testPlainListWrite() throws IOException {
        final List<Object> list = new ArrayList<Object>();
        list.add("123");
        list.add(new BigInteger("9876543210"));
        list.add("456789");
        list.add(new BigInteger("-9753124680"));
        list.add(Collections.emptyList());
        final List<Object> innerList = new ArrayList<Object>();
        innerList.add("i123");
        innerList.add(new Long(123));
        innerList.add("*");
        list.add(innerList);
        list.add(Collections.emptyMap());
        final Map<ByteArray, Object> innerMap = new TreeMap<>();
        innerMap.put(new ByteArray("123"), "456");
        innerMap.put(new ByteArray("int"), new Integer(1234));
        list.add(innerMap);
        list.add("");
        writer.write(list);
        writer.close();
        assertArrayEquals("l3:123i9876543210e6:456789i-9753124680elel4:i123i123e1:*eded3:1233:4563:inti1234ee0:e".getBytes(), out.toByteArray());
    }

    private void testPlainMapWrite(final Map<ByteArray, Object> map) throws IOException {
        map.put(new ByteArray("123"), "one two three");
        map.put(new ByteArray("456"), new BigInteger("0"));
        map.put(new ByteArray("789"), "seven eight nine");
        map.put(new ByteArray("147"), new BigInteger("-1"));
        map.put(new ByteArray("258"), "two five eight");
        map.put(new ByteArray("369"), new BigInteger("2"));
        map.put(new ByteArray("emptyList"), Collections.emptyList());
        final List<Object> innerList = new ArrayList<Object>();
        innerList.add("i123");
        innerList.add(new Long(123));
        innerList.add("*");
        map.put(new ByteArray("innerList"), innerList);
        map.put(new ByteArray("emptyMap"), Collections.emptyMap());
        final Map<ByteArray, Object> innerMap = new HashMap<>();
        innerMap.put(new ByteArray("123"), "456");
        innerMap.put(new ByteArray("int"), new Integer(1234));
        map.put(new ByteArray("innerMap"), innerMap);
        map.put(new ByteArray("emptyString"), "");
        writer.write(map);
        writer.close();
        assertArrayEquals("d3:12313:one two three3:147i-1e3:25814:two five eight3:369i2e3:456i0e3:78916:seven eight nine9:emptyListle8:emptyMapde11:emptyString0:9:innerListl4:i123i123e1:*e8:innerMapd3:1233:4563:inti1234eee".getBytes(), out.toByteArray());
    }

    @Test
    public void testPlainHashMapWrite() throws IOException {
        testPlainMapWrite(new HashMap<>());
    }

    @Test
    public void testPlainNavigableMapWrite() throws IOException {
        testPlainMapWrite(new TreeMap<>());
    }

    @Test(expected = InvalidDictionaryKeyOrderException.class)
    public void testKeyOrderGreater() throws IOException {
        final DictionaryWriter dict = writer.dictionary();
        dict.write("abc", "abc");
        dict.write("xyz", "xyz");
        dict.write("def", "def");
        fail();
        dict.close();
        writer.close();
    }

    @Test(expected = InvalidDictionaryKeyOrderException.class)
    public void testKeyOrderEqual() throws IOException {
        final DictionaryWriter dict = writer.dictionary();
        dict.write("abc", "abc");
        dict.write("xyz", "xyz");
        dict.write("xyz", "def");
        fail();
        dict.close();
        writer.close();
    }

    @Test
    public void test() throws IOException {
        writer.write("start");
        final DictionaryWriter dict = writer.dictionary();
        dict.write("000", "first key")
            .write("123", BigInteger.valueOf(123))
            .write("abc", "third key")
            .list("emptyList").close();
        dict.dictionary("emptyMap").close();
        dict.write("emptyString", "")
            .write("xyz", "last key")
            .close();
        final ListWriter list = writer.list();
        list.write(BigInteger.valueOf(1))
            .write(BigInteger.valueOf(2))
            .write("many")
            .write(BigInteger.valueOf(100500))
            .dictionary().close();
        list.list().close();
        list.write("")
            .write("last")
            .close();
        writer.dictionary().close();
        writer.write("")
              .write("end");
        final String expected = "5:startd3:0009:first key3:123i123e3:abc9:third key9:emptyListle8:emptyMapde11:emptyString0:3:xyz8:last keyeli1ei2e4:manyi100500edele0:4:lastede0:3:end";
        assertArrayEquals(expected.getBytes(), out.toByteArray());
    }
}
