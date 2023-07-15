import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Post implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String postId;
    private final String content;

    private final List<Comment> comments;

    public Post(String postId, String content) {
        this.postId = postId;
        this.content = content;
        this.comments = new ArrayList<>();
    }

    public String getPostId() {
        return postId;
    }

    public String getContent() {
        return content;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        getComments().add(comment);
    }

    @Override
    public String toString() {
        return "uuid: " + getPostId() + "\n" + getContent();
    }

}
