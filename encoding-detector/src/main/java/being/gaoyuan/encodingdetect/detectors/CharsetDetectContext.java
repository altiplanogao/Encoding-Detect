package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.DetectSummary;
import being.gaoyuan.encodingdetect.utils.LineType;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;


public class CharsetDetectContext extends DetectContext{
    public static final int LF = '\n';
    public static final int CR = '\r';

    public final Charset charset;
    private int lfs = 0;
    private int crs = 0;
    private int unixLines = 0;//\n
    private int windowsLines = 0;//\r\n
    private int macLines = 0;//\r

    private List<String> lines = new ArrayList<>();
    private StringBuilder sb = null;

    private String contentHash = "";

    public CharsetDetectContext(Charset charset, Forbids forbids, int maxAllowed) {
        super(forbids, 0, maxAllowed);
        this.charset = charset;
    }

    @Override
    protected void preHandle() {
        if (sb == null) {
            sb = new StringBuilder();
        }
    }

    @Override
    protected boolean doHandle(final int value){
        switch (value) {
            case LF:
                lfs++;
                if (getLastValue() == CR) {
                    windowsLines++;
                    macLines--;
                } else {
                    commitLine();
                    unixLines++;
                }
                return true;
            case CR:
                commitLine();
                crs++;
                macLines++;
                return true;
            default:
                char ch = (char) value;
                sb.append(ch);
                return false;
        }
    }

    @Override
    protected boolean doCommit() {
        if (sb != null) {
            commitLine();
        }
        MessageDigest digest = DigestUtils.getSha1Digest();
        for (String line : lines) {
            digest.update(line.getBytes(StandardCharsets.UTF_8));
        }
        contentHash = Base64.getEncoder().encodeToString(digest.digest());
        return true;
    }

    private void commitLine() {
        lines.add(sb.toString());
        sb = null;
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

    public int getLineCount(LineType lineType) {
        switch (lineType) {
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

    public DetectSummary asSummary() {
        DetectSummary summary = new DetectSummary();
        summary.charset = this.charset;
        summary.lineType = this.getLineType();
        summary.lines = this.getLineCount(summary.lineType);
        summary.chars = this.getHandled();
        summary.maxChar = (char) this.getMax();
        boolean ok = !this.isBroken();
        summary.ok = ok;
        summary.brokenChar = ok ? null : (char)this.getBrokenValue();
        summary.contentHash = this.getContentHash();
        summary.content.addAll(this.getLines());
        return summary;
    }
}
