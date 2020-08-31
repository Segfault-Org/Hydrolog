package segfault.hydrolog.http.template;

import segfault.hydrolog.posts.IPost;
import segfault.hydrolog.posts.IPostService;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.util.Date;

@SuppressWarnings("unused")
public final class StaticRenderingUtils {
    private StaticRenderingUtils() {}

    public static CharSequence renderDescr(@Nonnull IPost post, @Nonnull IPostService service) throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.renderDescr(post, out);
        final String str = out.toString();
        out.close();
        return str;
    }

    public static CharSequence renderPost(@Nonnull IPost post, @Nonnull IPostService service) throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.render(post, out);
        final String str = out.toString();
        out.close();
        return str;
    }

    public static String renderDate(long date) {
        return new Date(date).toString();
    }
}
