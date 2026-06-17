package com.tarento.commenthub.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tarento.commenthub.constant.Constants;
import com.tarento.commenthub.dto.ApiResponse;
import com.tarento.commenthub.dto.CommentTreeIdentifierDTO;
import com.tarento.commenthub.entity.CommentTree;
import com.tarento.commenthub.exception.CommentException;
import com.tarento.commenthub.repository.CommentTreeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("CommentTreeServiceImpl – 100% Coverage Tests")
@SuppressWarnings({"rawtypes", "unchecked"})
class CommentTreeServiceImplTest {

    // ── mocked dependencies ──────────────────────────────────────────────────
    @Mock
    private CommentTreeRepository commentTreeRepository;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private ValueOperations valueOperations;

    /**
     * Real ObjectMapper wrapped in a spy so tests get genuine JSON processing by
     * default and can selectively stub individual calls (e.g., to throw
     * {@link JsonProcessingException}) without affecting other tests.
     */
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CommentTreeServiceImpl service;

    @BeforeEach
    void injectValueAnnotationFields() {
        // Simulate @Value injected by Spring
        ReflectionTestUtils.setField(service, "jwtSecretKey", "test-secret-key-for-unit-tests");
        ReflectionTestUtils.setField(service, "redisTtl", 86400L);
    }

    //  findTargetNode  (static — called directly)
    @Nested
    @DisplayName("findTargetNode() — all branch paths")
    class FindTargetNodeTests {

        @Test
        @DisplayName("B1=true: index >= path.length → return currentNode immediately")
        void b1_indexAtEnd_returnsCurrentNode() {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            // index = length → base case
            JsonNode result = CommentTreeServiceImpl.findTargetNode(node, new String[]{"a", "b"}, 2);
            assertThat(result).isSameAs(node);
        }

        @Test
        @DisplayName("B1=false, B2=false: currentNode is NOT an array → returns null")
        void b2_notArray_returnsNull() {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put(Constants.COMMENT_ID, "c1");
            // isArray() = false
            JsonNode result = CommentTreeServiceImpl.findTargetNode(node, new String[]{"c1"}, 0);
            assertThat(result).isNull();
        }


        @Test
        @DisplayName("B3=false: array with a non-object (string) child → isObject=false → returns null")
        void b3_nonObjectChild_returnsNull() {
            ArrayNode array = JsonNodeFactory.instance.arrayNode();
            array.add("plain-string-not-an-object"); // TextNode → isObject()=false
            JsonNode result = CommentTreeServiceImpl.findTargetNode(array, new String[]{"target"}, 0);
            assertThat(result).isNull();
        }


        @Test
        @DisplayName("B4=false: object child with non-matching commentId → returns null")
        void b4_idMismatch_returnsNull() {
            ArrayNode array = JsonNodeFactory.instance.arrayNode();
            ObjectNode child = JsonNodeFactory.instance.objectNode();
            child.put(Constants.COMMENT_ID, "other-id");
            array.add(child);
            JsonNode result = CommentTreeServiceImpl.findTargetNode(array, new String[]{"target-id"}, 0);
            assertThat(result).isNull();
        }


        @Test
        @DisplayName("B5=false (no children): first-if fails, else-if true → recurse returns matching node")
        void b5_matchingNodeWithoutChildren_elseIfBranch() {
            ArrayNode array = JsonNodeFactory.instance.arrayNode();
            ObjectNode child = JsonNodeFactory.instance.objectNode();
            child.put(Constants.COMMENT_ID, "target-id");
            array.add(child);

            JsonNode result = CommentTreeServiceImpl.findTargetNode(array, new String[]{"target-id"}, 0);
            assertThat(result).isSameAs(child);
        }

