package segfault.hydrolog.posts.file;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import segfault.hydrolog.posts.IPost;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FilePostServiceTest {
    private File mTemp;
    private FilePostService mService;

    private final List<IPost> mDesiredPosts = new ArrayList<>(5);

    @Before
    public void setUp() throws Exception {
        final String name = System.getProperty("user.name");

        mTemp = new File(new File(System.getProperty("java.io.tmpdir")), "test-" + System.currentTimeMillis());
        mTemp.mkdir();
        mService = new FilePostService(mTemp.toPath());

        // Post 1
        final File post1 = new File(mTemp, "Hello World Post.html");
        post1.createNewFile();
        org.apache.commons.io.FileUtils.writeLines(post1,
                StandardCharsets.UTF_8.name(),
                Arrays.asList("<h1>Welcome to my blog</h1>",
                        "<p>Paragraph 1</p>"));
        final BasicFileAttributes attr = Files.readAttributes(post1.toPath(), BasicFileAttributes.class);
        mDesiredPosts.add(FilePost.create((long) Files.getAttribute(post1.toPath(), "unix:ino"),
                "Hello World Post",
                attr.creationTime().toMillis(),
                attr.lastModifiedTime().toMillis(),
                "Hello World Post.html",
                "<h1>Welcome to my blog</h1>",
                name,
                post1));

        // Post 2: Hidden
        final File post2 = new File(mTemp, ".Hello.html");
        post2.createNewFile();
        org.apache.commons.io.FileUtils.writeLines(post2,
                StandardCharsets.UTF_8.name(),
                Arrays.asList("<h1>The hidden garden</h1>",
                        "<p>Although this file exists on the server, it is not visible.</p>"));

        // Post 3: In directory, also CJK test
        final File subDir = new File(mTemp, "中文博客");
        subDir.mkdir();
        final File post3 = new File(subDir, "XX 入门教程.md");
        post3.createNewFile();
        org.apache.commons.io.FileUtils.writeLines(post3,
                StandardCharsets.UTF_8.name(),
                Arrays.asList("# XXXXX 中文教程",
                        "你知道吗？这个程序还可以读取 **中文** 哦（"));
        final BasicFileAttributes attr3 = Files.readAttributes(post3.toPath(), BasicFileAttributes.class);
        mDesiredPosts.add(FilePost.create((long) Files.getAttribute(post3.toPath(), "unix:ino"),
                "XX 入门教程",
                attr3.creationTime().toMillis(),
                attr3.lastModifiedTime().toMillis(),
                "中文博客/XX 入门教程.md",
                "# XXXXX 中文教程",
                name,
                post3));

        // Part 4: Hidden directory
        final File subDir1 = new File(mTemp, ".你看不到我");
        subDir1.mkdir();
        final File post4 = new File(subDir1, "Hello.html");
        post4.createNewFile();
        org.apache.commons.io.FileUtils.writeLines(post4,
                StandardCharsets.UTF_8.name(),
                Arrays.asList("<h1>The hidden garden</h1>",
                        "<p>Although this file exists on the server, it is not visible.</p>"));
        final File post5 = new File(subDir1, "Hello1.html");
        post5.createNewFile();
        org.apache.commons.io.FileUtils.writeLines(post5,
                StandardCharsets.UTF_8.name(),
                Arrays.asList("<h1>The hidden garden</h1>",
                        "<p>Although this file exists on the server, it is not visible.</p>"));

        // Part 5: Empty files
        final File post6 = new File(mTemp, "empty.md");
        post6.createNewFile();
        final BasicFileAttributes attr6 = Files.readAttributes(post6.toPath(), BasicFileAttributes.class);
        mDesiredPosts.add(FilePost.create((long) Files.getAttribute(post6.toPath(), "unix:ino"),
                "empty",
                attr6.creationTime().toMillis(),
                attr6.lastModifiedTime().toMillis(),
                "empty.md",
                null,
                name,
                post6));

        // Post 6: No read permissions
        final File post7 = new File(mTemp, "permission denied.html");
        post7.createNewFile();
        post7.setReadable(false);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(mTemp);
    }

    @Test
    public void list() throws Exception {
        final List<IPost> posts = mService.list(FilePostService.ListArgs.create());
        assertEquals("Verify list",
                CollectionUtils.getCardinalityMap(mDesiredPosts),
                CollectionUtils.getCardinalityMap(posts));
    }

    @Test
    public void render() throws Exception {
        final List<String> desired = new ArrayList<>(5);
        desired.add("<h1>Welcome to my blog</h1><p>Paragraph 1</p>");
        desired.add("<h1>XXXXX 中文教程</h1><p>你知道吗？这个程序还可以读取 <strong>中文</strong> 哦（</p>");
        desired.add("");

        final List<String> actual = mDesiredPosts.stream()
                .map(post -> {
                    try {
                        final ByteArrayOutputStream out = new ByteArrayOutputStream();
                        mService.render(post, out);
                        final InputStream in = new ByteArrayInputStream(out.toByteArray());
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        final String lines = reader.lines().collect(Collectors.joining());
                        reader.close();
                        in.close();
                        out.close();
                        return lines;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        assertEquals("Verify list",
                CollectionUtils.getCardinalityMap(desired),
                CollectionUtils.getCardinalityMap(actual));
    }

    @Test
    public void find() throws Exception {
        final IPost post1 = mDesiredPosts.get(0);
        assertEquals(post1, mService.find("Hello World Post.html"));

        assertNull(mService.find(".Hello.html"));

        final IPost post3 = mDesiredPosts.get(1);
        assertEquals(post3, mService.find("中文博客/XX 入门教程.md"));
    }
}