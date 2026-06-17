package com.tarento.commenthub.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Timestamp;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CommentTree entity – 100% Coverage Tests")
class CommentTreeTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static Timestamp now() {
        return Timestamp.from(Instant.now());
    }

    // =========================================================================
    // 1. No-args constructor
    // =========================================================================

    @Nested
    @DisplayName("No-args constructor")
    class NoArgsConstructorTests {

        @Test
        @DisplayName("creates an instance with every field set to null / default")
        void noArgsConstructor_allFieldsNull() {
            CommentTree entity = new CommentTree();

            assertThat(entity.getCommentTreeId()).isNull();
            assertThat(entity.getCommentTreeData()).isNull();
            assertThat(entity.getStatus()).isNull();
            assertThat(entity.getCreatedDate()).isNull();
            assertThat(entity.getLastUpdatedDate()).isNull();
        }
    }

    // =========================================================================
    // 2. All-args constructor
    // =========================================================================

    @Nested
    @DisplayName("All-args constructor")
    class AllArgsConstructorTests {

        @Test
        @DisplayName("sets all five fields to the supplied values")
        void allArgsConstructor_setsAllFields() throws Exception {
            JsonNode data = MAPPER.readTree("{\"key\":\"value\"}");
            Timestamp created = now();
            Timestamp updated = now();

            CommentTree entity = new CommentTree("tree-001", data, "active", created, updated);

            assertThat(entity.getCommentTreeId()).isEqualTo("tree-001");
            assertThat(entity.getCommentTreeData()).isSameAs(data);
            assertThat(entity.getStatus()).isEqualTo("active");
            assertThat(entity.getCreatedDate()).isSameAs(created);
            assertThat(entity.getLastUpdatedDate()).isSameAs(updated);
        }

        @Test
        @DisplayName("all-args constructor with null for every parameter stores null in every field")
        void allArgsConstructor_allNullParams_storesNull() {
            CommentTree entity = new CommentTree(null, null, null, null, null);

            assertThat(entity.getCommentTreeId()).isNull();
            assertThat(entity.getCommentTreeData()).isNull();
            assertThat(entity.getStatus()).isNull();
            assertThat(entity.getCreatedDate()).isNull();
            assertThat(entity.getLastUpdatedDate()).isNull();
        }

        @Test
        @DisplayName("two distinct instances created with the same arguments are independent")
        void allArgsConstructor_instancesAreIndependent() throws Exception {
            JsonNode data = MAPPER.readTree("{\"a\":1}");
            Timestamp ts = now();

            CommentTree a = new CommentTree("id", data, "active", ts, ts);
            CommentTree b = new CommentTree("id", data, "active", ts, ts);

            // Same values but different instances
            assertThat(a).isNotSameAs(b);
            assertThat(a.getCommentTreeId()).isEqualTo(b.getCommentTreeId());
        }
    }

    // =========================================================================
    // 3. commentTreeId getter / setter
    // =========================================================================

    @Nested
    @DisplayName("commentTreeId getter and setter")
    class CommentTreeIdTests {

        @Test
        @DisplayName("setCommentTreeId / getCommentTreeId round-trip")
        void setCommentTreeId_getCommentTreeId_roundTrip() {
            CommentTree entity = new CommentTree();
            entity.setCommentTreeId("ct-abc-123");
            assertThat(entity.getCommentTreeId()).isEqualTo("ct-abc-123");
        }

        @ParameterizedTest(name = "setCommentTreeId(\"{0}\") stores the value as-is")
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "uuid-1234-5678", "jwt.abc.def.ghi"})
        @DisplayName("setCommentTreeId accepts null, empty, blank, and any non-blank string")
        void setCommentTreeId_variousValues(String id) {
            CommentTree entity = new CommentTree();
            entity.setCommentTreeId(id);
            assertThat(entity.getCommentTreeId()).isEqualTo(id);
        }

        @Test
        @DisplayName("setCommentTreeId overwrites a previously set id")
        void setCommentTreeId_overwritesPreviousValue() {
            CommentTree entity = new CommentTree();
            entity.setCommentTreeId("old-id");
            entity.setCommentTreeId("new-id");
            assertThat(entity.getCommentTreeId()).isEqualTo("new-id");
        }
    }

    // =========================================================================
    // 4. commentTreeData getter / setter
    // =========================================================================

    @Nested
    @DisplayName("commentTreeData getter and setter")
    class CommentTreeDataTests {

        @Test
        @DisplayName("setCommentTreeData / getCommentTreeData round-trip with a simple JSON object")
        void setCommentTreeData_simpleObject_roundTrip() throws Exception {
            CommentTree entity = new CommentTree();
            JsonNode data = MAPPER.readTree("{\"entityId\":\"e1\",\"entityType\":\"course\"}");
            entity.setCommentTreeData(data);
            assertThat(entity.getCommentTreeData()).isSameAs(data);
        }

        @Test
        @DisplayName("setCommentTreeData stores a JSON array node")
        void setCommentTreeData_arrayNode() {
            CommentTree entity = new CommentTree();
            ArrayNode array = JsonNodeFactory.instance.arrayNode();
            array.add("comment-1");
            array.add("comment-2");
            entity.setCommentTreeData(array);
            assertThat(entity.getCommentTreeData().isArray()).isTrue();
            assertThat(entity.getCommentTreeData().size()).isEqualTo(2);
        }

        @Test
        @DisplayName("setCommentTreeData stores a deeply nested ObjectNode")
        void setCommentTreeData_nestedObjectNode() {
            CommentTree entity = new CommentTree();
            ObjectNode root = JsonNodeFactory.instance.objectNode();
            ObjectNode nested = JsonNodeFactory.instance.objectNode();
            nested.put("commentId", "c-001");
            nested.put("content", "Hello World");
            root.set("comment", nested);
            root.put("parentId", (String) null);

            entity.setCommentTreeData(root);

            assertThat(entity.getCommentTreeData().get("comment").get("commentId").asText())
                    .isEqualTo("c-001");
        }

        @Test
        @DisplayName("setCommentTreeData with null clears the previous data")
        void setCommentTreeData_null() throws Exception {
            CommentTree entity = new CommentTree();
            entity.setCommentTreeData(MAPPER.readTree("{\"x\":1}"));
            entity.setCommentTreeData(null);
            assertThat(entity.getCommentTreeData()).isNull();
        }

        @Test
        @DisplayName("setCommentTreeData with an empty JSON object stores it correctly")
        void setCommentTreeData_emptyObjectNode() {
            CommentTree entity = new CommentTree();
            ObjectNode empty = JsonNodeFactory.instance.objectNode();
            entity.setCommentTreeData(empty);
            assertThat(entity.getCommentTreeData().isEmpty()).isTrue();
        }
    }

    // =========================================================================
    // 5. status getter / setter
    // =========================================================================

    @Nested
    @DisplayName("status getter and setter")
    class StatusTests {

        @Test
        @DisplayName("setStatus / getStatus round-trip with 'active'")
        void setStatus_active_roundTrip() {
            CommentTree entity = new CommentTree();
            entity.setStatus("active");
            assertThat(entity.getStatus()).isEqualTo("active");
        }

        @Test
        @DisplayName("setStatus / getStatus round-trip with 'inactive'")
        void setStatus_inactive_roundTrip() {
            CommentTree entity = new CommentTree();
            entity.setStatus("inactive");
            assertThat(entity.getStatus()).isEqualTo("inactive");
        }

        @ParameterizedTest(name = "setStatus(\"{0}\") is stored as-is")
        @NullAndEmptySource
        @ValueSource(strings = {"DELETED", "archived", "pending"})
        @DisplayName("setStatus accepts null, empty, and any non-blank status string")
        void setStatus_variousValues(String status) {
            CommentTree entity = new CommentTree();
            entity.setStatus(status);
            assertThat(entity.getStatus()).isEqualTo(status);
        }

        @Test
        @DisplayName("setStatus overwrites a previously set status value")
        void setStatus_overwritesPreviousValue() {
            CommentTree entity = new CommentTree();
            entity.setStatus("active");
            entity.setStatus("inactive");
            assertThat(entity.getStatus()).isEqualTo("inactive");
        }
    }

    // =========================================================================
    // 6. createdDate getter / setter
    // =========================================================================

    @Nested
    @DisplayName("createdDate getter and setter")
    class CreatedDateTests {

        @Test
        @DisplayName("setCreatedDate / getCreatedDate round-trip")
        void setCreatedDate_getCreatedDate_roundTrip() {
            CommentTree entity = new CommentTree();
            Timestamp ts = now();
            entity.setCreatedDate(ts);
            assertThat(entity.getCreatedDate()).isSameAs(ts);
        }

        @Test
        @DisplayName("setCreatedDate with null clears the timestamp")
        void setCreatedDate_null() {
            CommentTree entity = new CommentTree();
            entity.setCreatedDate(now());
            entity.setCreatedDate(null);
            assertThat(entity.getCreatedDate()).isNull();
        }

        @Test
        @DisplayName("setCreatedDate preserves the exact millisecond value")
        void setCreatedDate_preservesMillis() {
            CommentTree entity = new CommentTree();
            long epochMs = 1_700_000_000_000L;
            Timestamp ts = new Timestamp(epochMs);
            entity.setCreatedDate(ts);
            assertThat(entity.getCreatedDate().getTime()).isEqualTo(epochMs);
        }
    }

    // =========================================================================
    // 7. lastUpdatedDate getter / setter
    // =========================================================================

    @Nested
    @DisplayName("lastUpdatedDate getter and setter")
    class LastUpdatedDateTests {

        @Test
        @DisplayName("setLastUpdatedDate / getLastUpdatedDate round-trip")
        void setLastUpdatedDate_getLastUpdatedDate_roundTrip() {
            CommentTree entity = new CommentTree();
            Timestamp ts = now();
            entity.setLastUpdatedDate(ts);
            assertThat(entity.getLastUpdatedDate()).isSameAs(ts);
        }

        @Test
        @DisplayName("setLastUpdatedDate with null clears the timestamp")
        void setLastUpdatedDate_null() {
            CommentTree entity = new CommentTree();
            entity.setLastUpdatedDate(now());
            entity.setLastUpdatedDate(null);
            assertThat(entity.getLastUpdatedDate()).isNull();
        }

        @Test
        @DisplayName("setLastUpdatedDate can be a time later than createdDate")
        void setLastUpdatedDate_laterThanCreatedDate() {
            CommentTree entity = new CommentTree();
            Timestamp created = new Timestamp(1_000_000L);
            Timestamp updated = new Timestamp(2_000_000L);

            entity.setCreatedDate(created);
            entity.setLastUpdatedDate(updated);

            assertThat(entity.getLastUpdatedDate().getTime())
                    .isGreaterThan(entity.getCreatedDate().getTime());
        }
    }

    // =========================================================================
    // 8. Setter independence – mutating one field must not affect any other
    // =========================================================================

    @Nested
    @DisplayName("Setter independence")
    class SetterIndependenceTests {

        private CommentTree entity;
        private JsonNode originalData;
        private Timestamp originalCreated;
        private Timestamp originalUpdated;

        @BeforeEach
        void setUp() throws Exception {
            originalData = MAPPER.readTree("{\"root\":true}");
            originalCreated = new Timestamp(1_000L);
            originalUpdated = new Timestamp(2_000L);

            entity = new CommentTree(
                    "original-id",
                    originalData,
                    "active",
                    originalCreated,
                    originalUpdated);
        }

        @Test
        @DisplayName("changing commentTreeId does not affect other fields")
        void setCommentTreeId_doesNotAffectOtherFields() {
            entity.setCommentTreeId("changed-id");

            assertThat(entity.getCommentTreeData()).isSameAs(originalData);
            assertThat(entity.getStatus()).isEqualTo("active");
            assertThat(entity.getCreatedDate()).isSameAs(originalCreated);
            assertThat(entity.getLastUpdatedDate()).isSameAs(originalUpdated);
        }

        @Test
        @DisplayName("changing commentTreeData does not affect other fields")
        void setCommentTreeData_doesNotAffectOtherFields() {
            entity.setCommentTreeData(JsonNodeFactory.instance.objectNode());

            assertThat(entity.getCommentTreeId()).isEqualTo("original-id");
            assertThat(entity.getStatus()).isEqualTo("active");
            assertThat(entity.getCreatedDate()).isSameAs(originalCreated);
            assertThat(entity.getLastUpdatedDate()).isSameAs(originalUpdated);
        }

        @Test
        @DisplayName("changing status does not affect other fields")
        void setStatus_doesNotAffectOtherFields() {
            entity.setStatus("inactive");

            assertThat(entity.getCommentTreeId()).isEqualTo("original-id");
            assertThat(entity.getCommentTreeData()).isSameAs(originalData);
            assertThat(entity.getCreatedDate()).isSameAs(originalCreated);
            assertThat(entity.getLastUpdatedDate()).isSameAs(originalUpdated);
        }

        @Test
        @DisplayName("changing createdDate does not affect other fields")
        void setCreatedDate_doesNotAffectOtherFields() {
            entity.setCreatedDate(new Timestamp(9_999L));

            assertThat(entity.getCommentTreeId()).isEqualTo("original-id");
            assertThat(entity.getCommentTreeData()).isSameAs(originalData);
            assertThat(entity.getStatus()).isEqualTo("active");
            assertThat(entity.getLastUpdatedDate()).isSameAs(originalUpdated);
        }

        @Test
        @DisplayName("changing lastUpdatedDate does not affect other fields")
        void setLastUpdatedDate_doesNotAffectOtherFields() {
            entity.setLastUpdatedDate(new Timestamp(9_999L));

            assertThat(entity.getCommentTreeId()).isEqualTo("original-id");
            assertThat(entity.getCommentTreeData()).isSameAs(originalData);
            assertThat(entity.getStatus()).isEqualTo("active");
            assertThat(entity.getCreatedDate()).isSameAs(originalCreated);
        }
    }
}

