package being.gaoyuan;

import being.gaoyuan.encodingdetect.FileType;
import being.gaoyuan.encodingdetect.detectors.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class EncodingDetectorTest {
    public static class ContentSet {
        private List<String> charsetList = new ArrayList<>();
        private int totalChars;
        private final List<String> lines;

        public ContentSet(List<String> lines) {
            this.lines = lines;
            totalChars = lines.stream().map(s -> s.length()).reduce(0, (a, b) -> a + b);
        }

        public List<String> getCharsetList() {
            return charsetList;
        }
    }

    @Test
    public void test() throws IOException {
        Path root = Paths.get(System.getProperty("user.home"));
        EncodingDetectorAgent detector = new EncodingDetectorAgent();
        detector.addDetector(new BinaryFileDetector())
                .addDetector(new XmlEncodingDetector())
                .addDetector(new PreferredEncodingDetector(
                        StandardCharsets.UTF_8,
                        StandardCharsets.ISO_8859_1))
                .addDetector(new WithBomEncodingDetector());
        Files.walkFileTree(root, new FileVisitor<Path>() {
            private void log(Path file, String info) {
                System.out.println("[" + info + "] " + StringUtils.repeat(" ", depth) + file);
            }

            private int depth = 0;

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.toFile().getName().endsWith(".git")) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                depth++;
//                log(dir, "Enter>>");
                return FileVisitResult.CONTINUE;
            }

            private final Set<String> KNOWN_EXTS = new HashSet<String>() {
                {
//                    add("afm");
//                    add("class");
//                    add("compositefont");
//                    add("kt");
//                    add("java");
//                    add("js");
//                    add("json");
//                    add("png");
//                    add("py");
//                    add("ttf");
//                    add("otf");
//                    add("md");
//                    add("properties");
//                    add("txt");
//                    add("yaml");
//                    add("yml");
//                    add("xml");
//                    add("lst");
//                    add("html");//?
//                    add("log");
//                    add("jar");
                }
            };

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String ext = FilenameUtils.getExtension(file.toFile().getName()).toLowerCase();
                if (KNOWN_EXTS.contains(ext)) {
                    return FileVisitResult.CONTINUE;
                }
                Optional<FileType> optionalFilType = detector.detect(file);
                if (optionalFilType.isPresent()) {
                    FileType fileEncoding = optionalFilType.get();
                    log(file, "-------" + fileEncoding);
                    return FileVisitResult.CONTINUE;
                }
                log(file, "UNKNOWN");

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                log(file, "Failed");
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                log(dir, "Leave<<");
                depth--;
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
