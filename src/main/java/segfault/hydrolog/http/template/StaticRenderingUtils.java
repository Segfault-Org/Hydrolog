package segfault.hydrolog.http.template;

import segfault.hydrolog.nativelib.Uptime;
import segfault.hydrolog.posts.IPost;
import segfault.hydrolog.posts.IPostService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
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

    public static String renderDate(long date, @Nonnull String format) {
        return new SimpleDateFormat(format).format(new Date(date));
    }

    @Nullable
    public static String systemUptime() {
        try {
            final long sec = Uptime.uptime();
            final long hours = sec / 3600;
            final long min = (sec % 3600) / 60;
            final long fSec = sec % 60;

            return String.format("%02d:%02d:%02d", hours, min, fSec);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
