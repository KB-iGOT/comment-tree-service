package com.tarento.commenthub.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class CommentTreeTest {

    @Test
    void testAllArgsConstructorAndGetters() throws Exception {
        String treeId = "tree123";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree("{\"key\":\"value\"}");
        String status = "active";
        Timestamp created = new Timestamp(System.currentTimeMillis());
        Timestamp updated = new Timestamp(System.currentTimeMillis());

        CommentTree commentTree = new CommentTree(treeId, jsonNode, status, created, updated);

        assertEquals(treeId, commentTree.getCommentTreeId());
        assertEquals(jsonNode, commentTree.getCommentTreeData());
        assertEquals(status, commentTree.getStatus());
        assertEquals(created, commentTree.getCreatedDate());
        assertEquals(updated, commentTree.getLastUpdatedDate());
    }

    @Test
    void testNoArgsConstructorAndSetters() throws Exception {
        CommentTree commentTree = new CommentTree();

        String treeId = "tree456";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree("{\"foo\":\"bar\"}");
        String status = "inactive";
        Timestamp created = new Timestamp(System.currentTimeMillis());
        Timestamp updated = new Timestamp(System.currentTimeMillis());

        commentTree.setCommentTreeId(treeId);
        commentTree.setCommentTreeData(jsonNode);
        commentTree.setStatus(status);
        commentTree.setCreatedDate(created);
        commentTree.setLastUpdatedDate(updated);

        assertEquals(treeId, commentTree.getCommentTreeId());
        assertEquals(jsonNode, commentTree.getCommentTreeData());
        assertEquals(status, commentTree.getStatus());
        assertEquals(created, commentTree.getCreatedDate());
        assertEquals(updated, commentTree.getLastUpdatedDate());
    }
}
