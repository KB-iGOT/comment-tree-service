package com.tarento.commenthub.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("ApiResponse – 100% Coverage Tests")
class ApiResponseTest {

    @Nested
    @DisplayName("Constructors")
    class ConstructorTests {

        @Test
        @DisplayName("default constructor initialises ver='v1', non-null ts, non-null params, empty result map")
        void defaultConstructor_initialisesFields() {
            ApiResponse response = new ApiResponse();

            assertThat(response.getVer()).isEqualTo("v1");
            assertThat(response.getTs()).isNotNull();
            assertThat(response.getParams()).isNotNull();
            assertThat(response.getResult()).isNotNull().isEmpty();
            // id and responseCode are not set by the default constructor
            assertThat(response.getId()).isNull();
            assertThat(response.getResponseCode()).isNull();
        }

        @Test
        @DisplayName("parameterised constructor sets id and delegates to default constructor")
        void parameterisedConstructor_setsId() {
            ApiResponse response = new ApiResponse("api-id-001");

            // id must be set
            assertThat(response.getId()).isEqualTo("api-id-001");
            // everything else must still be initialised (delegates to this())
            assertThat(response.getVer()).isEqualTo("v1");
            assertThat(response.getTs()).isNotNull();
            assertThat(response.getParams()).isNotNull();
            assertThat(response.getResult()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("each default constructor call produces a unique params.msgId (random UUID)")
        void defaultConstructor_uniqueParamsMsgIdPerInstance() {
            ApiResponse r1 = new ApiResponse();
            ApiResponse r2 = new ApiResponse();

            // Both must be non-null; msgIds are randomly generated → they must differ
            assertThat(r1.getParams().getMsgId()).isNotNull();
            assertThat(r2.getParams().getMsgId()).isNotNull();
            assertThat(r1.getParams().getMsgId()).isNotEqualTo(r2.getParams().getMsgId());
        }
    }

    @Nested
    @DisplayName("id getter and setter")
    class IdTests {

        @Test
        @DisplayName("setId / getId round-trip")
        void setId_getId_roundTrip() {
            ApiResponse response = new ApiResponse();
            response.setId("my-id");
            assertThat(response.getId()).isEqualTo("my-id");
        }

        @Test
        @DisplayName("setId accepts null")
        void setId_null_accepted() {
            ApiResponse response = new ApiResponse("initial-id");
            response.setId(null);
            assertThat(response.getId()).isNull();
        }
    }

    @Nested
    @DisplayName("ver getter and setter")
    class VerTests {

        @Test
        @DisplayName("setVer / getVer round-trip overrides the default 'v1'")
        void setVer_getVer_roundTrip() {
            ApiResponse response = new ApiResponse();
            response.setVer("v2");
            assertThat(response.getVer()).isEqualTo("v2");
        }
    }

    @Nested
    @DisplayName("ts getter and setter")
    class TsTests {

        @Test
        @DisplayName("setTs / getTs round-trip overrides the timestamp set by the constructor")
        void setTs_getTs_roundTrip() {
            ApiResponse response = new ApiResponse();
            response.setTs("2024-01-01 00:00:00.000");
            assertThat(response.getTs()).isEqualTo("2024-01-01 00:00:00.000");
        }
    }


    @Nested
    @DisplayName("params getter and setter")
    class ParamsTests {

        @Test
        @DisplayName("setParams / getParams round-trip")
        void setParams_getParams_roundTrip() {
            ApiResponse response = new ApiResponse();
            ApiRespParam params = new ApiRespParam("new-msg-id");
            response.setParams(params);
            assertThat(response.getParams()).isSameAs(params);
        }

        @Test
        @DisplayName("setParams null clears the params field")
        void setParams_null() {
            ApiResponse response = new ApiResponse();
            response.setParams(null);
            assertThat(response.getParams()).isNull();
        }
    }

    @Nested
    @DisplayName("responseCode getter and setter")
    class ResponseCodeTests {

        @Test
        @DisplayName("setResponseCode / getResponseCode round-trip")
        void setResponseCode_getResponseCode_roundTrip() {
            ApiResponse response = new ApiResponse();
            response.setResponseCode(HttpStatus.CREATED);
            assertThat(response.getResponseCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    @Nested
    @DisplayName("result map operations")
    class ResultMapTests {

        private ApiResponse response;

        @BeforeEach
        void setUp() {
            response = new ApiResponse();
        }

        @Test
        @DisplayName("getResult returns an empty, non-null map on a fresh instance")
        void getResult_freshInstance_isEmpty() {
            assertThat(response.getResult()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("put adds an entry; get retrieves it; containsKey returns true")
        void put_get_containsKey_happyPath() {
            response.put("key1", "value1");

            assertThat(response.get("key1")).isEqualTo("value1");
            assertThat(response.containsKey("key1")).isTrue();
        }

        @Test
        @DisplayName("put with null key and null value stores null without throwing")
        void put_nullKey_nullValue_storesNull() {
            response.put(null, null);

            // HashMap permits null keys and null values
            assertThat(response.containsKey(null)).isTrue();
            assertThat(response.get(null)).isNull();
        }

        @Test
        @DisplayName("put overwrites an existing entry under the same key")
        void put_overwritesExistingKey() {
            response.put("key", "original");
            response.put("key", "overwritten");

            assertThat(response.get("key")).isEqualTo("overwritten");
        }

        @Test
        @DisplayName("get for a key that was never put returns null")
        void get_missingKey_returnsNull() {
            assertThat(response.get("nonExistentKey")).isNull();
        }

        @Test
        @DisplayName("containsKey returns false for a key that was never put")
        void containsKey_missingKey_returnsFalse() {
            assertThat(response.containsKey("nonExistentKey")).isFalse();
        }

        @Test
        @DisplayName("putAll merges all entries from the supplied map")
        void putAll_mergesEntries() {
            response.put("existing", "data");

            Map<String, Object> extra = new HashMap<>();
            extra.put("newKey1", 42);
            extra.put("newKey2", true);
            response.putAll(extra);

            assertThat(response.getResult())
                    .containsEntry("existing", "data")
                    .containsEntry("newKey1", 42)
                    .containsEntry("newKey2", true)
                    .hasSize(3);
        }

        @Test
        @DisplayName("putAll with an empty map leaves the existing result map unchanged")
        void putAll_emptyMap_noChange() {
            response.put("pre", "existing");
            response.putAll(new HashMap<>());

            assertThat(response.getResult())
                    .hasSize(1)
                    .containsEntry("pre", "existing");
        }

        @Test
        @DisplayName("setResult replaces the internal map entirely")
        void setResult_replacesMap() {
            response.put("old", "value");

            Map<String, Object> newMap = new HashMap<>();
            newMap.put("fresh", "entry");
            response.setResult(newMap);

            assertThat(response.getResult())
                    .containsEntry("fresh", "entry")
                    .doesNotContainKey("old");
        }

        @Test
        @DisplayName("setResult with null makes getResult return null")
        void setResult_null_getResultReturnsNull() {
            response.setResult(null);
            assertThat(response.getResult()).isNull();
        }

        @Test
        @DisplayName("put stores various value types (Integer, Boolean, List, nested Map)")
        void put_supportsVariousValueTypes() {
            response.put("intVal", 100);
            response.put("boolVal", Boolean.FALSE);
            response.put("nestedMap", Map.of("a", "b"));

            assertThat(response.get("intVal")).isEqualTo(100);
            assertThat(response.get("boolVal")).isEqualTo(Boolean.FALSE);
            assertThat(response.get("nestedMap")).isInstanceOf(Map.class);
        }
    }
}

