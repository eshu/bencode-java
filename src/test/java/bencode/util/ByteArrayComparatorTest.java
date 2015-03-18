package bencode.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import bencode.util.ByteArrayComparator;

public class ByteArrayComparatorTest {
    @Test
    public void test() {
        final byte[] array1 = new byte[] {1, 2, 3};
        byte[] array2 = new byte[] {1, 2, 3};
        assertEquals(ByteArrayComparator.instance.compare(array1, array2), 0);
        assertEquals(ByteArrayComparator.instance.compare(array2, array1), 0);
        array2 = new byte[] {1, 2, 4};
        assertTrue(ByteArrayComparator.instance.compare(array1, array2) < 0);
        assertTrue(ByteArrayComparator.instance.compare(array2, array1) > 0);
        array2 = new byte[] {1, 2, 3, 4};
        assertTrue(ByteArrayComparator.instance.compare(array1, array2) < 0);
        assertTrue(ByteArrayComparator.instance.compare(array2, array1) > 0);
    }
}
