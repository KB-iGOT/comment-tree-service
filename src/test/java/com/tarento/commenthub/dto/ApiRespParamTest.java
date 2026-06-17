package com.tarento.commenthub.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("ApiRespParam – 100% Coverage Tests")
class ApiRespParamTest {


    @Nested
    @DisplayName("Constructors")
    class ConstructorTests {

        @Test
        @DisplayName("default constructor creates instance with all null fields")
        void defaultConstructor_allFieldsNull() {
            ApiRespParam param = new ApiRespParam();

            assertThat(param.getResMsgId()).isNull();
            assertThat(param.getMsgId()).isNull();
            assertThat(param.getErr()).isNull();
            assertThat(param.getStatus()).isNull();
            assertThat(param.getErrMsg()).isNull();
        }

        @Test
        @DisplayName("parameterised constructor sets resMsgId and msgId to the supplied id")
        void parameterisedConstructor_setsBothIds() {
            ApiRespParam param = new ApiRespParam("test-id-123");

            assertThat(param.getResMsgId()).isEqualTo("test-id-123");
            assertThat(param.getMsgId()).isEqualTo("test-id-123");

            // Fields not set by the constructor must remain null
            assertThat(param.getErr()).isNull();
            assertThat(param.getStatus()).isNull();
            assertThat(param.getErrMsg()).isNull();
        }

        @Test
        @DisplayName("parameterised constructor with null id sets both ids to null")
        void parameterisedConstructor_nullId_setBothIdsNull() {
            ApiRespParam param = new ApiRespParam(null);

            assertThat(param.getResMsgId()).isNull();
            assertThat(param.getMsgId()).isNull();
        }
    }


    @Nested
    @DisplayName("resMsgId getter and setter")
    class ResMsgIdTests {

        @Test
        @DisplayName("setResMsgId / getResMsgId round-trip")
        void setResMsgId_getResMsgId_roundTrip() {
            ApiRespParam param = new ApiRespParam();
            param.setResMsgId("res-001");
            assertThat(param.getResMsgId()).isEqualTo("res-001");
        }

        @Test
        @DisplayName("setResMsgId accepts null")
        void setResMsgId_null() {
            ApiRespParam param = new ApiRespParam("initial");
            param.setResMsgId(null);
            assertThat(param.getResMsgId()).isNull();
        }
    }

    @Nested
    @DisplayName("msgId getter and setter")
    class MsgIdTests {

        @Test
        @DisplayName("setMsgId / getMsgId round-trip")
        void setMsgId_getMsgId_roundTrip() {
            ApiRespParam param = new ApiRespParam();
            param.setMsgId("msg-001");
            assertThat(param.getMsgId()).isEqualTo("msg-001");
        }

        @Test
        @DisplayName("setMsgId accepts null")
        void setMsgId_null() {
            ApiRespParam param = new ApiRespParam("initial");
            param.setMsgId(null);
            assertThat(param.getMsgId()).isNull();
        }
    }


    @Nested
    @DisplayName("err getter and setter")
    class ErrTests {

        @Test
        @DisplayName("setErr / getErr round-trip")
        void setErr_getErr_roundTrip() {
            ApiRespParam param = new ApiRespParam();
            param.setErr("ERR_NOT_FOUND");
            assertThat(param.getErr()).isEqualTo("ERR_NOT_FOUND");
        }

        @Test
        @DisplayName("setErr accepts null")
        void setErr_null() {
            ApiRespParam param = new ApiRespParam();
            param.setErr(null);
            assertThat(param.getErr()).isNull();
        }
    }


    // 5. status getter / setter
    @Nested
    @DisplayName("status getter and setter")
    class StatusTests {

        @Test
        @DisplayName("setStatus / getStatus round-trip")
        void setStatus_getStatus_roundTrip() {
            ApiRespParam param = new ApiRespParam();
            param.setStatus("successful");
            assertThat(param.getStatus()).isEqualTo("successful");
        }

        @Test
        @DisplayName("setStatus with 'failed' value is stored as-is")
        void setStatus_failedValue() {
            ApiRespParam param = new ApiRespParam();
            param.setStatus("failed");
            assertThat(param.getStatus()).isEqualTo("failed");
        }
    }

    // 6. errMsg getter / setter
    @Nested
    @DisplayName("errMsg getter and setter")
    class ErrMsgTests {

        @Test
        @DisplayName("setErrMsg / getErrMsg round-trip")
        void setErrMsg_getErrMsg_roundTrip() {
            ApiRespParam param = new ApiRespParam();
            param.setErrMsg("Resource not found");
            assertThat(param.getErrMsg()).isEqualTo("Resource not found");
        }

        @Test
        @DisplayName("setErrMsg accepts null")
        void setErrMsg_null() {
            ApiRespParam param = new ApiRespParam();
            param.setErrMsg(null);
            assertThat(param.getErrMsg()).isNull();
        }
    }

    // 7. Independence: setters do not cross-pollinate fields
    @Test
    @DisplayName("each setter writes only its own field; unset fields remain null")
    void setters_doNotCrossPollinate() {
        ApiRespParam param = new ApiRespParam();

        param.setResMsgId("R");
        assertThat(param.getMsgId()).isNull();
        assertThat(param.getErr()).isNull();
        assertThat(param.getStatus()).isNull();
        assertThat(param.getErrMsg()).isNull();

        param.setMsgId("M");
        assertThat(param.getResMsgId()).isEqualTo("R"); // previous field unchanged
        assertThat(param.getErr()).isNull();

        param.setErr("E");
        param.setStatus("S");
        param.setErrMsg("EM");

        assertThat(param.getResMsgId()).isEqualTo("R");
        assertThat(param.getMsgId()).isEqualTo("M");
        assertThat(param.getErr()).isEqualTo("E");
        assertThat(param.getStatus()).isEqualTo("S");
        assertThat(param.getErrMsg()).isEqualTo("EM");
    }
}

