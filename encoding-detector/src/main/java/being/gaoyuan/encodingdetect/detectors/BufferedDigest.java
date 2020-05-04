package being.gaoyuan.encodingdetect.detectors;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

class BufferedDigest {
    private final MessageDigest digest;

    private final ByteBuffer buffer = ByteBuffer.allocate(4 * 1024);

    public BufferedDigest() {
        this.digest = DigestUtils.getSha1Digest();
    }

    private static byte[] intToBytes(final int data) {
        return new byte[] {
                (byte)((data >> 24) & 0xff),
                (byte)((data >> 16) & 0xff),
                (byte)((data >> 8) & 0xff),
                (byte)((data >> 0) & 0xff),
        };
    }

    public void update(final int input){
        byte[] bytes = intToBytes(input);
        int offset  = 0;
        for (int i = 0; i < bytes.length ; ++i){
            if (bytes[i]!=0){
                offset = i;
                break;
            }
        }
        update(bytes, offset);
    }

    private void update(byte[] bytes, int offset){
        digest.update(bytes, offset, bytes.length - offset);
    }

    public byte[] digest(){
        return digest.digest();
    }
}
