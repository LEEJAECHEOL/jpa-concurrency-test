package com.study.concurrency.optimistic.repository;

import com.study.concurrency.optimistic.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@SpringBootTest
class PostRepositoryTest {

  @Autowired
  private PostRepository postRepository;

  @BeforeEach
  void beforeEach() {
    Post post = Post.builder()
        .title("Optimistic Lock")
        .content("Init Content")
        .version(0)
        .build();
    postRepository.save(post);
  }


  /**
   * Optimistic Lock (낙관적인 LOCK)
   * - 트랜잭션 충돌이 발생하지 않을 것이라고 낙관적으로 생각하는 Locking 방법
   * - 트랜잭션 충돌에 대한 감지는 조회한 데이터의 version 값을 통해 이루어짐.
   *
   * ObjectOptimisticLockingFailureException 발생
   */
  @Test
  @DisplayName("Optimistic Lock")
  void test() throws Exception {
    Post post = postRepository.findByTitle("Optimistic Lock");

    CountDownLatch countDownLatch = new CountDownLatch(10);
    AtomicInteger i = new AtomicInteger(1);
    List<Thread> threads = Stream
        .generate(() -> new Thread(new UpdatePost(post.getId(), countDownLatch, i.getAndIncrement())))
        .limit(10)
        .collect(Collectors.toList());

    threads.forEach(Thread::start);
    countDownLatch.await();

  }



  private class UpdatePost implements Runnable {
    private Long id;
    private CountDownLatch countDownLatch;
    private Integer integer;

    public UpdatePost(Long id, CountDownLatch countDownLatch, Integer integer) {
      this.id = id;
      this.countDownLatch = countDownLatch;
      this.integer = integer;
    }

    @Override
    public void run() {
      Post post = null;
      try {
        Thread.sleep(1000 * (integer % 3));
        post = postRepository.findById(id).get();
        post.setContent("update " +  Thread.currentThread().getName());
        postRepository.save(post);

      } catch (OptimisticLockingFailureException optEx) {
        log.error("updated by " + Thread.currentThread().getName(), optEx);
      } catch (Exception e) {
        log.error("thread sleep error", e);
      }
      countDownLatch.countDown();
    }
  }


}