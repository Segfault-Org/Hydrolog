package segfault.hydrolog.posts.file;

import com.google.auto.value.AutoValue;
import org.apache.commons.io.FilenameUtils;
import segfault.hydrolog.posts.IPost;
import segfault.hydrolog.posts.IPostService;
import segfault.hydrolog.posts.renderers.PrebuiltRenderers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FilePostService implements IPostService {
    private final Logger logger = Logger.getLogger("FilePostService");

    private final Path mRoot;

    public FilePostService(@Nonnull Path root) {
        this.mRoot = root;
    }

    @Override
    @Nonnull
    public List<IPost> list(@Nonnull IPostService.ListArgs args) throws Exception {
        return Files.walk(mRoot)
                .map(Path::toFile)
                .map(file -> {
                    try {
                        return fileToPost(file);
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "Cannot scan file " + file.getName(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void render(@Nonnull IPost p, @Nonnull OutputStream out) throws Exception {
        final FilePost post = (FilePost) p;
        final FileReader reader = new FileReader(post.file());
        final String extension = FilenameUtils.getExtension(post.file().getName()).toLowerCase();

        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

        PrebuiltRenderers.getRenderer(extension).render(reader, writer);

        reader.close();
        writer.flush();
    }

    @Nullable
    @Override
    public IPost find(@Nonnull String path) throws Exception {
        final File file = new File(mRoot.toFile(), path);
        return fileToPost(file);
    }

    @Nullable
    private IPost fileToPost(@Nonnull File file) throws IOException {
        // The reason not to use isHidden() is that the directory test (see below) matches only '.'.
        if (!file.exists() || !file.isFile() || file.getName().startsWith(".") || !file.canRead()) return null;

        final String relativePath = file.getAbsolutePath().substring(mRoot.toUri().getPath().length());
        // Belonging in any hidden directory would result in hidden.
        if (relativePath.startsWith(".") || relativePath.matches("/\\.")) {
            return null;
        }
        final BufferedReader reader = new BufferedReader(new FileReader(file));
        final String descr = reader.readLine();
        reader.close();
        final BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        return FilePost.create((long) Files.getAttribute(file.toPath(), "unix:ino"),
                FilenameUtils.removeExtension(file.getName()),
                attr.creationTime().toMillis(),
                attr.lastModifiedTime().toMillis(),
                relativePath,
                descr,
                Files.getOwner(file.toPath()).getName(),
                file);
    }

    @Override
    public void renderDescr(@Nonnull IPost p, @Nonnull OutputStream out) throws Exception {
        final FilePost post = (FilePost) p;
        final Reader reader = new InputStreamReader(new ByteArrayInputStream(post.descr().getBytes()));
        final String extension = FilenameUtils.getExtension(post.file().getName()).toLowerCase();

        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

        PrebuiltRenderers.getRenderer(extension).render(reader, writer);

        reader.close();
        writer.flush();
    }


    @AutoValue
    public static abstract class ListArgs extends IPostService.ListArgs {
        @Nonnull
        public static ListArgs create() {
            return new AutoValue_FilePostService_ListArgs();
        }
    }
}
