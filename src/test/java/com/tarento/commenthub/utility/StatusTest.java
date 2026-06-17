package com.tarento.commenthub.utility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DisplayName("Status enum – 100% Coverage Tests")
class StatusTest {

    // =========================================================================
    // 1. Enum constants – identity and position
    // =========================================================================

    @Nested
    @DisplayName("Enum constants")
    class ConstantTests {

        @Test
        @DisplayName("ACTIVE exists, has ordinal 0 and name 'ACTIVE'")
        void active_hasCorrectOrdinalAndName() {
            assertThat(Status.ACTIVE).isNotNull();
            assertThat(Status.ACTIVE.ordinal()).isEqualTo(0);
            assertThat(Status.ACTIVE.name()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("INACTIVE exists, has ordinal 1 and name 'INACTIVE'")
        void inactive_hasCorrectOrdinalAndName() {
            assertThat(Status.INACTIVE).isNotNull();
            assertThat(Status.INACTIVE.ordinal()).isEqualTo(1);
            assertThat(Status.INACTIVE.name()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("ACTIVE and INACTIVE are distinct constants")
        void active_andInactive_areDistinct() {
            assertThat(Status.ACTIVE).isNotEqualTo(Status.INACTIVE);
        }
    }

    // =========================================================================
    // 2. values() – covers the synthetic static method
    // =========================================================================

    @Nested
    @DisplayName("values()")
    class ValuesTests {

        @Test
        @DisplayName("returns exactly two constants in declaration order: ACTIVE, INACTIVE")
        void values_returnsAllConstantsInOrder() {
            Status[] values = Status.values();
            assertThat(values)
                    .hasSize(2)
                    .containsExactly(Status.ACTIVE, Status.INACTIVE);
        }
    }

    // =========================================================================
    // 3. valueOf(String) – covers the synthetic static method
    // =========================================================================

    @Nested
    @DisplayName("valueOf(String)")
    class ValueOfTests {

        @Test
        @DisplayName("valueOf('ACTIVE') returns Status.ACTIVE")
        void valueOf_active_returnsActiveConstant() {
            assertThat(Status.valueOf("ACTIVE")).isSameAs(Status.ACTIVE);
        }

        @Test
        @DisplayName("valueOf('INACTIVE') returns Status.INACTIVE")
        void valueOf_inactive_returnsInactiveConstant() {
            assertThat(Status.valueOf("INACTIVE")).isSameAs(Status.INACTIVE);
        }

        @Test
        @DisplayName("valueOf with unknown name throws IllegalArgumentException")
        void valueOf_unknownName_throwsIllegalArgumentException() {
            assertThatThrownBy(() -> Status.valueOf("UNKNOWN"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}