        @Test
        @DisplayName("B5=true (has children): first-if taken → recurse returns children array")
        void b5_matchingNodeWithChildren_firstIfBranch() {
            ArrayNode array = JsonNodeFactory.instance.arrayNode();
            ObjectNode child = JsonNodeFactory.instance.objectNode();
            child.put(Constants.COMMENT_ID, "target-id");
            ArrayNode children = JsonNodeFactory.instance.arrayNode(); // non-null children
            child.set(Constants.CHILDREN, children);
            // first-if: isObject=true, equals=true, get(CHILDREN)!=null → true
            // recurse(children, path, 1) → index=1 >= length=1 → return children
            array.add(child);

            JsonNode result = CommentTreeServiceImpl.findTargetNode(array, new String[]{"target-id"}, 0);
            assertThat(result).isSameAs(children);
        }


        @Test
        @DisplayName("two-level path: correctly navigates through parent's children to reach target")
        void multiLevel_returnsDeepNode() {
            ArrayNode root = JsonNodeFactory.instance.arrayNode();

            ObjectNode l1 = JsonNodeFactory.instance.objectNode();
            l1.put(Constants.COMMENT_ID, "L1");
            ArrayNode l1Children = JsonNodeFactory.instance.arrayNode();

            ObjectNode l2 = JsonNodeFactory.instance.objectNode();
            l2.put(Constants.COMMENT_ID, "L2");
            l1Children.add(l2);
            l1.set(Constants.CHILDREN, l1Children);
            root.add(l1);

            JsonNode result = CommentTreeServiceImpl.findTargetNode(root, new String[]{"L1", "L2"}, 0);
            assertThat(result).isSameAs(l2);
        }

        // ── else-if B6=false explicitly (array contains a non-object child ONLY) ─
        @Test
        @DisplayName("else-if B6=false: non-object child skipped by isObject check in else-if")
        void elseIf_b6_nonObjectSkipped_returnsNull() {
            // Child is an integer node → isObject()=false for both if AND else-if → null
            ArrayNode array = JsonNodeFactory.instance.arrayNode();
            array.add(42); // IntNode
            JsonNode result = CommentTreeServiceImpl.findTargetNode(array, new String[]{"42"}, 0);
            assertThat(result).isNull();
        }
    }

    // =========================================================================
    // 2. getCommentTreeById()   — B8 true/false
    // =========================================================================

    @Nested
    @DisplayName("getCommentTreeById()")
    class GetCommentTreeByIdTests {

        @Test
        @DisplayName("B8=true: repository returns present → returns the entity")
        void b8_present_returnsCommentTree() {
            CommentTree tree = buildTree("tree-001");
            when(commentTreeRepository.findById("tree-001")).thenReturn(Optional.of(tree));

            CommentTree result = service.getCommentTreeById("tree-001");

            assertThat(result).isSameAs(tree);
        }

        @Test
        @DisplayName("B8=false: repository returns empty → throws CommentException")
        void b8_empty_throwsCommentException() {
            when(commentTreeRepository.findById("missing")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getCommentTreeById("missing")).isInstanceOf(CommentException.class).hasMessage("Comment Tree is not found");
        }
    }

    @Nested
    @DisplayName("getCommentTree()")
    class GetCommentTreeTests {

        private final CommentTreeIdentifierDTO validDto = new CommentTreeIdentifierDTO("course", "entity-001", "review");

        // ── Redis hit ─────
        @Test
        @DisplayName("TC1 — Redis hit: deserialises JSON from cache and returns immediately")
        void tc1_redisHit_returnsFromCache() {
            // Valid JSON that objectMapper (spy, real) can deserialise to a non-empty Map
            String cachedJson = "{\"entityId\":\"entity-001\",\"count\":5}";
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(anyString())).thenReturn(cachedJson);

            ApiResponse response = service.getCommentTree(validDto);

            assertThat(response.getResponseCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getResult()).containsKey("entityId");
            // DB must NOT be consulted when cache hits
            verify(commentTreeRepository, org.mockito.Mockito.never()).findById(anyString());
        }

