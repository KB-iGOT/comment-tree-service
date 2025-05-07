package com.tarento.commenthub.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tarento.commenthub.constant.Constants;
import com.tarento.commenthub.dto.ApiResponse;
import com.tarento.commenthub.dto.CommentTreeIdentifierDTO;
import com.tarento.commenthub.entity.CommentTree;
import com.tarento.commenthub.exception.CommentException;
import com.tarento.commenthub.repository.CommentTreeRepository;
import com.tarento.commenthub.service.CommentTreeService;
import com.tarento.commenthub.utility.Status;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class CommentTreeServiceImpl implements CommentTreeService {

  @Value("${jwt.secret.key}")
  private String jwtSecretKey;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CommentTreeRepository commentTreeRepository;

  @Autowired
  private RedisTemplate redisTemplate;

  @Value("${redis.ttl}")
  private long redisTtl;



  public static JsonNode findTargetNode(JsonNode currentNode, String[] hierarchyPath, int index) {
    if (index >= hierarchyPath.length) {
      return currentNode;
    }

    String targetCommentId = hierarchyPath[index];
    if (currentNode.isArray()) {
      for (JsonNode childNode : currentNode) {
        if (childNode.isObject() && targetCommentId.equals(
            childNode.get(Constants.COMMENT_ID).asText())
            && childNode.get(Constants.CHILDREN) != null) {
          return findTargetNode(childNode.get(Constants.CHILDREN), hierarchyPath, index + 1);
        } else if (childNode.isObject() && targetCommentId.equals(
            childNode.get(Constants.COMMENT_ID).asText())) {
          return findTargetNode(childNode, hierarchyPath, index + 1);
        }
      }
    }
    return null;
  }

  @Override
  public CommentTree getCommentTreeById(String commentTreeId) {
    Optional<CommentTree> optionalCommentTree = commentTreeRepository.findById(commentTreeId);
    if (optionalCommentTree.isPresent()) {
      return optionalCommentTree.get();
    }
    throw new CommentException(
        Constants.ERROR, "Comment Tree is not found", HttpStatus.OK.value());
  }

  @Override
  public ApiResponse getCommentTree(CommentTreeIdentifierDTO commentTreeIdentifierDTO) {
    log.info(
        "CommentTreeService::getCommentTree:Fetching Comment Tree for entityId: {}, entityType: {}, workflow: {}",
        commentTreeIdentifierDTO.getEntityId(),
        commentTreeIdentifierDTO.getEntityType(),
        commentTreeIdentifierDTO.getWorkflow());
    ApiResponse response = new ApiResponse();
    try {
      response.setResponseCode(HttpStatus.OK);
      String commentTreeId = generateJwtTokenKey(commentTreeIdentifierDTO);
      // Check if the value is a Map or needs deserialization
      // Retrieve the value from Redis
      Map<String, Object> resultMap = (Map<String, Object>) redisTemplate.opsForValue()
          .get(Constants.COMMENT_TREE_REDIS_KEY + commentTreeId);
      if (resultMap != null) {
        log.info("CommentTreeService::getCommentTree:found in redis");
        response.setResult(resultMap);
        return response;
      }
      Optional<CommentTree> optionalCommentTree = commentTreeRepository.findById(commentTreeId);
      if (optionalCommentTree.isPresent()) {
        log.info("CommentTreeService::getCommentTree:fetching from postgre");
        resultMap = objectMapper.convertValue(
            optionalCommentTree.get().getCommentTreeData(), Map.class);
        response.setResult(resultMap);
        redisTemplate.opsForValue()
            .set(Constants.COMMENT_TREE_REDIS_KEY + commentTreeId, resultMap, redisTtl,
                TimeUnit.SECONDS);
        return response;
      }
      log.info("CommentTreeService::getCommentTree:not found");
      response.put("message", "Comment Tree not found");
    } catch (Exception e) {
      log.error("Error while fetching Comment Tree: {}", e);
      response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR);
      response.put("error", "An error occurred while fetching the Comment Tree");
      response.put("details", e.getMessage());
    }
    return response;
  }


  public String generateJwtTokenKey(CommentTreeIdentifierDTO commentTreeIdentifierDTO) {
    log.info("generating JwtTokenKey");

    if (StringUtils.isAnyBlank(
        commentTreeIdentifierDTO.getEntityId(),
        commentTreeIdentifierDTO.getEntityType(),
        commentTreeIdentifierDTO.getWorkflow())) {
      throw new CommentException(Constants.ERROR,
          "Please provide values for 'entityType', 'entityId', and 'workflow' as all of these fields are mandatory.");
    }

    String jwtToken = JWT.create()
        .withClaim(Constants.ENTITY_ID, commentTreeIdentifierDTO.getEntityId())
        .withClaim(Constants.ENTITY_TYPE, commentTreeIdentifierDTO.getEntityType())
        .withClaim(Constants.WORKFLOW, commentTreeIdentifierDTO.getWorkflow())
        .sign(Algorithm.HMAC256(jwtSecretKey));

    log.info("commentTreeId: {}", jwtToken);
    return jwtToken;
  }


  public CommentTreeIdentifierDTO getCommentTreeIdentifierDTO(JsonNode commentTreeData) {
    return new CommentTreeIdentifierDTO(
        commentTreeData.get(Constants.ENTITY_TYPE).asText(),
        commentTreeData.get(Constants.ENTITY_ID).asText(),
        commentTreeData.get(Constants.WORKFLOW).asText()
    );
  }



}
