package communication.board.comment.service.request;

import lombok.Getter;

@Getter
public class CommentV2CreateRequest {
    private Long articleId;
    private String content;
    private String parentPath;
    private Long writerId;
}
