package segfault.hydrolog.http.template;

import org.apache.commons.io.IOUtils;
import segfault.hydrolog.posts.IPost;
import segfault.hydrolog.posts.IPostService;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.Date;
import java.util.List;

public class DefaultTemplate implements ITemplate {
    private final String lang;
    private final String title;
    private final String footer;

    public DefaultTemplate() {
        this.lang = System.getenv("html.default.lang");
        this.title = System.getenv("html.default.title");
        this.footer = System.getenv("html.default.footer");
    }

    @Override
    public void renderList(@Nonnull List<IPost> posts, @Nonnull IPostService service, @Nonnull OutputStream stream) throws Exception {
        final ByteArrayOutputStream body = new ByteArrayOutputStream();
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(body));
        append(writer, "<dl>");
        for (final IPost post : posts) {
            append(writer, "<dt><a href=\"");
            append(writer, post.path() + "\">");
            append(writer, post.title());
            append(writer, "</a>");
            append(writer, ", by ");
            append(writer, post.author());
            append(writer, "</dt>");
            append(writer, "<dd>");
            final ByteArrayOutputStream descrOut = new ByteArrayOutputStream();
            writer.flush();
            service.renderDescr(post, descrOut);
            descrOut.writeTo(body);
            append(writer, "<br />");
            // TODO: i18n
            append(writer, "Created: ");
            append(writer, new Date(post.created()).toString());
            if (post.created() != post.modified()) {
                append(writer, ", modified: ");
                append(writer, new Date(post.modified()).toString());
            }
            append(writer, "</dd>");
        }
        append(writer, "</dl>");

        renderBase(title, body, stream);
    }

    @Override
    public void renderPost(@Nonnull IPost post, @Nonnull IPostService service, @Nonnull OutputStream stream) throws Exception {
        final ByteArrayOutputStream postOut = new ByteArrayOutputStream();
        service.render(post, postOut);
        renderBase(post.title(), postOut, stream);
    }

    private void renderBase(@Nonnull CharSequence title, @Nonnull ByteArrayOutputStream body, @Nonnull OutputStream out) throws IOException {
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        append(writer, "<!DOCTYPE html>" +
                "<html lang=\"" + lang + "\"><head><meta charset=\"utf-8\" /><title>");
        append(writer, title);
        append(writer, "</title>");
        append(writer, "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        append(writer, "</head><body>");
        writer.flush();
        body.writeTo(out);

        append(writer, "<footer>");
        if (footer != null) {
            append(writer, "<p>");
            append(writer, footer);
            append(writer, "</p>");
        }
        append(writer, "</footer>");

        append(writer, "</body></html>");
        writer.close();
        body.close();
    }

    private void append(@Nonnull Appendable appendable, @Nonnull CharSequence charSequence) throws IOException {
        appendable.append(charSequence);
    }
}
