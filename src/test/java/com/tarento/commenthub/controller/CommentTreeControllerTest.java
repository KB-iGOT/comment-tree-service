package com.tarento.commenthub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarento.commenthub.constant.Constants;
import com.tarento.commenthub.dto.ApiResponse;
import com.tarento.commenthub.dto.CommentTreeIdentifierDTO;
import com.tarento.commenthub.service.CommentTreeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CommentTreeController.class)
@DisplayName("CommentTreeController – 100% Coverage Tests")
class CommentTreeControllerTest {


    /**
     * Auto-configured by @WebMvcTest; performs HTTP requests against the controller.
     */
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentTreeService commentTreeService;


    @Nested
    @DisplayName("GET /commentTree/v1/health")
    class HealthCheckTests {

        @Test
        @DisplayName("returns HTTP 200 with the exact literal string 'success'")
        void healthCheck_returns200WithSuccessBody() throws Exception {
            /*
             * The health-check method has no branches – a single test achieves
             * 100 % instruction, branch, and method coverage for that method.
             */
            mockMvc.perform(get("/commentTree/v1/health"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(Constants.SUCCESS_STRING));

            // Service must never be touched for a health check
            verify(commentTreeService, never())
                    .getCommentTree(any(CommentTreeIdentifierDTO.class));
        }
    }


    @Nested
    @DisplayName("POST /commentTree/v1/get")
    class SearchTests {

        /**
         * Fresh DTO recreated before every test to guarantee full isolation.
         */
        private CommentTreeIdentifierDTO validRequest;

        @BeforeEach
        void setUp() {
            validRequest = new CommentTreeIdentifierDTO("course", "entity-001", "review");
        }


        @Test
        @DisplayName("[Branch A] HTTP 200 when responseCode=NOT_FOUND and result map is empty (if-branch)")
        void search_notFoundWithEmptyResult_returnsHttp200Override() throws Exception {

            ApiResponse stubResponse = buildResponse(HttpStatus.NOT_FOUND);
            // result map left empty intentionally

            when(commentTreeService.getCommentTree(any(CommentTreeIdentifierDTO.class)))
                    .thenReturn(stubResponse);

            mockMvc.perform(post("/commentTree/v1/get")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk());
        }

        // ─────────────────────────────────────────────────────────────────────────
        // Branch B – ELSE-branch taken via short-circuit
        //   responseCode != NOT_FOUND  →  isEmpty() is NEVER evaluated
        //   → controller passes responseCode through unchanged
        // ─────────────────────────────────────────────────────────────────────────

        @Test
        @DisplayName("[Branch B] HTTP 200 from responseCode=OK via && short-circuit (else-branch)")
        void search_okResponseCode_shortCircuitsToElseBranch() throws Exception {
            ApiResponse stubResponse = buildResponse(HttpStatus.OK);

            when(commentTreeService.getCommentTree(any(CommentTreeIdentifierDTO.class)))
                    .thenReturn(stubResponse);

            mockMvc.perform(post("/commentTree/v1/get")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk());
        }

        // ─────────────────────────────────────────────────────────────────────────
        // Branch C – ELSE-branch taken because second sub-expression is FALSE
        //   responseCode == NOT_FOUND  BUT  result is NOT empty
        //   → overall condition is false → responseCode (404) passed through
        // ─────────────────────────────────────────────────────────────────────────

        @Test
        @DisplayName("[Branch C] HTTP 404 from responseCode when NOT_FOUND but result is non-empty (else-branch)")
        void search_notFoundWithNonEmptyResult_returnsHttp404FromResponseCode() throws Exception {

            ApiResponse stubResponse = buildResponse(HttpStatus.NOT_FOUND);
            stubResponse.put("data", "someValue");

            when(commentTreeService.getCommentTree(any(CommentTreeIdentifierDTO.class)))
                    .thenReturn(stubResponse);

            mockMvc.perform(post("/commentTree/v1/get")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isNotFound());
        }

        // ─────────────────────────────────────────────────────────────────────────
        // HTTP status delegation – arbitrary status codes pass through else-branch
        // ─────────────────────────────────────────────────────────────────────────

