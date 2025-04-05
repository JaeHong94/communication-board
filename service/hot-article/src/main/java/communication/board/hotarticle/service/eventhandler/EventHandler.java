package communication.board.hotarticle.service.eventhandler;

import communication.board.common.event.Event;
import communication.board.common.event.EventPayload;

public interface EventHandler<T extends EventPayload> {
    void handle(Event<T> event);
    boolean support(Event<T> event);
    Long findArticleId(Event<T> event);
}
