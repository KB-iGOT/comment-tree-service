package com.tarento.commenthub.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DisplayName("CommentException – 100% Coverage Tests")
class CommentExceptionTest {

    // 1. No-args constructor
    @Nested
    @DisplayName("CommentException() — no-args constructor")
    class NoArgsConstructorTests {

        @Test
        @DisplayName("creates an instance with all fields null")
        void noArgsConstructor_allFieldsNull() {
            CommentException ex = new CommentException();

            assertThat(ex.getCode()).isNull();
            assertThat(ex.getMessage()).isNull();
            assertThat(ex.getHttpStatusCode()).isNull();
            assertThat(ex.getErrors()).isNull();
        }

        @Test
        @DisplayName("is a RuntimeException — can be thrown and caught")
        void noArgsConstructor_isRuntimeException() {
            assertThatThrownBy(() -> {
                throw new CommentException();
            })
                    .isInstanceOf(RuntimeException.class)
                    .isInstanceOf(CommentException.class);
        }
    }

    // 2. Two-arg constructor  CommentException(String code, String message)
    @Nested
    @DisplayName("CommentException(String code, String message) — 2-arg constructor")
    class TwoArgConstructorTests {

        @Test
        @DisplayName("sets code and message; httpStatusCode and errors remain null")
        void twoArgConstructor_setsCodeAndMessage() {
            CommentException ex = new CommentException("NOT_FOUND", "Resource not found");

            assertThat(ex.getCode()).isEqualTo("NOT_FOUND");
            assertThat(ex.getMessage()).isEqualTo("Resource not found");
            assertThat(ex.getHttpStatusCode()).isNull();
            assertThat(ex.getErrors()).isNull();
        }

        @Test
        @DisplayName("accepts null for both code and message")
        void twoArgConstructor_nullCodeAndMessage() {
            CommentException ex = new CommentException(null, null);

            assertThat(ex.getCode()).isNull();
            assertThat(ex.getMessage()).isNull();
        }

        @ParameterizedTest(name = "message = \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "Comment not found", "An error occurred"})
        @DisplayName("stores any message value as-is")
        void twoArgConstructor_variousMessageValues(String message) {
            CommentException ex = new CommentException("CODE", message);
            assertThat(ex.getMessage()).isEqualTo(message);
        }
    }


    // 3. Three-arg constructor
    //    CommentException(String code, String message, Integer httpStatusCode)
    @Nested
    @DisplayName("CommentException(String, String, Integer) — 3-arg constructor")
    class ThreeArgConstructorTests {

        @Test
        @DisplayName("sets code, message, and httpStatusCode; errors remains null")
        void threeArgConstructor_setsAllThreeFields() {
            CommentException ex = new CommentException("NOT_FOUND", "Comment not found", 404);

            assertThat(ex.getCode()).isEqualTo("NOT_FOUND");
            assertThat(ex.getMessage()).isEqualTo("Comment not found");
            assertThat(ex.getHttpStatusCode()).isEqualTo(404);
            assertThat(ex.getErrors()).isNull();
        }

        @Test
        @DisplayName("stores httpStatusCode=200")
        void threeArgConstructor_httpStatusCode200() {
            CommentException ex = new CommentException("OK", "Success", 200);
            assertThat(ex.getHttpStatusCode()).isEqualTo(200);
        }

        @Test
        @DisplayName("stores httpStatusCode=500")
        void threeArgConstructor_httpStatusCode500() {
            CommentException ex = new CommentException("ERR", "Server error", 500);
            assertThat(ex.getHttpStatusCode()).isEqualTo(500);
        }

        @Test
        @DisplayName("accepts null for all three arguments")
        void threeArgConstructor_allNullArgs() {
            CommentException ex = new CommentException(null, null, null);

            assertThat(ex.getCode()).isNull();
            assertThat(ex.getMessage()).isNull();
            assertThat(ex.getHttpStatusCode()).isNull();
        }
    }

    // =========================================================================
    // 4. Map-based constructor   CommentException(Map<String, String> errors)
    // =========================================================================

    @Nested
    @DisplayName("CommentException(Map<String,String> errors) — map-based constructor")
    class MapConstructorTests {

