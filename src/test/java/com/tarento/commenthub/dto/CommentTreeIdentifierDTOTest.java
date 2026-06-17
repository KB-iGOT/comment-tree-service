package com.tarento.commenthub.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("CommentTreeIdentifierDTO – 100% Coverage Tests")
class CommentTreeIdentifierDTOTest {

    // =========================================================================
    // 1. Constructors
    // =========================================================================

    @Nested
    @DisplayName("Constructors")
    class ConstructorTests {

        @Test
        @DisplayName("no-args constructor creates instance with all null fields")
        void noArgsConstructor_allFieldsNull() {
            CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO();

            assertThat(dto.getEntityType()).isNull();
            assertThat(dto.getEntityId()).isNull();
            assertThat(dto.getWorkflow()).isNull();
        }

        @Test
        @DisplayName("all-args constructor sets every field")
        void allArgsConstructor_setsAllFields() {
            CommentTreeIdentifierDTO dto =
                    new CommentTreeIdentifierDTO("course", "entity-001", "review");

            assertThat(dto.getEntityType()).isEqualTo("course");
            assertThat(dto.getEntityId()).isEqualTo("entity-001");
            assertThat(dto.getWorkflow()).isEqualTo("review");
        }

        @Test
        @DisplayName("all-args constructor with null values stores nulls")
        void allArgsConstructor_nullValues() {
            CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO(null, null, null);

            assertThat(dto.getEntityType()).isNull();
            assertThat(dto.getEntityId()).isNull();
            assertThat(dto.getWorkflow()).isNull();
        }
    }

    // =========================================================================
    // 2. entityType getter / setter
    // =========================================================================

    @Nested
    @DisplayName("entityType getter and setter")
    class EntityTypeTests {

        @Test
        @DisplayName("setEntityType / getEntityType round-trip")
        void setEntityType_getEntityType_roundTrip() {
            CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO();
            dto.setEntityType("discussion");
            assertThat(dto.getEntityType()).isEqualTo("discussion");
        }

        @ParameterizedTest(name = "setEntityType(\"{0}\") is accepted")
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "COURSE", "module"})
        @DisplayName("setEntityType accepts null, empty, blank, and non-blank strings")
        void setEntityType_variousValues(String value) {
            CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO();
            dto.setEntityType(value);
            assertThat(dto.getEntityType()).isEqualTo(value);
        }
    }

    // =========================================================================
    // 3. entityId getter / setter
    // =========================================================================

    @Nested
    @DisplayName("entityId getter and setter")
    class EntityIdTests {

        @Test
        @DisplayName("setEntityId / getEntityId round-trip")
        void setEntityId_getEntityId_roundTrip() {
            CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO();
            dto.setEntityId("entity-42");
            assertThat(dto.getEntityId()).isEqualTo("entity-42");
        }

        @ParameterizedTest(name = "setEntityId(\"{0}\") is accepted")
        @NullAndEmptySource
        @ValueSource(strings = {"uuid-1234-5678", "0"})
        @DisplayName("setEntityId accepts null, empty, and non-empty strings")
        void setEntityId_variousValues(String value) {
            CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO();
            dto.setEntityId(value);
            assertThat(dto.getEntityId()).isEqualTo(value);
        }
    }

    // =========================================================================
    // 4. workflow getter / setter
    // =========================================================================

    @Nested
    @DisplayName("workflow getter and setter")
    class WorkflowTests {

        @Test
        @DisplayName("setWorkflow / getWorkflow round-trip")
        void setWorkflow_getWorkflow_roundTrip() {
            CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO();
            dto.setWorkflow("publish");
            assertThat(dto.getWorkflow()).isEqualTo("publish");
        }

        @ParameterizedTest(name = "setWorkflow(\"{0}\") is accepted")
        @NullAndEmptySource
        @ValueSource(strings = {"review", "approve", "REJECT"})
        @DisplayName("setWorkflow accepts null, empty, and non-empty strings")
        void setWorkflow_variousValues(String value) {
            CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO();
            dto.setWorkflow(value);
            assertThat(dto.getWorkflow()).isEqualTo(value);
        }
    }

    // =========================================================================
    // 5. Setter independence – changing one field must not affect others
    // =========================================================================

    @Test
    @DisplayName("setting one field via setter does not alter the other two fields")
    void setters_doNotCrossPollinate() {
        CommentTreeIdentifierDTO dto =
                new CommentTreeIdentifierDTO("course", "entity-001", "review");

        dto.setEntityType("module"); // change only entityType
        assertThat(dto.getEntityId()).isEqualTo("entity-001");
        assertThat(dto.getWorkflow()).isEqualTo("review");

        dto.setEntityId("entity-999"); // change only entityId
        assertThat(dto.getEntityType()).isEqualTo("module");
        assertThat(dto.getWorkflow()).isEqualTo("review");

        dto.setWorkflow("approve"); // change only workflow
        assertThat(dto.getEntityType()).isEqualTo("module");
        assertThat(dto.getEntityId()).isEqualTo("entity-999");
    }
}

