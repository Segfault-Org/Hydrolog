package segfault.hydrolog.posts;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.OutputStream;
import java.util.List;

public interface IPostService {
    @Nonnull
    List<IPost> list(@Nonnull ListArgs args) throws Exception;

    void render(@Nonnull IPost post, @Nonnull OutputStream out) throws Exception;

    void renderDescr(@Nonnull IPost post, @Nonnull OutputStream out) throws Exception;

    @Nullable
    IPost find(@Nonnull String path) throws Exception;

    abstract class ListArgs {}
}