        // ──  Redis miss + DB hit ────────────────────────────────────────────
        @Test
        @DisplayName("TC2 — Redis miss + DB hit: fetches from DB, stores serialised JSON in Redis")
        void tc2_redisMiss_dbHit_storesInRedisAndReturns() {
            ObjectNode data = JsonNodeFactory.instance.objectNode();
            data.put("entityId", "entity-001");
            data.put("entityType", "course");
            CommentTree tree = buildTree("any-tree-id");
            tree.setCommentTreeData(data);

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(anyString())).thenReturn(null);       // cache miss
            when(commentTreeRepository.findById(anyString())).thenReturn(Optional.of(tree));
            // valueOperations.set() is void — mock does nothing by default

            ApiResponse response = service.getCommentTree(validDto);

            assertThat(response.getResponseCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getResult()).containsKey("entityId");
            // Verify Redis store was invoked with correct TTL
            verify(valueOperations).set(anyString(), anyString(), eq(86400L), eq(TimeUnit.SECONDS));
        }

        // ──  Redis miss + DB miss ───────────────────────────────────────────
        @Test
        @DisplayName("TC3 — Redis miss + DB miss: response has 'message' = 'Comment Tree not found'")
        void tc3_redisMiss_dbMiss_notFoundMessage() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(anyString())).thenReturn(null);
            when(commentTreeRepository.findById(anyString())).thenReturn(Optional.empty());

            ApiResponse response = service.getCommentTree(validDto);

            assertThat(response.getResponseCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.get("message")).isEqualTo("Comment Tree not found");
        }

        // ──  JsonProcessingException inside inner try → RuntimeException → outer catch ──
        @Test
        @DisplayName("TC4 — JsonProcessingException in Redis deserialisation → outer catch → HTTP 500")
        void tc4_jsonProcessingException_outerCatch_returns500() throws Exception {
            // Redis returns a non-null string so readValue IS called
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(anyString())).thenReturn("{\"any\":\"value\"}");

            // Force the spy to throw when readValue(String, TypeReference) is called
            doThrow(new JsonProcessingException("simulated parse failure") {
            }).when(objectMapper).readValue(anyString(), any(TypeReference.class));

            ApiResponse response = service.getCommentTree(validDto);

            assertThat(response.getResponseCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.get("error")).isEqualTo("An error occurred while fetching the Comment Tree");
            assertThat(response.get("details")).isEqualTo("Failed to deserialize JSON");
        }

        // ──  generateJwtTokenKey throws (blank field) → outer catch ─────────
        @Test
        @DisplayName("TC5 — blank entityId → CommentException from generateJwtTokenKey → outer catch → HTTP 500")
        void tc5_blankEntityId_generateJwtThrows_outerCatch_returns500() {
            // Blank entityId causes generateJwtTokenKey to throw BEFORE any Redis access
            CommentTreeIdentifierDTO blankDto = new CommentTreeIdentifierDTO("course", "", "review");

            ApiResponse response = service.getCommentTree(blankDto);

            assertThat(response.getResponseCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.get("error")).isEqualTo("An error occurred while fetching the Comment Tree");
        }

        // ── null entityType in DTO → same outer-catch path ─────────────────
        @Test
        @DisplayName("TC6 — null entityType → outer catch → HTTP 500")
        void tc6_nullEntityType_outerCatch_returns500() {
            CommentTreeIdentifierDTO nullDto = new CommentTreeIdentifierDTO(null, "entity-001", "review");

            ApiResponse response = service.getCommentTree(nullDto);

            assertThat(response.getResponseCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =========================================================================
    // 4. generateJwtTokenKey()   — B14 true/false
    // =========================================================================

    @Nested
    @DisplayName("generateJwtTokenKey()")
    class GenerateJwtTokenKeyTests {

        // ── B14 true (any blank field) ────────────────────────────────────────
        @Test
        @DisplayName("B14=true: blank entityId → throws CommentException with mandatory-fields message")
        void b14_blankEntityId_throwsCommentException() {
            CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO("course", " ", "review");

            assertThatThrownBy(() -> service.generateJwtTokenKey(dto)).isInstanceOf(CommentException.class).hasMessageContaining("entityType");
        }

        @Test
        @DisplayName("B14=true: null entityType → throws CommentException")
        void b14_nullEntityType_throwsCommentException() {
            CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO(null, "entity-001", "review");

            assertThatThrownBy(() -> service.generateJwtTokenKey(dto)).isInstanceOf(CommentException.class);
        }

        @Test
        @DisplayName("B14=true: empty workflow → throws CommentException")
        void b14_emptyWorkflow_throwsCommentException() {
            CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO("course", "entity-001", "");

            assertThatThrownBy(() -> service.generateJwtTokenKey(dto)).isInstanceOf(CommentException.class);
        }

        // ── B14 false (all fields present) ───────────────────────────────────
        @Test
        @DisplayName("B14=false: all fields non-blank → returns a valid JWT string with three parts")
        void b14_allFieldsPresent_returnsJwtToken() {
            CommentTreeIdentifierDTO dto = new CommentTreeIdentifierDTO("course", "entity-001", "review");

            String token = service.generateJwtTokenKey(dto);

            // A compact JWT always has exactly two dots separating header.payload.signature
            assertThat(token).isNotBlank();
            assertThat(token.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("same DTO always produces the same deterministic token (HMAC is deterministic)")
        void allFieldsPresent_deterministicToken() {
            CommentTreeIdentifierDTO dto1 = new CommentTreeIdentifierDTO("course", "e1", "wf");
            CommentTreeIdentifierDTO dto2 = new CommentTreeIdentifierDTO("course", "e1", "wf");

            assertThat(service.generateJwtTokenKey(dto1)).isEqualTo(service.generateJwtTokenKey(dto2));
        }

        @Test
        @DisplayName("different DTOs produce different tokens")
        void differentDtos_produceDifferentTokens() {
            CommentTreeIdentifierDTO dto1 = new CommentTreeIdentifierDTO("course", "e1", "wf1");
            CommentTreeIdentifierDTO dto2 = new CommentTreeIdentifierDTO("course", "e1", "wf2");

            assertThat(service.generateJwtTokenKey(dto1)).isNotEqualTo(service.generateJwtTokenKey(dto2));
        }
    }

    // =========================================================================
    // 5. getCommentTreeIdentifierDTO()   — no branches, pure extraction
    // =========================================================================

    @Nested
    @DisplayName("getCommentTreeIdentifierDTO()")
    class GetCommentTreeIdentifierDTOTests {

        @Test
        @DisplayName("extracts entityType, entityId, workflow from a JsonNode and returns a populated DTO")
        void validJsonNode_returnsCorrectDto() {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put(Constants.ENTITY_TYPE, "course");
            node.put(Constants.ENTITY_ID, "entity-001");
            node.put(Constants.WORKFLOW, "review");

            CommentTreeIdentifierDTO dto = service.getCommentTreeIdentifierDTO(node);

            assertThat(dto.getEntityType()).isEqualTo("course");
            assertThat(dto.getEntityId()).isEqualTo("entity-001");
            assertThat(dto.getWorkflow()).isEqualTo("review");
        }

        @Test
        @DisplayName("null-value fields in JsonNode produce 'null' strings in the DTO (asText() behaviour)")
        void nullValueFields_producesNullStringInDto() {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.putNull(Constants.ENTITY_TYPE);
            node.put(Constants.ENTITY_ID, "e1");
            node.put(Constants.WORKFLOW, "wf");

            CommentTreeIdentifierDTO dto = service.getCommentTreeIdentifierDTO(node);

            assertThat(dto.getEntityType()).isEqualTo("null"); // NullNode.asText() = "null"
        }
    }

    private static CommentTree buildTree(String id) {
        CommentTree tree = new CommentTree();
        tree.setCommentTreeId(id);
        tree.setStatus("active");
        return tree;
    }
}

