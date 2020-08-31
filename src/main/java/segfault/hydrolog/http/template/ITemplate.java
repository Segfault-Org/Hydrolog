package segfault.hydrolog.http.template;

import segfault.hydrolog.posts.IPost;
import segfault.hydrolog.posts.IPostService;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface ITemplate {
    void renderList(@Nonnull List<IPost> posts, @Nonnull IPostService service, @Nonnull OutputStream stream) throws Exception;
    void renderPost(@Nonnull IPost post, @Nonnull IPostService service, @Nonnull OutputStream stream) throws Exception;
}
