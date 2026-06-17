package com.tarento.commenthub.exception;

import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("RestExceptionHandling – 100% Coverage Tests")
class RestExceptionHandlingTest {

    /**
     * System under test — no dependencies, can be instantiated directly.
     */
    private RestExceptionHandling handler;

    @BeforeEach
    void setUp() {
        handler = new RestExceptionHandling();
    }

    // =========================================================================
    // Branch B1 = FALSE  (ex is NOT a CommentException)
    // =========================================================================

    @Nested
    @DisplayName("B1=false — generic Exception → HTTP 500")
    class GenericExceptionTests {

        @Test
        @DisplayName("plain RuntimeException returns 500 with code = exception message")
        void genericException_returns500_withExceptionMessageAsCode() {
            RuntimeException ex = new RuntimeException("database timeout");

            ResponseEntity<?> response = handler.handleException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            ErrorResponse body = (ErrorResponse) response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getCode()).isEqualTo("database timeout");
            // message and httpStatusCode are not set for generic exceptions
            assertThat(body.getMessage()).isNull();
            assertThat(body.getHttpStatusCode()).isNull();
        }

        @Test
        @DisplayName("generic Exception with null message — code field is null")
        void genericException_nullMessage_codeIsNull() {
            Exception ex = new Exception((String) null);

            ResponseEntity<?> response = handler.handleException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            ErrorResponse body = (ErrorResponse) response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getCode()).isNull();
        }

        @Test
        @DisplayName("NullPointerException (subtype of RuntimeException) returns 500")
        void nullPointerException_returns500() {
            NullPointerException ex = new NullPointerException("npe message");

            ResponseEntity<?> response = handler.handleException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =========================================================================
    // Branch B1 = TRUE, B2 = TRUE, B3 = TRUE
    // (CommentException with non-null httpStatusCode and non-blank message)
    // =========================================================================

    @Nested
    @DisplayName("B1=true, B2=true, B3=true — CommentException with status and message")
    class CommentExceptionWithStatusAndMessageTests {

        @Test
        @DisplayName("returns HTTP 200 wrapper; ErrorResponse carries code, message, httpStatusCode from exception")
        void commentException_nonNullStatusAndNonBlankMessage_returnsOk() {
            // B2=true  → httpStatusCode != null  → use 404
            // B3=true  → message is non-blank    → log.error is called
            CommentException ex = new CommentException("NOT_FOUND", "Comment tree not found", 404);

            ResponseEntity<?> response = handler.handleException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            ErrorResponse body = (ErrorResponse) response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getCode()).isEqualTo("NOT_FOUND");
            assertThat(body.getMessage()).isEqualTo("Comment tree not found");
            assertThat(body.getHttpStatusCode()).isEqualTo(404);
        }

        @Test
        @DisplayName("httpStatusCode=200 in exception is preserved in ErrorResponse")
        void commentException_httpStatusCode200_preserved() {
            CommentException ex = new CommentException("OK_CODE", "All good", 200);

            ResponseEntity<?> response = handler.handleException(ex);

            ErrorResponse body = (ErrorResponse) response.getBody();
            Assertions.assertNotNull(body);
            assertThat(body.getHttpStatusCode()).isEqualTo(200);
        }

        @Test
        @DisplayName("httpStatusCode=500 in exception is preserved in ErrorResponse")
        void commentException_httpStatusCode500_preserved() {
            CommentException ex = new CommentException("SERVER_ERR", "Internal failure", 500);

            ResponseEntity<?> response = handler.handleException(ex);

            ErrorResponse body = (ErrorResponse) response.getBody();
            Assertions.assertNotNull(body);
            assertThat(body.getHttpStatusCode()).isEqualTo(500);
            assertThat(body.getMessage()).isEqualTo("Internal failure");
        }
    }

    // =========================================================================
    // Branch B1 = TRUE, B2 = FALSE, B3 = FALSE
    // (CommentException with null httpStatusCode and blank / null message)
    // =========================================================================

    @Nested
    @DisplayName("B1=true, B2=false, B3=false — CommentException null status + blank message")
    class CommentExceptionNullStatusBlankMessageTests {

        @Test
        @DisplayName("null httpStatusCode falls back to HttpStatus.OK.value()=200; blank message skips log.error")
        void commentException_nullHttpStatus_defaultsTo200() {

            CommentException ex = new CommentException("ERR", "");

            ResponseEntity<?> response = handler.handleException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            ErrorResponse body = (ErrorResponse) response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.getCode()).isEqualTo("ERR");
            // Falls back to HttpStatus.OK.value() because getHttpStatusCode() was null
            assertThat(body.getHttpStatusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("null message (isNotBlank=false) also skips log.error")
        void commentException_nullMessage_skipsLogError() {
            CommentException ex = new CommentException("CODE", null);
            // httpStatusCode is null from 2-arg constructor → B2=false

            ResponseEntity<?> response = handler.handleException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            ErrorResponse body = (ErrorResponse) response.getBody();
            Assertions.assertNotNull(body);
            assertThat(body.getHttpStatusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(body.getMessage()).isNull();
        }

        @Test
        @DisplayName("whitespace-only message (isNotBlank=false) skips log.error")
        void commentException_blankMessage_skipsLogError() {
            // B3=false → message is blank (whitespace only)
            CommentException ex = new CommentException("CODE", "   ");

            ResponseEntity<?> response = handler.handleException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            ErrorResponse body = (ErrorResponse) response.getBody();
            Assertions.assertNotNull(body);
            assertThat(body.getMessage()).isEqualTo("   ");
        }
    }

    // =========================================================================
    // Constructor of RestExceptionHandling (implicitly tested, but explicit here)
    // =========================================================================

    @Test
    @DisplayName("RestExceptionHandling can be instantiated directly (no-args constructor)")
    void restExceptionHandling_canBeInstantiated() {
        RestExceptionHandling h = new RestExceptionHandling();
        assertThat(h).isNotNull();
    }
}

