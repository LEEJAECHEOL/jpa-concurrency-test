package com.study.concurrency.pessimistic.service;

import com.study.concurrency.pessimistic.entity.PPost;
import com.study.concurrency.pessimistic.repository.PPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PPostService {
  private final PPostRepository postRepository;

  @Transactional
  public void updateContent(Long id, String content) {
    PPost post = postRepository.findPessimisticById(id);
    post.setContent(content);
    postRepository.save(post);
    log.info("current content = {} ", post.getContent());
  }
}
