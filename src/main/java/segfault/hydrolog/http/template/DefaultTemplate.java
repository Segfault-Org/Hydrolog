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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import static org.apache.velocity.runtime.RuntimeConstants.FILE_RESOURCE_LOADER_CACHE;
import static org.apache.velocity.runtime.RuntimeConstants.FILE_RESOURCE_LOADER_PATH;

public class DefaultTemplate implements ITemplate {
    private final VelocityContext mRootContext;

    private final VelocityEngine mEngine;

    public DefaultTemplate() {
        mRootContext = new VelocityContext();
        mRootContext.put("lang", System.getenv("html_default_lang"));
        mRootContext.put("title", System.getenv("html_default_title"));
        mRootContext.put("utils", StaticRenderingUtils.class);

        mEngine = new VelocityEngine();
        mEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file,classpath");
        mEngine.setProperty(FILE_RESOURCE_LOADER_PATH, System.getenv("html_default_override"));
        mEngine.setProperty(FILE_RESOURCE_LOADER_CACHE, true);
        mEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        mEngine.init();
    }

    @Override
    public void renderList(@Nonnull List<IPost> posts, @Nonnull IPostService service, @Nonnull OutputStream stream) throws Exception {
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        final Template t = mEngine.getTemplate("list.vm");
        final VelocityContext ctx = new VelocityContext(mRootContext);
        ctx.put("posts", posts);
        ctx.put("service", service);
        t.merge(ctx, writer);
        writer.close();
    }

    @Override
    public void renderPost(@Nonnull IPost post, @Nonnull IPostService service, @Nonnull OutputStream stream) throws Exception {
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        final Template t = mEngine.getTemplate("post.vm");
        final VelocityContext ctx = new VelocityContext(mRootContext);
        ctx.put("post", post);
        ctx.put("service", service);
        t.merge(ctx, writer);
        writer.close();
    }
}
