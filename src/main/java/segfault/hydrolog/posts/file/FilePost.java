package segfault.hydrolog.posts.file;

import com.google.auto.value.AutoValue;
import segfault.hydrolog.posts.IPost;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

@AutoValue
abstract class FilePost implements IPost {
    @Nonnull
    public static FilePost create(long id, @Nonnull CharSequence title, long created, long modified, @Nonnull String path, @Nullable String descr, @Nonnull String author,
                                  @Nonnull File file) {
        return new AutoValue_FilePost(id, title, created, modified, path, descr, author, file);
    }

    @Nonnull
    public abstract File file();
}
