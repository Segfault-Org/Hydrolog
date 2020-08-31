package segfault.hydrolog.posts.renderers;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.annotation.Nonnull;
import java.io.*;

public class MarkdownRenderer implements IRenderer {
    @Override
    public void render(@Nonnull Reader in, @Nonnull Writer out) throws Exception {
        final Parser parser = Parser.builder().build();
        final Node document = parser.parseReader(in);
        final HtmlRenderer renderer = HtmlRenderer.builder().build();
        renderer.render(document, out);
    }
}
