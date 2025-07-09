package com.tarento.commenthub.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTreeIdentifierDTOTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO(
                "post",
                "123",
                "workflow1"
        );

        assertEquals("post", dto.getEntityType());
        assertEquals("123", dto.getEntityId());
        assertEquals("workflow1", dto.getWorkflow());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO();

        dto.setEntityType("comment");
        dto.setEntityId("456");
        dto.setWorkflow("workflow2");

        assertEquals("comment", dto.getEntityType());
        assertEquals("456", dto.getEntityId());
        assertEquals("workflow2", dto.getWorkflow());
    }
}
