package bencode.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import bencode.exception.IllegalDictionaryKeyException;
import bencode.exception.IntegerFormatException;
import bencode.exception.InvalidDictionaryKeyOrderException;
import bencode.handler.impl.SimpleListBuilder;
import bencode.util.ByteArray;

public class BEncodeParserTest {
    @Test
    public void testInteger() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream("i123ei-123ei0e".getBytes());
        final SimpleListBuilder handler = new SimpleListBuilder();
        final BEncodeParser parser = new BEncodeParser(in);
        parser.setHandler(handler);
        parser.parse();
        assertEquals(3, handler.list.size());
        final Iterator<Object> iterator = handler.list.iterator();
        assertEquals(BigInteger.valueOf(123), iterator.next());
        assertEquals(BigInteger.valueOf(-123), iterator.next());
        assertEquals(BigInteger.ZERO, iterator.next());
    }

    @Test(expected = IntegerFormatException.class)
    public void testIntegerMinusZero() throws IOException {
        new BEncodeParser(new ByteArrayInputStream("i-0e".getBytes())).parse();
    }

    @Test(expected = IntegerFormatException.class)
    public void testIntegerLeadingZero() throws IOException {
        new BEncodeParser(new ByteArrayInputStream("i01e".getBytes())).parse();
    }

    @Test(expected = IntegerFormatException.class)
    public void testIntegerMinusLeadingZero() throws IOException {
        new BEncodeParser(new ByteArrayInputStream("i-01e".getBytes())).parse();
    }

    @Test
    public void testString() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream("0:3:1235:45678".getBytes());
        final SimpleListBuilder handler = new SimpleListBuilder();
        final BEncodeParser parser = new BEncodeParser(in);
        parser.setHandler(handler);
        parser.parse();
        assertEquals(handler.list.size(), 3);
        final Iterator<Object> iterator = handler.list.iterator();
        assertArrayEquals(new byte[0], (byte[]) iterator.next());
        assertArrayEquals("123".getBytes(), (byte[]) iterator.next());
        assertArrayEquals("45678".getBytes(), (byte[]) iterator.next());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testList() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream("l4:spami42eele".getBytes());
        final SimpleListBuilder handler = new SimpleListBuilder();
        final BEncodeParser parser = new BEncodeParser(in);
        parser.setHandler(handler);
        parser.parse();
        assertEquals(2, handler.list.size());
        final Iterator<Object> iterator = handler.list.iterator();
        List<Object> list = (List<Object>) iterator.next();
        assertEquals(2, list.size());
        final Iterator<Object> innerIterator = list.iterator();
        assertArrayEquals("spam".getBytes(), (byte[]) innerIterator.next());
        assertEquals(BigInteger.valueOf(42), innerIterator.next());
        list = (List<Object>) iterator.next();
        assertEquals(0, list.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDictionary() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream("d3:bar4:spam3:fooi42eede".getBytes());
        final SimpleListBuilder handler = new SimpleListBuilder();
        final BEncodeParser parser = new BEncodeParser(in);
        parser.setHandler(handler);
        parser.parse();
        assertEquals(handler.list.size(), 2);
        final Iterator<Object> iterator = handler.list.iterator();
        Map<ByteArray, Object> map = (Map<ByteArray, Object>) iterator.next();
        assertEquals(2, map.size());
        assertEquals(BigInteger.valueOf(42), map.get(new ByteArray("foo")));
        assertArrayEquals((byte[]) map.get(new ByteArray("bar")), "spam".getBytes());
        map = (Map<ByteArray, Object>) iterator.next();
        assertEquals(0, map.size());
    }

    @Test(expected = InvalidDictionaryKeyOrderException.class)
    public void testDictionaryInvalidKeyOrderGreater() throws IOException {
        new BEncodeParser(new ByteArrayInputStream("d3:fooi42e3:bar4:spame".getBytes())).parse();
    }

    @Test(expected = InvalidDictionaryKeyOrderException.class)
    public void testDictionaryInvalidKeyOrderEqual() throws IOException {
        new BEncodeParser(new ByteArrayInputStream("d3:bari42e3:bar4:spame".getBytes())).parse();
    }

    @Test(expected = IllegalDictionaryKeyException.class)
    public void testDictionaryIllegalKeyInteger() throws IOException {
        new BEncodeParser(new ByteArrayInputStream("di".getBytes())).parse();
    }

    @Test(expected = IllegalDictionaryKeyException.class)
    public void testDictionaryIllegalKeyDictionary() throws IOException {
        new BEncodeParser(new ByteArrayInputStream("dd".getBytes())).parse();
    }

    @Test(expected = IllegalDictionaryKeyException.class)
    public void testDictionaryIllegalKeyList() throws IOException {
        new BEncodeParser(new ByteArrayInputStream("dl".getBytes())).parse();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test() throws IOException {
        final String source = "5:startd3:0009:first key3:123i123e3:abc9:third key9:emptyListle8:emptyMapde11:emptyString0:3:xyz8:last keyeli1ei2e4:manyi100500edele0:4:lastede0:3:end";
        final ByteArrayInputStream in = new ByteArrayInputStream(source.getBytes());
        final SimpleListBuilder handler = new SimpleListBuilder();
        final BEncodeParser parser = new BEncodeParser(in);
        parser.setHandler(handler);
        parser.parse();
        assertEquals(6, handler.list.size());
        final Iterator<Object> iterator = handler.list.iterator();

        assertArrayEquals("start".getBytes(), (byte[]) iterator.next());

        final Map<ByteArray, Object> map = (Map<ByteArray, Object>) iterator.next();
        assertArrayEquals("first key".getBytes(), (byte[]) map.get(new ByteArray("000")));
        assertEquals(BigInteger.valueOf(123), map.get(new ByteArray("123")));
        assertArrayEquals("third key".getBytes(), (byte[]) map.get(new ByteArray("abc")));
        List<Object> emptyList = (List<Object>) map.get(new ByteArray("emptyList"));
        assertEquals(0, emptyList.size());
        Map<ByteArray, Object> emptyMap = (Map<ByteArray, Object>) map.get(new ByteArray("emptyMap"));
        assertEquals(0, emptyMap.size());
        assertArrayEquals(new byte[0], (byte[]) map.get(new ByteArray("emptyString")));
        assertArrayEquals("last key".getBytes(), (byte[]) map.get(new ByteArray("xyz")));

        final List<Object> list = (List<Object>) iterator.next();
        assertEquals(8, list.size());
        final Iterator<Object> listIterator = list.iterator();
        assertEquals(BigInteger.ONE, listIterator.next());
        assertEquals(BigInteger.valueOf(2), listIterator.next());
        assertArrayEquals("many".getBytes(), (byte[]) listIterator.next());
        assertEquals(BigInteger.valueOf(100500), listIterator.next());
        emptyMap = (Map<ByteArray, Object>) listIterator.next();
        assertEquals(0, emptyMap.size());
        emptyList = (List<Object>) listIterator.next();
        assertEquals(0, emptyList.size());
        assertArrayEquals(new byte[0], (byte[]) listIterator.next());
        assertArrayEquals("last".getBytes(), (byte[]) listIterator.next());

        emptyMap = (Map<ByteArray, Object>) iterator.next();
        assertEquals(0, emptyMap.size());

        assertArrayEquals(new byte[0], (byte[]) iterator.next());

        assertArrayEquals("end".getBytes(), (byte[]) iterator.next());
    }
}
