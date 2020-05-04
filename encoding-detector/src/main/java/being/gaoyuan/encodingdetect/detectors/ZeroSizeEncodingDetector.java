package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.BinaryType;
import being.gaoyuan.encodingdetect.EncodingDetector;
import being.gaoyuan.encodingdetect.FileType;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Optional;

public class ZeroSizeEncodingDetector implements EncodingDetector {
    private static final long TIMEOUT_MS = 10_000;

    @Override
    public Optional<FileType> detect(File file, Collection<Charset> attempt) {
        if (file.length() == 0) {
            final Path path = file.toPath();
            ByteBuffer buf = ByteBuffer.allocate(16);
            buf.clear();
            try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
                final long start = System.currentTimeMillis();

                while (true) {
                    if (System.currentTimeMillis() - start > TIMEOUT_MS) {
                        //timeout, maybe a device
                        return Optional.of(new FileType(BinaryType.UNKNOWN_BINARY, attempt.size()));
                    }
                    int got = channel.read(buf);
                    if (got < 0) {
                        //empty file
                        return Optional.empty();
                    } else if (got == 0) {
                        //maybe processing
                        Thread.sleep(100);
                    } else {
                        //unknown, maybe a device
                        return Optional.of(new FileType(BinaryType.UNKNOWN_BINARY, attempt.size()));
                    }
                }
            } catch (Throwable e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    //
//    @Override
//    public Optional<FileType> detect(File file, Collection<Charset> attempt) {
//        if (file.length() == 0) {
//            try (FileInputStream stream = new FileInputStream(file)) {
//                //may block
//                int b = stream.read();
//                if (b < 0) {
//                    //empty file
//                    return Optional.empty();
//                } else {
//                    //device
//                    return Optional.of(new FileType(BinaryType.UNKNOWN_BINARY, attempt.size()));
//                }
//            } catch (Throwable e) {
//                return Optional.empty();
//            }
//        } else {
//            return Optional.empty();
//        }
//    }
}