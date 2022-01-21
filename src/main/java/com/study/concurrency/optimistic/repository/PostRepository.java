package com.study.concurrency.optimistic.repository;

import com.study.concurrency.optimistic.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
  Post findByTitle(String title);
}
