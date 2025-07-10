package com.tarento.commenthub.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchCriteriaTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        SearchCriteria criteria = new SearchCriteria(
                10,
                0,
                "tree123",
                "post",
                "entity42",
                "workflow1",
                true,
                false
        );

        assertEquals(10, criteria.getLimit());
        assertEquals(0, criteria.getOffset());
        assertEquals("tree123", criteria.getCommentTreeId());
        assertEquals("post", criteria.getEntityType());
        assertEquals("entity42", criteria.getEntityId());
        assertEquals("workflow1", criteria.getWorkflow());
        assertTrue(criteria.isOverrideCache());
        assertFalse(criteria.isEnrichedUser());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        SearchCriteria criteria = new SearchCriteria();

        criteria.setLimit(20);
        criteria.setOffset(5);
        criteria.setCommentTreeId("tree456");
        criteria.setEntityType("comment");
        criteria.setEntityId("entity99");
        criteria.setWorkflow("workflow2");
        criteria.setOverrideCache(false);
        criteria.setEnrichedUser(true);

        assertEquals(20, criteria.getLimit());
        assertEquals(5, criteria.getOffset());
        assertEquals("tree456", criteria.getCommentTreeId());
        assertEquals("comment", criteria.getEntityType());
        assertEquals("entity99", criteria.getEntityId());
        assertEquals("workflow2", criteria.getWorkflow());
        assertFalse(criteria.isOverrideCache());
        assertTrue(criteria.isEnrichedUser());
    }
}
