package com.tarento.commenthub.dto;

import com.tarento.commenthub.entity.CommentTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("ResponseDTO – 100% Coverage Tests")
class ResponseDTOTest {

    // =========================================================================
    // 1. Constructors
    // =========================================================================

    @Nested
    @DisplayName("Constructors")
    class ConstructorTests {

        @Test
        @DisplayName("no-args constructor creates instance with null commentTree")
        void noArgsConstructor_nullCommentTree() {
            ResponseDTO dto = new ResponseDTO();

            assertThat(dto.getCommentTree()).isNull();
        }

        @Test
        @DisplayName("all-args constructor sets commentTree to the supplied entity")
        void allArgsConstructor_setsCommentTree() {
            CommentTree tree = buildCommentTree("tree-100");

            ResponseDTO dto = new ResponseDTO(tree);

            assertThat(dto.getCommentTree()).isSameAs(tree);
        }

        @Test
        @DisplayName("all-args constructor with null is allowed")
        void allArgsConstructor_nullArgument() {
            ResponseDTO dto = new ResponseDTO(null);

            assertThat(dto.getCommentTree()).isNull();
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
            ResponseDTO dto = new ResponseDTO();
            CommentTree tree = buildCommentTree("tree-200");
            dto.setCommentTree(tree);
            assertThat(dto.getCommentTree()).isSameAs(tree);
        }

        @Test
        @DisplayName("setCommentTree with null clears the previous tree")
        void setCommentTree_null_clearsPreviousTree() {
            ResponseDTO dto = new ResponseDTO(buildCommentTree("tree-300"));
            dto.setCommentTree(null);
            assertThat(dto.getCommentTree()).isNull();
        }

        @Test
        @DisplayName("setCommentTree replaces the previously set tree with a new one")
        void setCommentTree_replacesPreviousTree() {
            ResponseDTO dto = new ResponseDTO();
            CommentTree firstTree = buildCommentTree("first");
            CommentTree secondTree = buildCommentTree("second");

            dto.setCommentTree(firstTree);
            assertThat(dto.getCommentTree().getCommentTreeId()).isEqualTo("first");

            dto.setCommentTree(secondTree);
            assertThat(dto.getCommentTree().getCommentTreeId()).isEqualTo("second");
        }

        @Test
        @DisplayName("getCommentTree exposes the internal state of the stored entity")
        void getCommentTree_exposesEntityState() {
            CommentTree tree = buildCommentTree("state-tree");
            tree.setStatus("inactive");

            ResponseDTO dto = new ResponseDTO(tree);

            assertThat(dto.getCommentTree().getStatus()).isEqualTo("inactive");
            assertThat(dto.getCommentTree().getCommentTreeId()).isEqualTo("state-tree");
        }
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

