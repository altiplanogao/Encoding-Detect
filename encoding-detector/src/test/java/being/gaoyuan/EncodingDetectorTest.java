package being.gaoyuan;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.*;

public class EncodingDetectorTest {
    public static class ContentSet {
        private List<String> charsetList = new ArrayList<>();
        private int totalChars;
        private final List<String> lines;

        public ContentSet(List<String> lines) {
            this.lines = lines;
            totalChars = lines.stream().map(s -> s.length()).reduce(0, (a,b)->a+b);
        }

        public List<String> getCharsetList() {
            return charsetList;
        }
    }
    @Test
    public void test() throws IOException {
        Path root = Paths.get(System.getProperty("user.home"));
        EncodingDetector detector = new EncodingDetector();
        Files.walkFileTree(root, new FileVisitor<Path>() {
            private void log(Path file, String info){
                System.out.println("[" + info + "] " + StringUtils.repeat(" ", depth) + file);
            }
            private int depth = 0;
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                depth++;
//                log(dir, "Enter>>");
                return FileVisitResult.CONTINUE;
            }

            private final Set<String> KNOWN_EXTS = new HashSet<String>(){
                {
                    add("class");
                    add("java");
                    add("js");
                    add("json");
                    add("png");
                    add("py");
                    add("ttf");
                    add("otf");
                    add("properties");
                    add("txt");
                    add("yaml");
                    add("yml");
                    add("xml");
                }
            };
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String ext = FilenameUtils.getExtension(file.toFile().getName()).toLowerCase();
                if(KNOWN_EXTS.contains(ext)){
                    return FileVisitResult.CONTINUE;
                }
                Optional<FileType> fileType = FileTypes.checkType(file.toFile());
                if(fileType.isPresent()){
                    log(file, "-------" + fileType.get());
                    return FileVisitResult.CONTINUE;
                }

                FileEncoding fileEncoding = detector.detect(file);
                if(!fileEncoding.charsets.isEmpty()){
                    Map<String, String> charset2Sha1 = new HashMap<>();
                    Map<String, List<String>> sha1ToLines = new HashMap<>();
                    for (String charset : fileEncoding.charsets){
                        List<String> lines = EncodingDetector.readLines(file.toFile(), Charset.forName(charset));
                        MessageDigest digest = DigestUtils.getSha1Digest();
                        for (String line : lines){
                            digest.update(line.getBytes(StandardCharsets.UTF_8));
                        }
                        String base64 = Base64.getEncoder().encodeToString(digest.digest());
                        charset2Sha1.put(charset, base64);
                        sha1ToLines.put(base64, lines);
                    }
                    Map<String, ContentSet> sha1ToContent = new HashMap<>();
                    for (Map.Entry<String, List<String>> entry : sha1ToLines.entrySet()){
                        String sha1 = entry.getKey();
                        List<String> lines = entry.getValue();
                        sha1ToContent.put(sha1, new ContentSet(lines));
                    }
                    for(Map.Entry<String, String> entry : charset2Sha1.entrySet()){
                        sha1ToContent.get(entry.getValue()).getCharsetList().add(entry.getKey());
                    }
                    int x = 1;
                }
                if(fileEncoding.isText()) {
                    log(file, "-------" + fileEncoding.brief());
                }
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
