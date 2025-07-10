package com.tarento.commenthub.utility;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CommentsUtilityTest {

    /**
     * Tests the containsNull method with a list containing a null element.
     * This is an edge case explicitly handled by the method.
     */
    @Test
    void testContainsNullWithListContainingNull() {
        List<String> listWithNull = new ArrayList<>();
        listWithNull.add("Not null");
        listWithNull.add(null);
        listWithNull.add("Also not null");
        assertTrue(CommentsUtility.containsNull(listWithNull));
    }

    /**
     * Tests the containsNull method with a null list input.
     * This is an edge case explicitly handled by the method.
     */
    @Test
    void testContainsNullWithNullList() {
        List<?> nullList = null;
        assertTrue(CommentsUtility.containsNull(nullList));
    }

    /**
     * Test case for containsNull method when the list is not null and contains no null elements.
     * This test verifies that the method returns false for a non-null list with non-null elements.
     */
    @Test
    void test_containsNull_nonNullListWithNoNullElements() {
        List<String> list = Arrays.asList("element1", "element2", "element3");
        assertFalse(CommentsUtility.containsNull(list));
    }

    /**
     * Tests that containsNull returns true when the list contains a null element.
     * Path constraints: !((list == null)), (element == null)
     */
    @Test
    void test_containsNull_returnsTrue_whenListContainsNull() {
        List<String> listWithNull = Arrays.asList("A", null, "C");
        assertTrue(CommentsUtility.containsNull(listWithNull));
    }

    /**
     * Test case for containsNull method when the input list is null.
     * Expected behavior: Method should return true when the input list is null.
     */
    @Test
    void test_containsNull_returnsTrue_whenListIsNull() {
        List<?> nullList = null;
        boolean result = CommentsUtility.containsNull(nullList);
        assertTrue(result);
    }

}
