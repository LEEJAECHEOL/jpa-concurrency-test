package com.study.concurrency.pessimistic.repository;

import com.study.concurrency.pessimistic.entity.PPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;

public interface PPostRepository extends JpaRepository<PPost, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  PPost findPessimisticById(Long id);
}