        @Test
        @DisplayName("HTTP 201 CREATED passes through unchanged via else-branch")
        void search_createdResponseCode_returns201() throws Exception {
            // responseCode=CREATED  → sub-expr-1 false → short-circuit → else-branch
            ApiResponse stubResponse = buildResponse(HttpStatus.CREATED);
            stubResponse.put("id", "new-comment-id");

            when(commentTreeService.getCommentTree(any(CommentTreeIdentifierDTO.class)))
                    .thenReturn(stubResponse);

            mockMvc.perform(post("/commentTree/v1/get")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("HTTP 500 INTERNAL_SERVER_ERROR passes through unchanged via else-branch")
        void search_internalServerError_returns500() throws Exception {
            // responseCode=INTERNAL_SERVER_ERROR → sub-expr-1 false → else-branch
            ApiResponse stubResponse = buildResponse(HttpStatus.INTERNAL_SERVER_ERROR);
            stubResponse.put("error", "An error occurred while fetching the Comment Tree");

            when(commentTreeService.getCommentTree(any(CommentTreeIdentifierDTO.class)))
                    .thenReturn(stubResponse);

            mockMvc.perform(post("/commentTree/v1/get")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isInternalServerError());
        }

        // ─────────────────────────────────────────────────────────────────────────
        // Service interaction: DTO is forwarded and result is surfaced in response
        // ─────────────────────────────────────────────────────────────────────────

        @Test
        @DisplayName("invokes service once with the parsed DTO and surfaces result keys in the response body")
        void search_forwardsDtoToServiceAndSurfacesResultInResponseBody() throws Exception {
            ApiResponse stubResponse = buildResponse(HttpStatus.OK);
            stubResponse.put("commentTreeData", "treePayload");

            when(commentTreeService.getCommentTree(any(CommentTreeIdentifierDTO.class)))
                    .thenReturn(stubResponse);

            MvcResult mvcResult = mockMvc.perform(post("/commentTree/v1/get")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            // Service must be called exactly once
            verify(commentTreeService).getCommentTree(any(CommentTreeIdentifierDTO.class));

            // The key placed into the ApiResponse result map must appear in serialised JSON
            String body = mvcResult.getResponse().getContentAsString();
            assertThat(body)
                    .as("Response body should contain the result key 'commentTreeData'")
                    .contains("commentTreeData");
        }

        @Test
        @DisplayName("response body contains all standard ApiResponse envelope fields")
        void search_responseBodyContainsApiResponseEnvelopeFields() throws Exception {
            ApiResponse stubResponse = buildResponse(HttpStatus.OK);
            stubResponse.put("message", "Comment Tree not found");

            when(commentTreeService.getCommentTree(any(CommentTreeIdentifierDTO.class)))
                    .thenReturn(stubResponse);

            MvcResult mvcResult = mockMvc.perform(post("/commentTree/v1/get")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String body = mvcResult.getResponse().getContentAsString();
            assertThat(body).contains("ver");       // ApiResponse.ver
            assertThat(body).contains("ts");        // ApiResponse.ts (timestamp)
            assertThat(body).contains("params");    // ApiResponse.params (ApiRespParam)
            assertThat(body).contains("message");   // key placed in result map
        }


        @Test
        @DisplayName("returns HTTP 500 when request body is missing (RestExceptionHandling catches the parse error)")
        void search_missingBody_returns500ViaCatchAllHandler() throws Exception {
            mockMvc.perform(post("/commentTree/v1/get")
                            .contentType(MediaType.APPLICATION_JSON))   // no .content(...)
                    .andExpect(status().isInternalServerError());

            // Service must NOT be called if body parsing already failed
            verify(commentTreeService, never())
                    .getCommentTree(any(CommentTreeIdentifierDTO.class));
        }

        @Test
        @DisplayName("returns HTTP 500 when request body is malformed JSON (RestExceptionHandling catches the parse error)")
        void search_malformedJson_returns500ViaCatchAllHandler() throws Exception {
            mockMvc.perform(post("/commentTree/v1/get")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{not-valid-json}"))
                    .andExpect(status().isInternalServerError());

            verify(commentTreeService, never())
                    .getCommentTree(any(CommentTreeIdentifierDTO.class));
        }
    }

    /**
     * Build a minimal {@link ApiResponse} stub with only {@code responseCode} set.
     * The result map is intentionally left <em>empty</em> unless the caller adds
     * entries afterwards (e.g. {@code stub.put("key", value)}).
     *
     * @param status the HTTP status to inject into the response
     * @return a fresh, minimally-populated {@code ApiResponse}
     */
    private static ApiResponse buildResponse(HttpStatus status) {
        ApiResponse response = new ApiResponse();
        response.setResponseCode(status);
        return response;
    }
}

