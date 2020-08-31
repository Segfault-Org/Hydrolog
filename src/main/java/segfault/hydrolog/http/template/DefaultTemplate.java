package segfault.hydrolog.http.template;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import segfault.hydrolog.posts.IPost;
import segfault.hydrolog.posts.IPostService;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

public class DefaultTemplate implements ITemplate {
    private final String lang;
    private final String title;
    private final String footer;

    private final VelocityEngine mEngine;

    public DefaultTemplate() {
        this.lang = System.getenv("html.default.lang");
        this.title = System.getenv("html.default.title");
        this.footer = System.getenv("html.default.footer");

        mEngine = new VelocityEngine();
        mEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        mEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        mEngine.setProperty("classpath.resource.loader.cache", true);
        mEngine.init();
    }

    public CharSequence renderDescr(@Nonnull IPost post, @Nonnull IPostService service) throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.renderDescr(post, out);
        final String str = out.toString();
        out.close();
        return str;
    }

    public CharSequence renderPost(@Nonnull IPost post, @Nonnull IPostService service) throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.render(post, out);
        final String str = out.toString();
        out.close();
        return str;
    }

    public String renderDate(long date) {
        return new Date(date).toString();
    }

    @Override
    public void renderList(@Nonnull List<IPost> posts, @Nonnull IPostService service, @Nonnull OutputStream stream) throws Exception {
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        final Template t = mEngine.getTemplate("list.vm");
        final VelocityContext ctx = new VelocityContext();
        ctx.put("lang", lang);
        ctx.put("title", title);
        ctx.put("footer", footer);
        ctx.put("posts", posts);
        ctx.put("utils", this);
        ctx.put("service", service);
        t.merge(ctx, writer);
        writer.close();
    }

    @Override
    public void renderPost(@Nonnull IPost post, @Nonnull IPostService service, @Nonnull OutputStream stream) throws Exception {
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        final Template t = mEngine.getTemplate("post.vm");
        final VelocityContext ctx = new VelocityContext();
        ctx.put("lang", lang);
        ctx.put("title", title);
        ctx.put("footer", footer);
        ctx.put("post", post);
        ctx.put("utils", this);
        ctx.put("service", service);
        t.merge(ctx, writer);
        writer.close();
    }
}
