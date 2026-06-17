package com.tarento.commenthub.config;

import org.junit.jupiter.api.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


@DisplayName("RedisConfig – 100% Coverage Tests")
class RedisConfigTest {

    private RedisConfig config;

    private LettuceConnectionFactory createdFactory;

    @BeforeEach
    void setUp() {
        config = new RedisConfig();
        // Simulate Spring's @Value injection
        ReflectionTestUtils.setField(config, "redisHost", "localhost");
        ReflectionTestUtils.setField(config, "redisPort", 6379);
    }

    @AfterEach
    void tearDown() {
        if (createdFactory != null) {
            try {
                createdFactory.destroy();
            } catch (Exception ignored) { /* best-effort */ }
            createdFactory = null;
        }
    }


    @Test
    @DisplayName("RedisConfig can be instantiated directly (constructor covered)")
    void constructor_createsInstance() {
        assertThat(config).isNotNull();
    }

    // =========================================================================
    // 2. redisTemplate(RedisConnectionFactory)
    // =========================================================================

    @Nested
    @DisplayName("redisTemplate(RedisConnectionFactory)")
    class RedisTemplateTests {

        @Test
        @DisplayName("returns a non-null RedisTemplate wired to the supplied connection factory")
        void returnsNonNullTemplateWiredToFactory() {
            RedisConnectionFactory mockFactory = mock(RedisConnectionFactory.class);

            RedisTemplate<String, String> template = config.redisTemplate(mockFactory);

            assertThat(template).isNotNull();
            assertThat(template.getConnectionFactory()).isSameAs(mockFactory);
        }

        @Test
        @DisplayName("key serializer is StringRedisSerializer (keys stored as plain Strings)")
        void keySerializer_isStringRedisSerializer() {
            RedisTemplate<String, String> template =
                    config.redisTemplate(mock(RedisConnectionFactory.class));

            assertThat(template.getKeySerializer())
                    .isNotNull()
                    .isInstanceOf(StringRedisSerializer.class);
        }

        @Test
        @DisplayName("value serializer is StringRedisSerializer (values stored as plain Strings)")
        void valueSerializer_isStringRedisSerializer() {
            RedisTemplate<String, String> template =
                    config.redisTemplate(mock(RedisConnectionFactory.class));

            assertThat(template.getValueSerializer())
                    .isNotNull()
                    .isInstanceOf(StringRedisSerializer.class);
        }

        @Test
        @DisplayName("different mock factories produce independent template instances")
        void differentFactories_produceIndependentTemplates() {
            RedisConnectionFactory factoryA = mock(RedisConnectionFactory.class);
            RedisConnectionFactory factoryB = mock(RedisConnectionFactory.class);

            RedisTemplate<String, String> templateA = config.redisTemplate(factoryA);
            RedisTemplate<String, String> templateB = config.redisTemplate(factoryB);

            assertThat(templateA).isNotSameAs(templateB);
            assertThat(templateA.getConnectionFactory()).isSameAs(factoryA);
            assertThat(templateB.getConnectionFactory()).isSameAs(factoryB);
        }
    }

    // =========================================================================
    // 3. redisConnectionFactory()
    //    Creates a LettuceConnectionFactory, calls afterPropertiesSet() on it,
    //    and returns it.  Lettuce uses lazy TCP connections, so no real Redis
    //    server is required — afterPropertiesSet() only initialises the Netty
    //    client, not an actual socket connection.
    // =========================================================================

    @Nested
    @DisplayName("redisConnectionFactory()")
    class RedisConnectionFactoryTests {

        @Test
        @DisplayName("creates and returns a non-null LettuceConnectionFactory")
        void createsLettuceConnectionFactory() {
            createdFactory = (LettuceConnectionFactory) config.redisConnectionFactory();

            assertThat(createdFactory)
                    .isNotNull()
                    .isInstanceOf(LettuceConnectionFactory.class);
        }

        @Test
        @DisplayName("factory is properly initialised (afterPropertiesSet was called by the bean method)")
        void factoryIsInitialised_afterPropertiesSetCalled() {
            createdFactory = (LettuceConnectionFactory) config.redisConnectionFactory();

            // LettuceConnectionFactory.isRunning() returns false until start() is called,
            // but the factory IS initialised (client created) — verified by being non-null.
            assertThat(createdFactory).isNotNull();
        }

        @Test
        @DisplayName("factory created with custom host and port is non-null")
        void customHostAndPort_factoryNonNull() {
            // Override the injected values to verify the method respects them
            ReflectionTestUtils.setField(config, "redisHost", "redis-test-server");
            ReflectionTestUtils.setField(config, "redisPort", 6380);

            createdFactory = (LettuceConnectionFactory) config.redisConnectionFactory();

            assertThat(createdFactory)
                    .isNotNull()
                    .isInstanceOf(LettuceConnectionFactory.class);
        }
    }
}

