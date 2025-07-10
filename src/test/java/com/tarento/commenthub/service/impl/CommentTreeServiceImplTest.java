package com.tarento.commenthub.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tarento.commenthub.constant.Constants;
import com.tarento.commenthub.dto.ApiResponse;
import com.tarento.commenthub.dto.CommentTreeIdentifierDTO;
import com.tarento.commenthub.entity.CommentTree;
import com.tarento.commenthub.exception.CommentException;
import com.tarento.commenthub.repository.CommentTreeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentTreeServiceImplTest {

    @InjectMocks
    private CommentTreeServiceImpl service;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CommentTreeRepository repository;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        setField(service, "jwtSecretKey", "test-secret");
        setField(service, "redisTtl", 60L);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
    @Test
    void testGetCommentTreeById_found() {
        CommentTree tree = new CommentTree();
        when(repository.findById("id")).thenReturn(Optional.of(tree));

        CommentTree result = service.getCommentTreeById("id");
        assertEquals(tree, result);
    }

    @Test
    void testGetCommentTreeById_notFound() {
        when(repository.findById("id")).thenReturn(Optional.empty());
        CommentException ex = assertThrows(CommentException.class,
                () -> service.getCommentTreeById("id"));
        assertEquals("Comment Tree is not found", ex.getMessage());
    }

    @Test
    void testGenerateJwtTokenKey_valid() {
        CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO("type", "id", "wf");
        String jwt = service.generateJwtTokenKey(dto);
        assertNotNull(jwt);
    }

    @Test
    void testGenerateJwtTokenKey_invalid() {
        CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO(null, "id", "wf");
        assertThrows(CommentException.class, () -> service.generateJwtTokenKey(dto));
    }

    @Test
    void testGetCommentTreeIdentifierDTO() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put(Constants.ENTITY_TYPE, "type");
        node.put(Constants.ENTITY_ID, "id");
        node.put(Constants.WORKFLOW, "wf");

        CommentTreeIdentifierDTO dto = service.getCommentTreeIdentifierDTO(node);

        assertEquals("type", dto.getEntityType());
        assertEquals("id", dto.getEntityId());
        assertEquals("wf", dto.getWorkflow());
    }

    @Test
    void testFindTargetNode_foundChild() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree("""
            [
              {
                "commentId": "1",
                "children": [
                  { "commentId": "2" }
                ]
              }
            ]
            """);

        JsonNode result = CommentTreeServiceImpl.findTargetNode(tree, new String[]{"1", "2"}, 0);
        assertNotNull(result);
    }

    @Test
    void testFindTargetNode_notFound() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree("""
            [
              {
                "commentId": "1",
                "children": []
              }
            ]
            """);

        JsonNode result = CommentTreeServiceImpl.findTargetNode(tree, new String[]{"x"}, 0);
        assertNull(result);
    }

    @Test
    void testGetCommentTree_foundInRedis() throws Exception {
        CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO("type", "id", "wf");
        String jwtKey = service.generateJwtTokenKey(dto);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(Constants.COMMENT_TREE_REDIS_KEY + jwtKey)).thenReturn("{\"a\":\"b\"}");

        Map<String, Object> map = Map.of("a", "b");
        when(objectMapper.readValue(anyString(), any(Class.class))).thenReturn(map);

        ApiResponse response = service.getCommentTree(dto);

        assertEquals(HttpStatus.OK, response.getResponseCode());
    }

    @Test
    void testGetCommentTree_foundInDb() throws Exception {
        CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO("type", "id", "wf");
        String jwtKey = service.generateJwtTokenKey(dto);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(Constants.COMMENT_TREE_REDIS_KEY + jwtKey)).thenReturn(null);

        CommentTree tree = new CommentTree();
        tree.setCommentTreeData(mock(JsonNode.class));

        when(repository.findById(jwtKey)).thenReturn(Optional.of(tree));
        when(objectMapper.convertValue(any(), eq(Map.class))).thenReturn(Map.of("x", "y"));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"x\":\"y\"}");

        ApiResponse response = service.getCommentTree(dto);

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertTrue(response.getResult().containsKey("x"));
    }

    @Test
    void testGetCommentTree_notFound() {
        CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO("type", "id", "wf");
        String jwtKey = service.generateJwtTokenKey(dto);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(Constants.COMMENT_TREE_REDIS_KEY + jwtKey)).thenReturn(null);
        when(repository.findById(jwtKey)).thenReturn(Optional.empty());

        ApiResponse response = service.getCommentTree(dto);

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertTrue(response.getResult().containsKey("message"));
    }

    @Test
    void testGetCommentTree_jsonProcessingException() throws Exception {
        CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO("type", "id", "wf");

        String commentTreeId = service.generateJwtTokenKey(dto);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(Constants.COMMENT_TREE_REDIS_KEY + commentTreeId)).thenReturn("bad-json");

        // Mock Jackson to throw JsonProcessingException
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
                .thenThrow(new JsonProcessingException("Invalid JSON") {});

        ApiResponse response = service.getCommentTree(dto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getResponseCode());
        assertTrue(response.getResult().containsKey("error"));
        assertTrue(response.getResult().containsKey("details"));
    }

    @Test
    void testGetCommentTree_genericException() {
        CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO("type", "id", "wf");

        when(redisTemplate.opsForValue()).thenThrow(new RuntimeException("Redis error"));

        ApiResponse response = service.getCommentTree(dto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getResponseCode());
        assertTrue(response.getResult().containsKey("error"));
        assertTrue(response.getResult().containsKey("details"));
    }
}
