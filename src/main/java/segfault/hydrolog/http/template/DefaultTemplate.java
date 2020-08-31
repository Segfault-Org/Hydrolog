package segfault.hydrolog.http.template;

import segfault.hydrolog.posts.IPost;
import segfault.hydrolog.posts.IPostService;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public class DefaultTemplate implements ITemplate {
    private final String lang;
    private final String title;
    private final String footer;

    // Copied from postfix.org.
    private static final String CSS = "" +
            "BODY {\n" +
            "    margin-left: 10px;\n" +
            "    margin-right: 10px;\n" +
            "    }\n" +
            "#left {\n" +
            "    border: 0px;\n" +
            "    float: left;\n" +
            "    margin-left: 10px;\n" +
            "    width: 140px;\n" +
            "    }\n" +
            "#main {\n" +
            "    border: 0px;\n" +
            "    font-weight: normal; \n" +
            "    margin-left: 180px;\n" +
            "    }\n" +
            ".nav { \n" +
            "    font-size: small;\n" +
            "    line-height: 45%;\n" +
            "    text-decoration: none; \n" +
            "    white-space: nowrap; \n" +
            "    } \n" +
            ".navhead { \n" +
            "    font-size: medium;\n" +
            "    line-height: 90%;\n" +
            "    padding-left: 0px;\n" +
            "    text-decoration: none; \n" +
            "    }\n" +
            ".footer {\n" +
            "    left: 0;\n" +
            "    bottom: 0;\n" +
            "    width: 100%;\n" +
            "    text-align: center;\n" +
            "}";

    public DefaultTemplate() {
        this.lang = System.getenv("html.default.lang");
        this.title = System.getenv("html.default.title");
        this.footer = System.getenv("html.default.footer");
    }

    @Override
    public void renderList(@Nonnull List<IPost> posts, @Nonnull IPostService service, @Nonnull OutputStream stream) throws Exception {
        renderBase(title, out -> {
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            append(writer, "<dl>");
            for (final IPost post : posts) {
                append(writer, "<dt><a href=\"");
                append(writer, post.path() + "\">");
                append(writer, post.title());
                append(writer, "</a>");
                // TODO: i18n
                append(writer, " by ");
                append(writer, post.author());
                append(writer, ", ");
                append(writer, new Date(post.created()).toString());
                append(writer, "</dt>");
                if (post.descr() != null) {
                    append(writer, "<dd>");
                    writer.flush();
                    service.renderDescr(post, out);
                    append(writer, "</dd>");
                }
            }
            append(writer, "</dl>");

        }, stream);
    }

    @Override
    public void renderPost(@Nonnull IPost post, @Nonnull IPostService service, @Nonnull OutputStream stream) throws Exception {
        renderBase(String.format("%s - %s", post.title(), title), out -> {
            out.write("<h1>".getBytes(StandardCharsets.UTF_8));
            out.write(post.title().toString().getBytes(StandardCharsets.UTF_8));
            out.write("</h1>".getBytes(StandardCharsets.UTF_8));
            service.render(post, out);
        }, stream);
    }

    private void renderBase(@Nonnull CharSequence title, @Nonnull IOnRenderBodyCallback callback, @Nonnull OutputStream out) throws Exception {
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        append(writer, "<!DOCTYPE html>" +
                "<html lang=\"" + lang + "\"><head><meta charset=\"utf-8\" /><title>");
        append(writer, title);
        append(writer, "</title>");
        append(writer, "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        append(writer, "<style type=\"text/css\">");
        append(writer, CSS);
        append(writer, "</style>");
        append(writer, "</head><body>");
        append(writer, "<div id=\"left\">");
        append(writer, "<p class=\"nav\"><a href=\"/\">Home</a></p>");
        append(writer, "<p class=\"navhead\"></p>");
        append(writer, "</div>");
        append(writer, "<div id=\"main\">");
        writer.flush();
        callback.onRenderBody(out);
        append(writer, "</div>");

        append(writer, "<div class=\"footer\">");
        if (footer != null) {
            append(writer, "<p>");
            append(writer, footer);
            append(writer, "</p>");
        }
        append(writer, "</div>");

        append(writer, "</body></html>");
        writer.close();
    }

    private void append(@Nonnull Appendable appendable, @Nonnull CharSequence charSequence) throws IOException {
        appendable.append(charSequence);
    }

    private interface IOnRenderBodyCallback {
        void onRenderBody(@Nonnull OutputStream out) throws Exception;
    }
}
