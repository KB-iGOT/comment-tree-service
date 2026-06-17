package com.tarento.commenthub.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SearchCriteria – 100% Coverage Tests")
class SearchCriteriaTest {

    // =========================================================================
    // 1. Constructors
    // =========================================================================

    @Nested
    @DisplayName("Constructors")
    class ConstructorTests {

        @Test
        @DisplayName("no-args constructor: Integer fields null, Strings null, booleans false")
        void noArgsConstructor_defaultValues() {
            SearchCriteria sc = new SearchCriteria();

            assertThat(sc.getLimit()).isNull();
            assertThat(sc.getOffset()).isNull();
            assertThat(sc.getCommentTreeId()).isNull();
            assertThat(sc.getEntityType()).isNull();
            assertThat(sc.getEntityId()).isNull();
            assertThat(sc.getWorkflow()).isNull();
            assertThat(sc.isOverrideCache()).isFalse();
            assertThat(sc.isEnrichedUser()).isFalse();
        }

        @Test
        @DisplayName("all-args constructor sets every field to the supplied value")
        void allArgsConstructor_setsAllFields() {
            SearchCriteria sc =
                    new SearchCriteria(10, 5, "tree-001", "course", "entity-001", "review", true, false);

            assertThat(sc.getLimit()).isEqualTo(10);
            assertThat(sc.getOffset()).isEqualTo(5);
            assertThat(sc.getCommentTreeId()).isEqualTo("tree-001");
            assertThat(sc.getEntityType()).isEqualTo("course");
            assertThat(sc.getEntityId()).isEqualTo("entity-001");
            assertThat(sc.getWorkflow()).isEqualTo("review");
            assertThat(sc.isOverrideCache()).isTrue();
            assertThat(sc.isEnrichedUser()).isFalse();
        }

        @Test
        @DisplayName("all-args constructor with null Integers and empty strings is allowed")
        void allArgsConstructor_nullIntegersEmptyStrings() {
            SearchCriteria sc =
                    new SearchCriteria(null, null, "", "", "", "", false, false);

            assertThat(sc.getLimit()).isNull();
            assertThat(sc.getOffset()).isNull();
            assertThat(sc.getCommentTreeId()).isEmpty();
        }

        @Test
        @DisplayName("all-args constructor with both booleans true")
        void allArgsConstructor_bothBooleansTrue() {
            SearchCriteria sc =
                    new SearchCriteria(0, 0, null, null, null, null, true, true);

            assertThat(sc.isOverrideCache()).isTrue();
            assertThat(sc.isEnrichedUser()).isTrue();
        }
    }

    // =========================================================================
    // 2. limit getter / setter
    // =========================================================================

    @Nested
    @DisplayName("limit getter and setter")
    class LimitTests {

        @Test
        @DisplayName("setLimit / getLimit round-trip with positive integer")
        void setLimit_getLimit_positive() {
            SearchCriteria sc = new SearchCriteria();
            sc.setLimit(20);
            assertThat(sc.getLimit()).isEqualTo(20);
        }

        @Test
        @DisplayName("setLimit with null clears the limit")
        void setLimit_null() {
            SearchCriteria sc = new SearchCriteria();
            sc.setLimit(10);
            sc.setLimit(null);
            assertThat(sc.getLimit()).isNull();
        }

        @Test
        @DisplayName("setLimit with zero is stored as-is")
        void setLimit_zero() {
            SearchCriteria sc = new SearchCriteria();
            sc.setLimit(0);
            assertThat(sc.getLimit()).isZero();
        }
    }

    // =========================================================================
    // 3. offset getter / setter
    // =========================================================================

    @Nested
    @DisplayName("offset getter and setter")
    class OffsetTests {

        @Test
        @DisplayName("setOffset / getOffset round-trip")
        void setOffset_getOffset_roundTrip() {
            SearchCriteria sc = new SearchCriteria();
            sc.setOffset(100);
            assertThat(sc.getOffset()).isEqualTo(100);
        }

        @Test
        @DisplayName("setOffset with null clears the offset")
        void setOffset_null() {
            SearchCriteria sc = new SearchCriteria();
            sc.setOffset(50);
            sc.setOffset(null);
            assertThat(sc.getOffset()).isNull();
        }
    }

    // =========================================================================
    // 4. commentTreeId getter / setter
    // =========================================================================

    @Nested
    @DisplayName("commentTreeId getter and setter")
    class CommentTreeIdTests {

        @Test
        @DisplayName("setCommentTreeId / getCommentTreeId round-trip")
        void setCommentTreeId_getCommentTreeId_roundTrip() {
            SearchCriteria sc = new SearchCriteria();
            sc.setCommentTreeId("ct-id-999");
            assertThat(sc.getCommentTreeId()).isEqualTo("ct-id-999");
        }

        @ParameterizedTest(name = "setCommentTreeId(\"{0}\") is stored as-is")
        @NullAndEmptySource
        @ValueSource(strings = {"tree-1", "jwt.abc.def"})
        @DisplayName("setCommentTreeId accepts null, empty, and non-blank values")
        void setCommentTreeId_variousValues(String value) {
            SearchCriteria sc = new SearchCriteria();
            sc.setCommentTreeId(value);
            assertThat(sc.getCommentTreeId()).isEqualTo(value);
        }
    }

    // =========================================================================
    // 5. entityType getter / setter
    // =========================================================================

    @Nested
    @DisplayName("entityType getter and setter")
    class EntityTypeTests {

