package being.gaoyuan;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public class DetectContext {
    public static final int LF = '\n';
    public static final int CR = '\r';

    public final Charset charset;
    private final Set<Integer> forbidChars;
    private int chars = 0;
    private int maxChar = 0;
    private int lfs = 0;
    private int crs = 0;
    private int lastChar = -1;
    private boolean broken = false;

    private int unixLines = 0;//\n
    private int windowsLines = 0;//\r\n
    private int macLines = 0;//\r

    private List<String> lines = new ArrayList<>();
//    private StringBuilder raw = new StringBuilder();
    private StringBuilder sb = null;

    private boolean committed = false;
    private String contentHash = "";

    public DetectContext(Charset charset, Set<Integer> forbidChars) {
        this.charset = charset;
        this.forbidChars = forbidChars;
    }

    public void push(final int chr) {
        chars++;
        if (sb == null) {
            sb = new StringBuilder();
        }
        char ch = (char)chr;
//        raw.append(ch);
        switch (chr) {
            case LF:
                lfs++;
                if (lastChar == CR) {
                    windowsLines++;
                    macLines--;
                } else {
                    commitLine();
                    unixLines++;
                }
                break;
            case CR:
                commitLine();
                crs++;
                macLines++;
                break;
            default:
                if (forbidChars.contains(chr)) {
                    broken = true;
                    return;
                }
                sb.append(ch);
        }
        if (maxChar < chr) {
            maxChar = chr;
        }
        lastChar = chr;
    }

    private void commitLine() {
        lines.add(sb.toString());
        sb = null;
    }

    void setBroken(boolean broken) {
        this.broken = broken;
    }

    public boolean isBroken() {
        return broken;
    }

    public void commit() {
        if (committed) {
            throw new IllegalStateException();
        } else {
            if (sb != null) {
                commitLine();
            }
            MessageDigest digest = DigestUtils.getSha1Digest();
            for(String line : lines){
                digest.update(line.getBytes(StandardCharsets.UTF_8));
            }
            contentHash = Base64.getEncoder().encodeToString(digest.digest());
            committed = true;
        }
    }

    public LineType getLineType() {
        int allTypeEnds = unixLines + windowsLines + macLines;
        if (allTypeEnds == 0) {
            return LineType.NA;
        }
        if (windowsLines >= unixLines) {
            if (windowsLines >= macLines) {
                return LineType.WIN;
            } else if (unixLines >= macLines) {
                return LineType.UNIX;
            } else {
                return LineType.MAC;
            }
        } else if (unixLines >= macLines) {
            return LineType.UNIX;
        } else {
            return LineType.MAC;
        }
    }

    public int getLineCount(LineType lineType){
        switch (lineType){
            case MAC:
                return macLines;
            case WIN:
                return windowsLines;
            case UNIX:
                return unixLines;
            case NA:
            default:
                return 0;
        }
    }

    public List<String> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public String getContentHash() {
        return contentHash;
    }

    public int getChars() {
        return chars;
    }

    public int getMaxChar() {
        return maxChar;
    }
}
