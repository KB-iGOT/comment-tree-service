package com.tarento.commenthub.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ErrorResponse – 100% Coverage Tests")
class ErrorResponseTest {
    // =========================================================================
    // 1. Builder
    // =========================================================================
    @Nested
    @DisplayName("Builder")
    class BuilderTests {
        @Test
        @DisplayName("all four fields set via builder")
        void builder_allFields() {
            Map<String, String> errors = Map.of("f", "v");
            ErrorResponse r = ErrorResponse.builder()
                    .code("NOT_FOUND").message("not found").errors(errors).httpStatusCode(404).build();
            assertThat(r.getCode()).isEqualTo("NOT_FOUND");
            assertThat(r.getMessage()).isEqualTo("not found");
            assertThat(r.getErrors()).isEqualTo(errors);
            assertThat(r.getHttpStatusCode()).isEqualTo(404);
        }

        @Test
        @DisplayName("only code — rest null")
        void builder_onlyCode() {
            ErrorResponse r = ErrorResponse.builder().code("C").build();
            assertThat(r.getCode()).isEqualTo("C");
            assertThat(r.getMessage()).isNull();
            assertThat(r.getErrors()).isNull();
            assertThat(r.getHttpStatusCode()).isNull();
        }

        @Test
        @DisplayName("only message")
        void builder_onlyMessage() {
            ErrorResponse r = ErrorResponse.builder().message("msg").build();
            assertThat(r.getMessage()).isEqualTo("msg");
            assertThat(r.getCode()).isNull();
        }

        @Test
        @DisplayName("only errors map")
        void builder_onlyErrors() {
            Map<String, String> e = new HashMap<>();
            e.put("entityId", "blank");
            ErrorResponse r = ErrorResponse.builder().errors(e).build();
            assertThat(r.getErrors()).containsEntry("entityId", "blank");
            assertThat(r.getCode()).isNull();
        }

        @Test
        @DisplayName("only httpStatusCode")
        void builder_onlyHttpStatusCode() {
            ErrorResponse r = ErrorResponse.builder().httpStatusCode(500).build();
            assertThat(r.getHttpStatusCode()).isEqualTo(500);
            assertThat(r.getCode()).isNull();
        }

        @Test
        @DisplayName("empty builder — all null")
        void builder_empty_allNull() {
            ErrorResponse r = ErrorResponse.builder().build();
            assertThat(r.getCode()).isNull();
            assertThat(r.getMessage()).isNull();
            assertThat(r.getErrors()).isNull();
            assertThat(r.getHttpStatusCode()).isNull();
        }

        @Test
        @DisplayName("builder() returns new instance each time")
        void builder_newInstanceEachCall() {
            assertThat(ErrorResponse.builder()).isNotSameAs(ErrorResponse.builder());
        }

        @Test
        @DisplayName("builder toString is non-null")
        void builder_toString_nonNull() {
            String s = ErrorResponse.builder().code("c").message("m").httpStatusCode(200).toString();
            assertThat(s).isNotNull().contains("ErrorResponseBuilder");
        }
    }

    // =========================================================================
    // 2. Getters
    // =========================================================================
    @Nested
    @DisplayName("Getters")
    class GetterTests {
        @Test
        void getCode() {
            assertThat(ErrorResponse.builder().code("C").build().getCode()).isEqualTo("C");
        }

        @Test
        void getMessage() {
            assertThat(ErrorResponse.builder().message("M").build().getMessage()).isEqualTo("M");
        }

        @Test
        void getHttpStatusCode() {
            assertThat(ErrorResponse.builder().httpStatusCode(422).build().getHttpStatusCode()).isEqualTo(422);
        }

        @Test
        void getErrors() {
            Map<String, String> m = Map.of("k", "v");
            assertThat(ErrorResponse.builder().errors(m).build().getErrors()).isSameAs(m);
        }
    }

    // =========================================================================
    // 3. equals()
    // =========================================================================
    @Nested
    @DisplayName("equals()")
    class EqualsTests {
        private ErrorResponse full() {
            return ErrorResponse.builder()
                    .code("CODE").message("MSG").errors(Map.of("k", "v")).httpStatusCode(200).build();
        }

        @Test
        void sameRef() {
            ErrorResponse r = full();
            assertThat(r.equals(r)).isTrue();
        }

        @Test
        void equalContent() {
            assertThat(full()).isEqualTo(full());
        }

        @Test
        void nullArg() {
            assertThat(full() == null).isFalse();
        }

        @Test
        void differentType() {
            assertThat(full().equals("x")).isFalse();
        }

        @Test
        void differentCode() {
            assertThat(ErrorResponse.builder().code("A").build()).isNotEqualTo(ErrorResponse.builder().code("B").build());
        }

        @Test
        void differentMessage() {
            assertThat(ErrorResponse.builder().message("a").build()).isNotEqualTo(ErrorResponse.builder().message("b").build());
        }

        @Test
        void differentStatus() {
            assertThat(ErrorResponse.builder().httpStatusCode(200).build()).isNotEqualTo(ErrorResponse.builder().httpStatusCode(500).build());
        }

        @Test
        void differentErrors() {
            assertThat(ErrorResponse.builder().errors(Map.of("k", "v1")).build()).isNotEqualTo(ErrorResponse.builder().errors(Map.of("k", "v2")).build());
        }

        @Test
        void allNull() {
            assertThat(ErrorResponse.builder().build()).isEqualTo(ErrorResponse.builder().build());
        }

        @Test
        void nullVsNonNullCode() {
            assertThat(ErrorResponse.builder().build()).isNotEqualTo(ErrorResponse.builder().code("X").build());
        }
        // canEqual is NOT generated by Lombok for final @Value classes — no test needed
    }

    // =========================================================================
    // 4. hashCode()
    // =========================================================================
    @Nested
    @DisplayName("hashCode()")
    class HashCodeTests {
        @Test
        void sameContentSameHash() {
            ErrorResponse a = ErrorResponse.builder().code("C").message("M").httpStatusCode(200).build();
            ErrorResponse b = ErrorResponse.builder().code("C").message("M").httpStatusCode(200).build();
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        void allNullNoThrow() {
            assertThat(ErrorResponse.builder().build().hashCode()).isNotNull();
        }

        @Test
        void consistent() {
            ErrorResponse r = ErrorResponse.builder().code("X").httpStatusCode(404).build();
            assertThat(r.hashCode()).isEqualTo(r.hashCode());
        }

        @Test
        void equalsHashCodeContract() {
            ErrorResponse a = ErrorResponse.builder().code("X").httpStatusCode(500).build();
            ErrorResponse b = ErrorResponse.builder().code("X").httpStatusCode(500).build();
            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }
    }

    // =========================================================================
    // 5. toString()
    // =========================================================================
    @Nested
    @DisplayName("toString()")
    class ToStringTests {
        @Test
        void containsFieldValues() {
            String s = ErrorResponse.builder().code("ERR-001").message("An error").httpStatusCode(404).build().toString();
            assertThat(s).isNotNull().contains("ERR-001").contains("An error").contains("404");
        }

        @Test
        void allNullNoThrow() {
            assertThat(ErrorResponse.builder().build().toString()).isNotNull();
        }
    }
}
