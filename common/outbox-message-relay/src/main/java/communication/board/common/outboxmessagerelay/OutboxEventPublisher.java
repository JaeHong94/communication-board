package communication.board.common.outboxmessagerelay;

import communication.board.common.dataserializer.Snowflake;
import communication.board.common.event.Event;
import communication.board.common.event.EventPayload;
import communication.board.common.event.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {
    private final Snowflake outboxIdSnowflake = new  Snowflake();
    private final Snowflake eventIdSnowflake = new  Snowflake();
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(EventType type, EventPayload payload, Long shardKey) {
        // articleId = 10, shardKey == articleId
        // articleId % SHARD_COUNT = 물리적 shardKey
        Outbox outbox = Outbox.create(
                outboxIdSnowflake.nextId(),
                type,
                Event.of(eventIdSnowflake.nextId(), type, payload).toJson(),
                shardKey % MessageRelayConstants.SHARD_COUNT
        );
        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));
    }
}
