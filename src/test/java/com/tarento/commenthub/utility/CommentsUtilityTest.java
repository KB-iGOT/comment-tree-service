package com.tarento.commenthub.utility;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Full-coverage unit tests for {@link CommentsUtility}.
 *
 * <h3>Methods covered</h3>
 * <ol>
 *   <li>{@code CommentsUtility()} – implicit no-args constructor.</li>
 *   <li>{@code containsNull(List<?>)} – static utility method.</li>
 * </ol>
 *
 * <h3>Branch map for containsNull()</h3>
 * <pre>
 *   B1  list == null              true  → return true
 *                                 false → enter loop
 *   B2  element == null (in loop) true  → return true
 *                                 false → continue
 *   B3  loop exhausted            → return false
 * </pre>
 *
 * Test matrix:
 * <table>
 *   <tr><th>Test</th><th>B1</th><th>B2</th><th>B3</th><th>result</th></tr>
 *   <tr><td>null list</td>           <td>true</td>  <td>–</td>    <td>–</td>   <td>true</td></tr>
 *   <tr><td>empty list</td>          <td>false</td> <td>–</td>    <td>yes</td> <td>false</td></tr>
 *   <tr><td>all non-null elements</td><td>false</td><td>false</td><td>yes</td> <td>false</td></tr>
 *   <tr><td>list with null element</td><td>false</td><td>true</td><td>–</td>  <td>true</td></tr>
 * </table>
 */
@DisplayName("CommentsUtility – 100% Coverage Tests")
class CommentsUtilityTest {

  // =========================================================================
  // Constructor (implicit Java no-args constructor)
  // =========================================================================

  @Test
  @DisplayName("CommentsUtility can be instantiated (covers implicit constructor)")
  void constructor_canBeInstantiated() {
    CommentsUtility utility = new CommentsUtility();
    assertThat(utility).isNotNull();
  }

  // =========================================================================
  // containsNull(List<?>)
  // =========================================================================

  @Nested
  @DisplayName("containsNull(List<?>)")
  class ContainsNullTests {

    // ── B1 = true ────────────────────────────────────────────────────────
    @Test
    @DisplayName("B1=true : null list → returns true immediately")
    void nullList_returnsTrue() {
      assertThat(CommentsUtility.containsNull(null)).isTrue();
    }

    // ── B1 = false, loop never entered (empty list) → return false ────────
    @Test
    @DisplayName("B1=false, B3: empty list → returns false (loop not entered)")
    void emptyList_returnsFalse() {
      assertThat(CommentsUtility.containsNull(Collections.emptyList())).isFalse();
    }

    // ── B1 = false, B2 = false for every element, B3 → return false ──────
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

    // ── B1 = false, B2 = true (null found) → return true ─────────────────
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

