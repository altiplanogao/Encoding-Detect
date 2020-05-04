package being.gaoyuan;

import being.gaoyuan.encodingdetect.EncodingDetectorAgent;
import being.gaoyuan.encodingdetect.FileType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.*;

public class UserHomeDetectionTest {
    /**
     * Test if all files in user home can finish file type detection
     * The test may last for very long time
     *
     * @throws IOException
     */
    @Test
    public void test() throws IOException {
        Path root = Paths.get(System.getProperty("user.home"));
        Set<String> textExtensions = new HashSet<>();
        Set<String> binaryExtensions = new HashSet<>();
        EncodingDetectorAgent detector = EncodingDetectorAgent.createDefault();
        Files.walkFileTree(root, new FileVisitor<Path>() {
            private final long walkStart = System.currentTimeMillis();

            private void log(Path file, String info) {
                System.out.println(StringUtils.repeat(" ", depth) + "[" + info + "] " + file);
            }

            private int fileIndex = 0;
            private int depth = 0;

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                depth++;
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
                log(file, "REACH #" + fileIndex + " " + decimalFormat.format(theFile.length()));
                Optional<FileType> optionalFilType = detector.detect(theFile);
                if (optionalFilType.isPresent()) {
                    FileType fileType = optionalFilType.get();
                    (fileType.isText() ? textExtensions : binaryExtensions)
                            .add(FilenameUtils.getExtension(theFile.getName()));
                    double cost = 0.001 * (System.currentTimeMillis() - startTime);
                    double totalCost = 0.001 * (System.currentTimeMillis() - walkStart);
                    log(file, "" + fileType +
                            String.format(" %.2fs/%.2fs", cost, totalCost));
                    return FileVisitResult.CONTINUE;
                }
                throw new IllegalStateException("Unexpected call");
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                log(file, "Failed");
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
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
