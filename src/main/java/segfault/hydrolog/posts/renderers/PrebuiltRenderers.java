package segfault.hydrolog.posts.renderers;

import javax.annotation.Nonnull;

public final class PrebuiltRenderers {
    @Nonnull
    public static IRenderer getRenderer(@Nonnull String extension) {
        switch (extension.toLowerCase()) {
            case "md":
                return new MarkdownRenderer();
            case "html":
                return new HtmlRenderer();
            default:
                return new PlainRenderer();
        }
    }
}
