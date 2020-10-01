package com.wearewaes.scalableweb.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Calculates the offsets for two decoded plain strings.
 * No need to create Spring Bean.
 */
public final class OffsetCalculator {

    private OffsetCalculator() {
        throw new AssertionError("Should not be instantiated");
    }

    public static Map<Integer, Integer> calculate(String leftDecoded, String rightDecoded) {
        char[] leftAsChars = leftDecoded.toCharArray();
        char[] rightAsChars = rightDecoded.toCharArray();

        int start = 0;
        int offset = -1;
        int length = 0;

        Map<Integer, Integer> offsetToLength = new LinkedHashMap<>();

        while (start < leftAsChars.length) {
            if (leftAsChars[start] != rightAsChars[start]) {
                if (offset < 0) {
                    offset = start;
                }
                offsetToLength.put(offset, ++length);
            } else {
                offset = -1;
                length = 0;
            }
            start++;
        }

        return offsetToLength;
    }
}
