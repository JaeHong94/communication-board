package communication.board.comment.controller;

import communication.board.comment.repository.ArticleCommentCountRepository;
import communication.board.comment.service.CommentV2Service;
import communication.board.comment.service.request.CommentV2CreateRequest;
import communication.board.comment.service.response.CommentPageResponse;
import communication.board.comment.service.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentV2Controller {
    private final CommentV2Service commentV2Service;
    private final ArticleCommentCountRepository articleCommentCountRepository;

    @GetMapping("/v2/comments/{commentId}")
    public CommentResponse read(@PathVariable("commentId") Long commentId) {
        return commentV2Service.read(commentId);
    }

    @GetMapping("/v2/comments")
    public CommentPageResponse readAll(
            @RequestParam("articleId") Long articleId,
            @RequestParam("page") Long page,
            @RequestParam("pageSize") Long pageSize
    ) {
        return commentV2Service.readAll(articleId, page, pageSize);
    }

    @GetMapping("/v2/comments/infinite-scroll")
    public List<CommentResponse> readAllInfiniteScroll(
            @RequestParam("articleId") Long articleId,
            @RequestParam(value = "lastPath", required = false) String lastPath,
            @RequestParam("pageSize") Long pageSize
    ) {
        return commentV2Service.readAllInfiniteScroll(articleId, lastPath, pageSize);
    }

    @GetMapping("/v2/comments/articles/{articleId}/count")
    public Long count(@PathVariable("articleId") Long articleId) {
        return commentV2Service.count(articleId);
    }

    @PostMapping("/v2/comments")
    public CommentResponse create(@RequestBody CommentV2CreateRequest request) {
        return commentV2Service.create(request);
    }

    @DeleteMapping("/v2/comments/{commentId}")
    public void delete(@PathVariable("commentId") Long commentId) {
        commentV2Service.delete(commentId);
    }
}
