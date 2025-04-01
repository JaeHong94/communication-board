package communication.board.comment.api.data;

import communication.board.comment.entity.Comment;
import communication.board.common.dataserializer.Snowflake;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class DataInitializer {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    TransactionTemplate transactionTemplate;
    Snowflake snowflake = new Snowflake();
    CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);

    static final int BULK_INSERT_SIZE = 2000;
    static final int EXECUTE_COUNT = 6000;

    @Test
    void initialize() throws InterruptedException {
        // 10개의 스레드 생성
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < EXECUTE_COUNT; i++) {
            // 작업을 비동기로 데이터 삽입 작업
            executorService.submit(() ->{
                // 데이터 생성 및 저장
                insert();
                latch.countDown();
                // 남은 작업 수
                System.out.println("latch.getCount() = " + latch.getCount());
            });
        }
        // CountDownLatch가 0이 될때까지 대기
        latch.await();
        // 스레드풀 종료
        executorService.shutdown();
    }

    void insert() {
        transactionTemplate.executeWithoutResult(status -> {
            Comment prev = null;
            for (int i = 0; i < BULK_INSERT_SIZE; i++) {
                Comment comment = Comment.create(
                        snowflake.nextId(),
                        "content" + i,
                        i % 2 == 0 ? null : prev.getCommentId(),
                        1L,
                        1L
                );
                prev = comment;
                // 생성한 Article 데이터 저장하도록 요청
                entityManager.persist(comment);
            }
        });
    }
}
