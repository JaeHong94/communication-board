package communication.board.common.snowflake;

import java.util.random.RandomGenerator;

public class Snowflake {
    /**
     * [1비트][41비트: 타임스탬프][10비트: 노드 ID][12비트: 시퀀스 번호
     * 분상 환경에서도 중복 없이 순차적 ID 생성하기 위한 규칙
     * 타임스탬프: 순차성
     * 노드ID + 시퀀스 번호: 고유성
     */
    private static final int UNUSED_BITS = 1;
    private static final int EPOCH = 41;
    private static final int NODE_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;

    private static final long maxNodeId = (1L << NODE_ID_BITS) - 1;
    private static final long maxSequence = (1L << SEQUENCE_BITS) - 1;

    private final long nodeId = RandomGenerator.getDefault().nextLong(maxNodeId + 1);

    private final long startTimeMillis = 1704067200000L;

    private long lastTimeMillis = startTimeMillis;
    private long sequence = 0L;

    public synchronized long nextId() {
        long currentMillis = System.currentTimeMillis();

        if (currentMillis < lastTimeMillis) {
            throw new IllegalStateException("Invalid Time");
        }

        if (currentMillis == lastTimeMillis) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                currentMillis = waitNextMillis(currentMillis);
            }
        } else {
            sequence = 0;
        }

        lastTimeMillis = currentMillis;

        return ((currentMillis - startTimeMillis) << (NODE_ID_BITS + SEQUENCE_BITS))
                | (nodeId << SEQUENCE_BITS)
                | sequence;
    }

    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp <= lastTimeMillis) {
            currentTimestamp = System.currentTimeMillis();
        }

        return currentTimestamp;
    }
}
