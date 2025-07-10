package com.tarento.commenthub.exception;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommentExceptionTest {

    @Test
    void testNoArgsConstructor() {
        CommentException ex = new CommentException();
        assertNull(ex.getCode());
        assertNull(ex.getMessage());
        assertNull(ex.getHttpStatusCode());
        assertNull(ex.getErrors());
    }

    @Test
    void testTwoArgConstructor() {
        CommentException ex = new CommentException("ERR001", "Something went wrong");
        assertEquals("ERR001", ex.getCode());
        assertEquals("Something went wrong", ex.getMessage());
        assertNull(ex.getHttpStatusCode());
        assertNull(ex.getErrors());
    }

    @Test
    void testThreeArgConstructor() {
        CommentException ex = new CommentException("ERR002", "Bad Request", 400);
        assertEquals("ERR002", ex.getCode());
        assertEquals("Bad Request", ex.getMessage());
        assertEquals(400, ex.getHttpStatusCode());
        assertNull(ex.getErrors());
    }

    @Test
    void testMapConstructor() {
        Map<String, String> errors = new HashMap<>();
        errors.put("field1", "must not be null");
        errors.put("field2", "must be valid");

        CommentException ex = new CommentException(errors);
        assertEquals(errors.toString(), ex.getMessage());
        assertEquals(errors, ex.getErrors());
        assertNull(ex.getCode());
        assertNull(ex.getHttpStatusCode());
    }

    @Test
    void testSettersAndGetters() {
        CommentException ex = new CommentException();

        ex.setCode("ERR003");
        ex.setMessage("Another error");
        ex.setHttpStatusCode(500);

        Map<String, String> errors = new HashMap<>();
        errors.put("key", "value");
        ex.setErrors(errors);

        assertEquals("ERR003", ex.getCode());
        assertEquals("Another error", ex.getMessage());
        assertEquals(500, ex.getHttpStatusCode());
        assertEquals(errors, ex.getErrors());
    }
}
