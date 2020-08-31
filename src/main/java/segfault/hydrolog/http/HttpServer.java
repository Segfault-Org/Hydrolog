package segfault.hydrolog.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import segfault.hydrolog.http.template.DefaultTemplate;
import segfault.hydrolog.http.template.ITemplate;
import segfault.hydrolog.posts.IPost;
import segfault.hydrolog.posts.IPostService;
import segfault.hydrolog.posts.file.FilePostService;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HttpServer implements HttpHandler {
    private final Logger logger = Logger.getLogger("HttpServer");

    private final com.sun.net.httpserver.HttpServer mServer;
    private ExecutorService mPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    private final ITemplate mTemplate;
    private final IPostService mService;

    public HttpServer(@Nonnull InetSocketAddress addr) throws IOException {
        mServer = com.sun.net.httpserver.HttpServer.create(addr, 0);
        mServer.setExecutor(mPool);
        mServer.createContext("/", this);

        mService = new FilePostService(new File(System.getenv("post_file_root")).toPath());

        mTemplate = new DefaultTemplate();
    }

    public void start() {
        mServer.start();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (!httpExchange.getRequestMethod().equals("GET")) {
            httpExchange.sendResponseHeaders(405, -1);
            return;
        }
        final OutputStream out = httpExchange.getResponseBody();

        try {
            if (httpExchange.getRequestURI().getPath().equals("/")) {
                httpExchange.sendResponseHeaders(200, 0);
                renderList(out);
                return;
            }
            final IPost post = mService.find(httpExchange.getRequestURI().getPath());
            if (post == null) {
                httpExchange.sendResponseHeaders(404, -1);
                return;
            }
            httpExchange.sendResponseHeaders(200, 0);
            renderPost(out, post);
        } catch (Exception e) {
            httpExchange.sendResponseHeaders(500, -1);
            logger.log(Level.SEVERE, "Cannot handle request", e);
        } finally {
            out.flush();
            out.close();
        }
    }

    private void renderList(@Nonnull OutputStream out) throws Exception {
        final List<IPost> list = mService.list(FilePostService.ListArgs.create());
        mTemplate.renderList(list
                .stream()
                .sorted((o, t1) -> {
                    // First -> Old
                    if (o.modified() > t1.modified()) return -1;
                    if (o.modified() < t1.modified()) return 1;
                    return 0;
                })
                .collect(Collectors.toList()), mService, out);
    }

    private void renderPost(@Nonnull OutputStream out, @Nonnull IPost post) throws Exception {
        mTemplate.renderPost(post, mService, out);
    }
}
