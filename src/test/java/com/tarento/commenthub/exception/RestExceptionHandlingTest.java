package com.tarento.commenthub.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class RestExceptionHandlingTest {

    private RestExceptionHandling handler;

    @BeforeEach
    void setUp() {
        handler = new RestExceptionHandling();
    }

    @Test
    void testHandleCommentException_withMessage() {
        CommentException ex = new CommentException("ERR001", "Some error occurred", HttpStatus.BAD_REQUEST.value());

        ResponseEntity<?> response = handler.handleException(ex);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof ErrorResponse);

        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("ERR001", error.getCode());
        assertEquals("Some error occurred", error.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getHttpStatusCode());
    }

    @Test
    void testHandleCommentException_withNullMessage() {
        CommentException ex = new CommentException("ERR002", null, null);

        ResponseEntity<?> response = handler.handleException(ex);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof ErrorResponse);

        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("ERR002", error.getCode());
        assertNull(error.getMessage());
        assertEquals(HttpStatus.OK.value(), error.getHttpStatusCode());
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("UnexpectedError");

        ResponseEntity<?> response = handler.handleException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof ErrorResponse);

        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("UnexpectedError", error.getCode());
        assertNull(error.getMessage());
        assertNull(error.getHttpStatusCode());
    }
}
