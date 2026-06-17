package com.tarento.commenthub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;


@DisplayName("CommentTreeApplication – 100% Coverage Tests")
class CommentTreeApplicationTest {

    // =========================================================================
    //  Constructor  (implicit Java no-args constructor)
    // =========================================================================

    @Test
    @DisplayName("CommentTreeApplication can be instantiated (covers implicit constructor)")
    void constructor_canBeInstantiated() {
        CommentTreeApplication app = new CommentTreeApplication();
        assertThat(app).isNotNull();
    }

    // =========================================================================
    // main(String[])
    //    Production code: SpringApplication.run(CommentTreeApplication.class, args)
    //    Strategy      : mock the static call so no real Spring context starts
    // =========================================================================

    @Nested
    @DisplayName("main(String[])")
    class MainMethodTests {

        @Test
        @DisplayName("calls SpringApplication.run() with CommentTreeApplication.class and the supplied args")
        void main_delegatesToSpringApplicationRun_withCorrectClassAndArgs() {
            try (MockedStatic<SpringApplication> mockedSpring = mockStatic(SpringApplication.class)) {

                // Stub the static call – production code discards the return value, so null is fine
                mockedSpring
                        .when(() -> SpringApplication.run(any(Class.class), any(String[].class)))
                        .thenReturn(null);

                String[] args = {};
                CommentTreeApplication.main(args);

                // Verify the exact overload and arguments that production code uses
                mockedSpring.verify(() ->
                        SpringApplication.run(CommentTreeApplication.class, args));
            }
        }

        @Test
        @DisplayName("passes non-empty args array through to SpringApplication.run()")
        void main_passesArgsToSpringApplicationRun() {
            try (MockedStatic<SpringApplication> mockedSpring = mockStatic(SpringApplication.class)) {

                mockedSpring
                        .when(() -> SpringApplication.run(any(Class.class), any(String[].class)))
                        .thenReturn(null);

                String[] args = {"--server.port=8099", "--spring.profiles.active=test"};
                CommentTreeApplication.main(args);

                // Verify those exact args were forwarded
                mockedSpring.verify(() ->
                        SpringApplication.run(CommentTreeApplication.class, args));
            }
        }
    }
}

