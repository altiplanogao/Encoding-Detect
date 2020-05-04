package being.gaoyuan;

import being.gaoyuan.encodingdetect.BinaryTypeCollection;
import being.gaoyuan.encodingdetect.FileType;
import being.gaoyuan.encodingdetect.detectors.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
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
        Set<String> textExtensions = new HashSet<>();
        Set<String> binaryExtensions = new HashSet<>();
        EncodingDetectorAgent detector = new EncodingDetectorAgent();
        detector.addDetector(new BinaryFileDetector(
                new BinaryTypeCollection().loadPreDefines()))
                .addDetector(new XmlEncodingDetector())
                .addDetector(new PreferredEncodingDetector(
                        StandardCharsets.UTF_8,
                        StandardCharsets.ISO_8859_1))
                .addDetector(new BomEncodingDetector())
                .addDetector(new UnicodeReferencedEncodingDetector())
                .addDetector(new PreferredByExtensionEncodingDetector());
        Files.walkFileTree(root, new FileVisitor<Path>() {
            private final long walkStart = System.currentTimeMillis();
            private void log(Path file, String info) {
                System.out.println(StringUtils.repeat(" ", depth) + "[" + info + "] " + file);
            }
            private int fileIndex = 0;
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
//                {
//                    addAll(Arrays.asList(new String[]{
//                            "afm", "compositefont", "class",
//                            "java", "js", "jpg", "json", "jar",
//                            "kt",
//                            "lst", "log", "md",
//                            "otf",
//                            "png", "py", "pyc", "properties",
//                            "ttf", "txt",
//                            "yaml", "yml"}));
//                }
            };

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                fileIndex++;
                File theFile = file.toFile();
                long startTime = System.currentTimeMillis();
                String ext = FilenameUtils.getExtension(file.toFile().getName()).toLowerCase();
                if (KNOWN_EXTS.contains(ext)) {
                    return FileVisitResult.CONTINUE;
                }
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                decimalFormat.setGroupingUsed(true);
                decimalFormat.setGroupingSize(3);
                log(file, "REACH #"+fileIndex+" " + decimalFormat.format(theFile.length()));
                Optional<FileType> optionalFilType = detector.detect(theFile);
                if (optionalFilType.isPresent()) {
                    FileType fileType = optionalFilType.get();
                    (fileType.isText()? textExtensions : binaryExtensions)
                            .add(FilenameUtils.getExtension(theFile.getName()));
                    double cost = 0.001 * (System.currentTimeMillis() - startTime);
                    double totalCost = 0.001 * (System.currentTimeMillis() - walkStart);
                    log(file, "" + fileType +
                            String.format(" %.2fs/%.2fs", cost, totalCost));
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
        System.out.println("Text Extensions:");
        System.out.println(Arrays.toString(textExtensions.toArray()));
        System.out.println();
        System.out.println("Binary Extensions:");
        System.out.println(Arrays.toString(binaryExtensions.toArray()));
    }
}
