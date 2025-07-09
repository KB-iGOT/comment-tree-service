package com.tarento.commenthub.dto;

import com.tarento.commenthub.entity.CommentTree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentsResoponseDTOTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        CommentTree tree = new CommentTree();
        CommentsResoponseDTO dto = new CommentsResoponseDTO(tree, 5);

        assertSame(tree, dto.getCommentTree());
        assertEquals(5, dto.getCommentCount());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        CommentTree tree = new CommentTree();
        CommentsResoponseDTO dto = new CommentsResoponseDTO();

        dto.setCommentTree(tree);
        dto.setCommentCount(10);

        assertSame(tree, dto.getCommentTree());
        assertEquals(10, dto.getCommentCount());
    }
}
