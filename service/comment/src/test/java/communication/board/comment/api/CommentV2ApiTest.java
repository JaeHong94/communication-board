package communication.board.comment.api;

import communication.board.comment.service.request.CommentV2CreateRequest;
import communication.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class CommentV2ApiTest {
    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void createTest() {
        CommentResponse response1 = create(new CommentV2CreateRequest(1L, "my comment1", null, 1L));
        CommentResponse response2 = create(new CommentV2CreateRequest(1L, "my comment2", response1.getPath(), 1L));
        CommentResponse response3 = create(new CommentV2CreateRequest(1L, "my comment3", response2.getPath(), 1L));

        System.out.println("response1.getCommentId() = " + response1.getPath());
        System.out.println("\tresponse2.getPath() = " + response2.getPath());
        System.out.println("\tresponse2.getCommentId() = " + response2.getCommentId());
        System.out.println("\t\tresponse3.getPath() = " + response3.getPath());
        System.out.println("\t\tresponse3.getCommentId() = " + response3.getCommentId());
    }

    CommentResponse create(CommentV2CreateRequest request) {
        return restClient.post()
                .uri("/v2/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void readTest() {
        CommentResponse response = restClient.get()
                .uri("/v2/comments/{commentId}", 160807627974057984L)
                .retrieve()
                .body(CommentResponse.class);
        System.out.println("response = " + response);
    }

    @Test
    void deleteTest() {
        restClient.delete()
                .uri("/v2/comments/{commentId}", 160807627974057984L)
                .retrieve()
                .toBodilessEntity();
    }

    @Getter
    @AllArgsConstructor
    public static class CommentV2CreateRequest {
        private Long articleId;
        private String content;
        private String parentPath;
        private Long writerId;
    }
}
