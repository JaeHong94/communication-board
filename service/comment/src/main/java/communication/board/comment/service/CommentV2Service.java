package communication.board.comment.service;

import communication.board.comment.entity.CommentPath;
import communication.board.comment.entity.CommentV2;
import communication.board.comment.repository.CommentV2Repository;
import communication.board.comment.service.request.CommentV2CreateRequest;
import communication.board.comment.service.response.CommentResponse;
import communication.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
public class CommentV2Service {
    private final Snowflake snowflake = new Snowflake();
    private final CommentV2Repository commentV2Repository;

    @Transactional
    public CommentResponse create(CommentV2CreateRequest request) {
        CommentV2 parent = findParent(request);
        CommentPath parentCommentPath = parent == null ? CommentPath.create("") : parent.getCommentPath();
        CommentV2 comment = commentV2Repository.save(
                CommentV2.create(
                        snowflake.nextId(),
                        request.getContent(),
                        request.getArticleId(),
                        request.getWriterId(),
                        parentCommentPath.createChildCommentPath(
                                commentV2Repository.findDescendantsTopPath(request.getArticleId(), parentCommentPath.getPath())
                                        .orElse(null)
                        )
                )
        );

        return CommentResponse.from(comment);
    }

    private CommentV2 findParent(CommentV2CreateRequest request) {
        String parentPath = request.getParentPath();
        if (parentPath == null) {
            return null;
        }
        return commentV2Repository.findByPath(parentPath)
                .filter(not(CommentV2::getDeleted))
                .orElseThrow();
    }

    public CommentResponse read(Long commentId) {
        return CommentResponse.from(
                commentV2Repository.findById(commentId).orElseThrow()
        );
    }

    @Transactional
    public void delete(Long commentId) {
        commentV2Repository.findById(commentId)
                .filter(not(CommentV2::getDeleted))
                .ifPresent(comment -> {
                    if (hasChildren(comment)) {
                        comment.delete();
                    } else {
                        delete(comment);
                    }
                });
    }

    private boolean hasChildren(CommentV2 comment) {
        return commentV2Repository.findDescendantsTopPath(
                comment.getArticleId(),
                comment.getCommentPath().getPath()
        ).isPresent();
    }

    private void delete(CommentV2 comment) {
        commentV2Repository.delete(comment);
        if (!comment.isRoot()) {
            commentV2Repository.findByPath(comment.getCommentPath().getParentPath())
                    .filter(CommentV2::getDeleted)
                    .filter(not(this::hasChildren))
                    .ifPresent(this::delete);
        }
    }
}
