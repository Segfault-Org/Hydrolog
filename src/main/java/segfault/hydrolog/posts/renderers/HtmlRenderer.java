package segfault.hydrolog.posts.renderers;

import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import java.io.Reader;
import java.io.Writer;

public class HtmlRenderer implements IRenderer {
    @Override
    public void render(@Nonnull Reader in, @Nonnull Writer out) throws Exception {
        IOUtils.copyLarge(in, out);
    }
}
