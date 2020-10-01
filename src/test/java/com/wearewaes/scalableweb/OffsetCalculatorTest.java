package com.wearewaes.scalableweb;

import com.wearewaes.scalableweb.util.OffsetCalculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class OffsetCalculatorTest {

    /**
     * We do not use calculator for such case in the flow, but still this case should be covered
     * to make sure the algorithm works correctly.
     */
    @Test
    public void testOffsetCalculation_NoDifference() {
        String left = "0123456789";
        String right = "0123456789";

        Map<Integer, Integer> response = OffsetCalculator.calculate(left, right);

        Assertions.assertEquals(0, response.size());

        // offsets should be empty
        Assertions.assertArrayEquals(new Integer[]{}, response.keySet().toArray(new Integer[0]));
        // offset length should be empty
        Assertions.assertArrayEquals(new Integer[]{}, response.values().toArray(new Integer[0]));
    }

    /**
     * We do not use calculator for such case in the flow, but still this case should be covered
     * to make sure the algorithm works correctly.
     */
    @Test
    public void testOffsetCalculation_allSymbolsAreSame() {
        String left = "0000000000";
        String right = "0000000000";

        Map<Integer, Integer> response = OffsetCalculator.calculate(left, right);

        Assertions.assertEquals(0, response.size());

        // offsets should be empty
        Assertions.assertArrayEquals(new Integer[]{}, response.keySet().toArray(new Integer[0]));
        // offset length should be empty
        Assertions.assertArrayEquals(new Integer[]{}, response.values().toArray(new Integer[0]));
    }

    @Test
    public void testOffsetCalculation_firstSymbolDiffers() {
        String left = "0123456789";
        String right = "a123456789";

        Map<Integer, Integer> response = OffsetCalculator.calculate(left, right);

        Assertions.assertEquals(1, response.size());

        Assertions.assertArrayEquals(new Integer[]{0}, response.keySet().toArray(new Integer[0]));
        Assertions.assertArrayEquals(new Integer[]{1}, response.values().toArray(new Integer[0]));
    }

    @Test
    public void testOffsetCalculation_lastSymbolDiffers() {
        String left = "0123456789";
        String right = "012345678a";

        Map<Integer, Integer> response = OffsetCalculator.calculate(left, right);

        Assertions.assertEquals(1, response.size());

        Assertions.assertArrayEquals(new Integer[]{9}, response.keySet().toArray(new Integer[0]));
        Assertions.assertArrayEquals(new Integer[]{1}, response.values().toArray(new Integer[0]));
    }

    @Test
    public void testOffsetCalculation_TwoOffsets() {
        String left = "0123456789";
        String right = "0aa3456aaa";

        Map<Integer, Integer> response = OffsetCalculator.calculate(left, right);

        Assertions.assertEquals(2, response.size());

        // offsets should be at 1st and 7th index
        Assertions.assertArrayEquals(new Integer[]{1, 7}, response.keySet().toArray(new Integer[0]));
        // offset length is 1 and 3 symbols respectively
        Assertions.assertArrayEquals(new Integer[]{2, 3}, response.values().toArray(new Integer[0]));
    }

    @Test
    public void testOffsetCalculation_allSymbolsDiffer() {
        String left = "0123456789";
        String right = "abcdefghij";

        Map<Integer, Integer> response = OffsetCalculator.calculate(left, right);

        Assertions.assertEquals(1, response.size());

        Assertions.assertArrayEquals(new Integer[]{0}, response.keySet().toArray(new Integer[0]));
        Assertions.assertArrayEquals(new Integer[]{10}, response.values().toArray(new Integer[0]));
    }

    @Test
    public void testOffsetCalculation_withBracketsReversedCase() {
        String left = "{\"name}\":\"MyName\"}";
        String right = "{\"name}\":\"yMnAME\"}";

        Map<Integer, Integer> response = OffsetCalculator.calculate(left, right);

        Assertions.assertEquals(1, response.size());

        Assertions.assertArrayEquals(new Integer[]{10}, response.keySet().toArray(new Integer[0]));
        Assertions.assertArrayEquals(new Integer[]{6}, response.values().toArray(new Integer[0]));
    }
}
