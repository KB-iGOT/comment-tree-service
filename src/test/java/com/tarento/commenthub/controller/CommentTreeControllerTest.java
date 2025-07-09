package com.tarento.commenthub.controller;

import com.tarento.commenthub.constant.Constants;
import com.tarento.commenthub.dto.ApiResponse;
import com.tarento.commenthub.dto.CommentTreeIdentifierDTO;
import com.tarento.commenthub.service.CommentTreeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

class CommentTreeControllerTest {

    @Mock
    private CommentTreeService commentTreeService;

    @InjectMocks
    private CommentTreeController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHealthCheck() {
        String result = controller.healthCheck();
        assertEquals(Constants.SUCCESS_STRING, result);
    }

    @Test
    void testSearch_whenResponseNotFoundAndResultEmpty() {
        CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResponseCode(HttpStatus.NOT_FOUND);
        apiResponse.setResult(Collections.emptyMap());

        when(commentTreeService.getCommentTree(dto)).thenReturn(apiResponse);

        ResponseEntity<?> response = controller.search(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(apiResponse, response.getBody());
        verify(commentTreeService, times(1)).getCommentTree(dto);
    }

    @Test
    void testSearch_whenResponseIsSomethingElse() {
        CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResponseCode(HttpStatus.OK);
        apiResponse.setResult(Collections.singletonMap("key", "value"));

        when(commentTreeService.getCommentTree(dto)).thenReturn(apiResponse);

        ResponseEntity<?> response = controller.search(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(apiResponse, response.getBody());
        verify(commentTreeService, times(1)).getCommentTree(dto);
    }
}
