package communication.board.comment.controller;

import communication.board.comment.service.CommentV2Service;
import communication.board.comment.service.request.CommentV2CreateRequest;
import communication.board.comment.service.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentV2Controller {
    private final CommentV2Service commentV2Service;

    @GetMapping("/v2/comments/{commentId}")
    public CommentResponse read(@PathVariable("commentId") Long commentId) {
        return commentV2Service.read(commentId);
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
