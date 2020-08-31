package segfault.hydrolog.posts.renderers;

import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import java.io.Reader;
import java.io.Writer;

public class PlainRenderer implements IRenderer {
    @Override
    public void render(@Nonnull Reader in, @Nonnull Writer out) throws Exception {
        out.write("<p>");
        IOUtils.copyLarge(in, out);
        out.write("</p>");
    }
}
