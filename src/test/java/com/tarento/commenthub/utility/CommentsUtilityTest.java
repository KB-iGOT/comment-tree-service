package com.tarento.commenthub.utility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("CommentsUtility – 100% Coverage Tests")
class CommentsUtilityTest {

    @Test
    @DisplayName("CommentsUtility can be instantiated (covers implicit constructor)")
    void constructor_canBeInstantiated() {
        CommentsUtility utility = new CommentsUtility();
        assertThat(utility).isNotNull();
    }


    @Nested
    @DisplayName("containsNull(List<?>)")
    class ContainsNullTests {


        @Test
        @DisplayName("B1=true : null list → returns true immediately")
        void nullList_returnsTrue() {
            assertThat(CommentsUtility.containsNull(null)).isTrue();
        }


        @Test
        @DisplayName("B1=false, B3: empty list → returns false (loop not entered)")
        void emptyList_returnsFalse() {
            assertThat(CommentsUtility.containsNull(Collections.emptyList())).isFalse();
        }


        @Test
        @DisplayName("B2=false: list with all non-null elements → returns false")
        void listWithAllNonNullElements_returnsFalse() {
            List<String> list = Arrays.asList("alpha", "beta", "gamma");
            assertThat(CommentsUtility.containsNull(list)).isFalse();
        }

        @Test
        @DisplayName("B2=false: single non-null element → returns false")
        void singleNonNullElement_returnsFalse() {
            assertThat(CommentsUtility.containsNull(Collections.singletonList("only"))).isFalse();
        }

        @Test
        @DisplayName("B2=true : list containing one null element → returns true")
        void listWithOneNullElement_returnsTrue() {
            List<String> list = Arrays.asList("a", null, "c");
            assertThat(CommentsUtility.containsNull(list)).isTrue();
        }

        @Test
        @DisplayName("B2=true : first element is null → returns true immediately")
        void firstElementNull_returnsTrueImmediately() {
            List<String> list = Arrays.asList(null, "b", "c");
            assertThat(CommentsUtility.containsNull(list)).isTrue();
        }

        @Test
        @DisplayName("B2=true : list containing only null elements → returns true")
        void listWithOnlyNullElements_returnsTrue() {
            List<String> list = Collections.singletonList(null);
            assertThat(CommentsUtility.containsNull(list)).isTrue();
        }

        @Test
        @DisplayName("B2=true : last element is null → returns true after iterating non-null ones")
        void lastElementNull_returnsTrueAfterIteration() {
            // covers: B2=false for first elements, then B2=true for the last
            List<String> list = Arrays.asList("x", "y", null);
            assertThat(CommentsUtility.containsNull(list)).isTrue();
        }

        @Test
        @DisplayName("list of mixed types containing null → returns true")
        void mixedTypeListWithNull_returnsTrue() {
            List<Object> list = Arrays.asList(1, "text", null, 3.14);
            assertThat(CommentsUtility.containsNull(list)).isTrue();
        }

        @Test
        @DisplayName("list of mixed types without null → returns false")
        void mixedTypeListWithoutNull_returnsFalse() {
            List<Object> list = Arrays.asList(1, "text", 3.14, true);
            assertThat(CommentsUtility.containsNull(list)).isFalse();
        }
    }
}