        @Test
        @DisplayName("setEntityType / getEntityType round-trip")
        void setEntityType_getEntityType_roundTrip() {
            SearchCriteria sc = new SearchCriteria();
            sc.setEntityType("module");
            assertThat(sc.getEntityType()).isEqualTo("module");
        }

        @ParameterizedTest(name = "setEntityType(\"{0}\") is accepted")
        @NullAndEmptySource
        @ValueSource(strings = {"course", "PROGRAM"})
        @DisplayName("setEntityType accepts null, empty, and non-empty strings")
        void setEntityType_variousValues(String value) {
            SearchCriteria sc = new SearchCriteria();
            sc.setEntityType(value);
            assertThat(sc.getEntityType()).isEqualTo(value);
        }
    }

    // =========================================================================
    // 6. entityId getter / setter
    // =========================================================================

    @Nested
    @DisplayName("entityId getter and setter")
    class EntityIdTests {

        @Test
        @DisplayName("setEntityId / getEntityId round-trip")
        void setEntityId_getEntityId_roundTrip() {
            SearchCriteria sc = new SearchCriteria();
            sc.setEntityId("e-001");
            assertThat(sc.getEntityId()).isEqualTo("e-001");
        }

        @ParameterizedTest(name = "setEntityId(\"{0}\") is accepted")
        @NullAndEmptySource
        @ValueSource(strings = {"uuid-abc", "123"})
        @DisplayName("setEntityId accepts null, empty, and non-empty strings")
        void setEntityId_variousValues(String value) {
            SearchCriteria sc = new SearchCriteria();
            sc.setEntityId(value);
            assertThat(sc.getEntityId()).isEqualTo(value);
        }
    }

    // =========================================================================
    // 7. workflow getter / setter
    // =========================================================================

    @Nested
    @DisplayName("workflow getter and setter")
    class WorkflowTests {

        @Test
        @DisplayName("setWorkflow / getWorkflow round-trip")
        void setWorkflow_getWorkflow_roundTrip() {
            SearchCriteria sc = new SearchCriteria();
            sc.setWorkflow("approve");
            assertThat(sc.getWorkflow()).isEqualTo("approve");
        }

        @ParameterizedTest(name = "setWorkflow(\"{0}\") is accepted")
        @NullAndEmptySource
        @ValueSource(strings = {"review", "PUBLISH"})
        @DisplayName("setWorkflow accepts null, empty, and non-empty strings")
        void setWorkflow_variousValues(String value) {
            SearchCriteria sc = new SearchCriteria();
            sc.setWorkflow(value);
            assertThat(sc.getWorkflow()).isEqualTo(value);
        }
    }

    // =========================================================================
    // 8. overrideCache getter / setter  (boolean field)
    // =========================================================================

    @Nested
    @DisplayName("overrideCache getter and setter")
    class OverrideCacheTests {

        @Test
        @DisplayName("setOverrideCache(true) / isOverrideCache returns true")
        void setOverrideCache_true() {
            SearchCriteria sc = new SearchCriteria();
            sc.setOverrideCache(true);
            assertThat(sc.isOverrideCache()).isTrue();
        }

        @Test
        @DisplayName("setOverrideCache(false) / isOverrideCache returns false")
        void setOverrideCache_false() {
            SearchCriteria sc = new SearchCriteria();
            sc.setOverrideCache(true); // set to true first
            sc.setOverrideCache(false); // then reset
            assertThat(sc.isOverrideCache()).isFalse();
        }
    }

    // =========================================================================
    // 9. enrichedUser getter / setter  (boolean field)
    // =========================================================================

    @Nested
    @DisplayName("enrichedUser getter and setter")
    class EnrichedUserTests {

        @Test
        @DisplayName("setEnrichedUser(true) / isEnrichedUser returns true")
        void setEnrichedUser_true() {
            SearchCriteria sc = new SearchCriteria();
            sc.setEnrichedUser(true);
            assertThat(sc.isEnrichedUser()).isTrue();
        }

        @Test
        @DisplayName("setEnrichedUser(false) / isEnrichedUser returns false")
        void setEnrichedUser_false() {
            SearchCriteria sc = new SearchCriteria();
            sc.setEnrichedUser(true);
            sc.setEnrichedUser(false);
            assertThat(sc.isEnrichedUser()).isFalse();
        }
    }

    // =========================================================================
    // 10. Setter independence – one setter must not affect any other field
    // =========================================================================

    @Test
    @DisplayName("each setter writes only its own field; all others remain at their previous values")
    void setters_doNotCrossPollinate() {
        SearchCriteria sc =
                new SearchCriteria(10, 0, "tree-id", "course", "e-id", "review", false, false);

        // Change limit only
        sc.setLimit(99);
        assertThat(sc.getOffset()).isEqualTo(0);
        assertThat(sc.getCommentTreeId()).isEqualTo("tree-id");
        assertThat(sc.getEntityType()).isEqualTo("course");
        assertThat(sc.getEntityId()).isEqualTo("e-id");
        assertThat(sc.getWorkflow()).isEqualTo("review");
        assertThat(sc.isOverrideCache()).isFalse();
        assertThat(sc.isEnrichedUser()).isFalse();

        // Toggle both booleans
        sc.setOverrideCache(true);
        sc.setEnrichedUser(true);
        assertThat(sc.getLimit()).isEqualTo(99); // unchanged from above
        assertThat(sc.getOffset()).isEqualTo(0); // still original
    }
}

