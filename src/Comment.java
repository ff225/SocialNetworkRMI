import java.io.Serial;
import java.io.Serializable;

public class Comment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String author;
    private final String content;

    public Comment(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return getAuthor() + " dice: " + getContent();
    }
}
