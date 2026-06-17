package com.tarento.commenthub.dto;

import com.tarento.commenthub.entity.CommentTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CommentsResoponseDTO – 100% Coverage Tests")
class CommentsResoponseDTOTest {

    // =========================================================================
    // 1. Constructors
    // =========================================================================

    @Nested
    @DisplayName("Constructors")
    class ConstructorTests {

        @Test
        @DisplayName("no-args constructor creates instance with null tree and zero count")
        void noArgsConstructor_defaultValues() {
            CommentsResoponseDTO dto = new CommentsResoponseDTO();

            assertThat(dto.getCommentTree()).isNull();
            assertThat(dto.getCommentCount()).isZero();
        }

        @Test
        @DisplayName("all-args constructor sets both fields")
        void allArgsConstructor_setsAllFields() {
            CommentTree tree = buildCommentTree("tree-001");

            CommentsResoponseDTO dto = new CommentsResoponseDTO(tree, 5);

            assertThat(dto.getCommentTree()).isSameAs(tree);
            assertThat(dto.getCommentCount()).isEqualTo(5);
        }

        @Test
        @DisplayName("all-args constructor with null tree and zero count is allowed")
        void allArgsConstructor_nullTreeZeroCount() {
            CommentsResoponseDTO dto = new CommentsResoponseDTO(null, 0);

            assertThat(dto.getCommentTree()).isNull();
            assertThat(dto.getCommentCount()).isZero();
        }

        @Test
        @DisplayName("all-args constructor with negative count stores the value as-is")
        void allArgsConstructor_negativeCount() {
            CommentsResoponseDTO dto = new CommentsResoponseDTO(null, -1);

            assertThat(dto.getCommentCount()).isEqualTo(-1);
        }
    }

    // =========================================================================
    // 2. commentTree getter / setter
    // =========================================================================

    @Nested
    @DisplayName("commentTree getter and setter")
    class CommentTreeTests {

        @Test
        @DisplayName("setCommentTree / getCommentTree round-trip")
        void setCommentTree_getCommentTree_roundTrip() {
            CommentsResoponseDTO dto = new CommentsResoponseDTO();
            CommentTree tree = buildCommentTree("tree-002");
            dto.setCommentTree(tree);
            assertThat(dto.getCommentTree()).isSameAs(tree);
        }

        @Test
        @DisplayName("setCommentTree with null clears the field")
        void setCommentTree_null() {
            CommentsResoponseDTO dto = new CommentsResoponseDTO(buildCommentTree("t"), 1);
            dto.setCommentTree(null);
            assertThat(dto.getCommentTree()).isNull();
        }

        @Test
        @DisplayName("getCommentTree exposes the correct entity id after set")
        void getCommentTree_exposesCorrectEntityId() {
            CommentTree tree = buildCommentTree("specific-id");
            CommentsResoponseDTO dto = new CommentsResoponseDTO();
            dto.setCommentTree(tree);
            assertThat(dto.getCommentTree().getCommentTreeId()).isEqualTo("specific-id");
        }
    }

    // =========================================================================
    // 3. commentCount getter / setter
    // =========================================================================

    @Nested
    @DisplayName("commentCount getter and setter")
    class CommentCountTests {

        @Test
        @DisplayName("setCommentCount / getCommentCount round-trip with positive value")
        void setCommentCount_getCommentCount_positive() {
            CommentsResoponseDTO dto = new CommentsResoponseDTO();
            dto.setCommentCount(42);
            assertThat(dto.getCommentCount()).isEqualTo(42);
        }

        @Test
        @DisplayName("setCommentCount with zero stores zero")
        void setCommentCount_zero() {
            CommentsResoponseDTO dto = new CommentsResoponseDTO(null, 99);
            dto.setCommentCount(0);
            assertThat(dto.getCommentCount()).isZero();
        }

        @Test
        @DisplayName("setCommentCount with negative value is stored as-is (no validation in DTO)")
        void setCommentCount_negative() {
            CommentsResoponseDTO dto = new CommentsResoponseDTO();
            dto.setCommentCount(-5);
            assertThat(dto.getCommentCount()).isEqualTo(-5);
        }
    }

    // =========================================================================
    // 4. Setter independence
    // =========================================================================

    @Test
    @DisplayName("setting commentTree does not alter commentCount and vice versa")
    void setters_doNotCrossPollinate() {
        CommentsResoponseDTO dto = new CommentsResoponseDTO(buildCommentTree("t"), 10);

        dto.setCommentTree(null);
        assertThat(dto.getCommentCount()).isEqualTo(10); // unchanged

        dto.setCommentCount(20);
        assertThat(dto.getCommentTree()).isNull(); // unchanged
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private static CommentTree buildCommentTree(String id) {
        CommentTree tree = new CommentTree();
        tree.setCommentTreeId(id);
        tree.setStatus("active");
        tree.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        return tree;
    }
}