        @Test
        @DisplayName("sets message to errors.toString() and stores the errors map")
        void mapConstructor_setsMessageAndErrors() {
            Map<String, String> errors = new HashMap<>();
            errors.put("entityId", "must not be blank");
            errors.put("workflow", "must not be blank");

            CommentException ex = new CommentException(errors);

            // message is set to the map's toString() representation
            assertThat(ex.getMessage()).isEqualTo(errors.toString());
            // errors field holds the original map reference
            assertThat(ex.getErrors()).isSameAs(errors);
            // code is not set by this constructor
            assertThat(ex.getCode()).isNull();
            assertThat(ex.getHttpStatusCode()).isNull();
        }

        @Test
        @DisplayName("empty map produces message = \"{}\"")
        void mapConstructor_emptyMap_messageIsEmptyBraces() {
            Map<String, String> emptyErrors = new HashMap<>();

            CommentException ex = new CommentException(emptyErrors);

            assertThat(ex.getMessage()).isEqualTo("{}");
            assertThat(ex.getErrors()).isSameAs(emptyErrors);
        }

        @Test
        @DisplayName("single-entry map produces correct toString message")
        void mapConstructor_singleEntryMap() {
            Map<String, String> errors = Map.of("field", "required");

            CommentException ex = new CommentException(errors);

            assertThat(ex.getMessage()).isEqualTo(errors.toString());
            assertThat(ex.getErrors()).containsEntry("field", "required");
        }
    }

    // =========================================================================
    // 5. Lombok-generated getters and setters (correctness verification)
    //    These are @lombok.Generated → excluded from JaCoCo measurement,
    //    but still exercised here to verify the fields behave correctly.
    // =========================================================================

    @Nested
    @DisplayName("Getters and Setters (Lombok-generated, correctness check)")
    class GetterSetterTests {

        private CommentException ex;

        @BeforeEach
        void setUp() {
            ex = new CommentException();
        }

        @Test
        @DisplayName("setCode / getCode round-trip")
        void setCode_getCode_roundTrip() {
            ex.setCode("ERR_CODE");
            assertThat(ex.getCode()).isEqualTo("ERR_CODE");
        }

        @Test
        @DisplayName("setMessage / getMessage round-trip")
        void setMessage_getMessage_roundTrip() {
            ex.setMessage("Custom message");
            assertThat(ex.getMessage()).isEqualTo("Custom message");
        }

        @Test
        @DisplayName("setHttpStatusCode / getHttpStatusCode round-trip")
        void setHttpStatusCode_getHttpStatusCode_roundTrip() {
            ex.setHttpStatusCode(404);
            assertThat(ex.getHttpStatusCode()).isEqualTo(404);
        }

        @Test
        @DisplayName("setErrors / getErrors round-trip")
        void setErrors_getErrors_roundTrip() {
            Map<String, String> errors = Map.of("key", "value");
            ex.setErrors(errors);
            assertThat(ex.getErrors()).isSameAs(errors);
        }

        @Test
        @DisplayName("setters accept null for all fields")
        void setters_acceptNull() {
            ex.setCode(null);
            ex.setMessage(null);
            ex.setHttpStatusCode(null);
            ex.setErrors(null);

            assertThat(ex.getCode()).isNull();
            assertThat(ex.getMessage()).isNull();
            assertThat(ex.getHttpStatusCode()).isNull();
            assertThat(ex.getErrors()).isNull();
        }

        @Test
        @DisplayName("each setter writes only its own field without affecting others")
        void setters_doNotCrossPollinate() {
            ex.setCode("c1");
            assertThat(ex.getMessage()).isNull();
            assertThat(ex.getHttpStatusCode()).isNull();
            assertThat(ex.getErrors()).isNull();

            ex.setMessage("msg");
            assertThat(ex.getCode()).isEqualTo("c1");   // unchanged
            assertThat(ex.getHttpStatusCode()).isNull();

            ex.setHttpStatusCode(500);
            assertThat(ex.getCode()).isEqualTo("c1");
            assertThat(ex.getMessage()).isEqualTo("msg");
        }
    }
}

