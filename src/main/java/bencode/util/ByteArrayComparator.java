package bencode.util;

import java.util.Comparator;

public enum ByteArrayComparator implements Comparator<byte[]> {
    instance;

    @Override
    public int compare(final byte[] first, final byte[] second) {
        final int length = Math.min(first.length, second.length);
        for (int i = 0; i < length; i++) {
            final int diff = ((first[i]) & 0xFF) - ((second[i]) & 0xFF);
            if (diff != 0)
                return diff;
        }
        return first.length - second.length;
    }

}
