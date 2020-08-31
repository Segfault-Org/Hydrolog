package segfault.hydrolog.posts.renderers;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public interface IRenderer {
    void render(@Nonnull Reader in, @Nonnull Writer out) throws Exception;
}
