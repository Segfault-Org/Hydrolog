package segfault.hydrolog.posts;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IPost {
    long id();

    @Nonnull
    CharSequence title();

    long created();

    long modified();

    @Nonnull
    String path();

    @Nullable
    String descr();

    @Nonnull
    String author();
}
