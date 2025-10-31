package com.tarento.commenthub.controller;

import com.tarento.commenthub.constant.Constants;
import com.tarento.commenthub.dto.ApiResponse;
import com.tarento.commenthub.dto.CommentTreeIdentifierDTO;
import com.tarento.commenthub.service.CommentTreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("commentTree/v1")
@Slf4j
public class CommentTreeController {

  @Autowired
  CommentTreeService commentTreeService;

  @GetMapping("/health")
  public String healthCheck() {
    return Constants.SUCCESS_STRING;
  }

  @PostMapping("/get")
  public ResponseEntity<?> search(@RequestBody CommentTreeIdentifierDTO commentTreeIdentifierDTO) {
    ApiResponse response = commentTreeService.getCommentTree(commentTreeIdentifierDTO);
    if (response.getResponseCode().equals(HttpStatus.NOT_FOUND) && response.getResult().isEmpty()) {
      return new ResponseEntity<>(response, HttpStatus.OK);
    }
    return new ResponseEntity<>(response, response.getResponseCode());
  }

}
