package com.study.concurrency.pessimistic;

import com.study.concurrency.pessimistic.entity.PPost;
import com.study.concurrency.pessimistic.repository.PPostRepository;
import com.study.concurrency.pessimistic.service.PPostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootTest
public class PessimisticTest {
  @Autowired
  private PPostService pPostService;

  @Autowired
  private PPostRepository postRepository;

  @BeforeEach
  void beforeEach() {
    PPost post = PPost.builder()
        .title("Pessimistic Lock")
        .content("Init Content")
        .build();
    postRepository.save(post);
  }


  /**
   * Pessimistic Lock 비관적인 Lock
   * - 트랜잭션 충돌이 발생할 것이라 예상하고 조회시 미리 데이터에 대한 LOCK을 점유하는 방법
   * - 다른 트랜잭션들의 지연(WAIT)을 유발
   *
   * select query 뒤에 for update가 붙음
   *  - 동시성 제어를 위해 특정 row에 배타적 LOCK을 거는 행위 = 데이터 수정할거니 다른 곳은 건들지 말라는 소리
   *
   * Lock LockModeType
   *  - PESSIMISTIC_WRITE
   *    일반적인 옵션. 데이터베이스에 쓰기 락
   *    다른 트랜잭션에서 읽기도 쓰기도 못함.
   *
   *  - PESSIMISTIC_READ
   *    반복 읽기만하고 수정하지 않는 용도로 락을 걸 때 사용
   *    다른 트랜잭션에서 읽기는 가능함
   *
   *  - PESSINISTIC_FORCE_INCREMENT
   *    Version 정보를 사용하는 비관적 락
   */

  @Test
  @DisplayName("Pessimistic Lock")
  void test() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(100);

    ExecutorService service = Executors.newFixedThreadPool(10);

    for (int i = 0; i < 100; i++) {
      service.execute(() -> {
        try {
          pPostService.updateContent(1L, "update " + Thread.currentThread().getName());
        } catch (Exception e) {
          log.error("thread sleep error", e);
        }
        countDownLatch.countDown();
      });
    }
    countDownLatch.await();
  }
}
