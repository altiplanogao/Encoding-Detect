package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.utils.LineType;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;


public class CharsetDetectContext extends DetectContext {
    public static final int LF = '\n';
    public static final int CR = '\r';
    private static final int LINE_BOUND = 200;
    private static final int MAX_LINE_ALLOWED = 100;

    public final Charset charset;
    private int lfs = 0;
    private int crs = 0;
    private int unixLines = 0;//\n
    private int windowsLines = 0;//\r\n
    private int macLines = 0;//\r

    private List<String> lines = new ArrayList<>();
    private int extraLines = 0;
    private BoundedCharBuffer charBuffer = null;

    private final BufferedDigest digest;
    private String contentHash = "";

    public CharsetDetectContext(Charset charset, Forbids forbids, int minAllowed, int maxAllowed) {
        super(forbids, minAllowed, maxAllowed);
        this.charset = charset;
        this.digest = new BufferedDigest();
    }

    @Override
    protected void preHandle() {
        if (charBuffer == null) {
            charBuffer = new BoundedCharBuffer(LINE_BOUND);
        }
    }

    @Override
    protected boolean doHandle(final int value) {
        digest.update(value);
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
                charBuffer.append(ch);
                return false;
        }
    }

    @Override
    protected boolean doCommit() {
        if (charBuffer != null) {
            commitLine();
        }
        contentHash = Base64.getEncoder().encodeToString(digest.digest());
        return true;
    }

    private void commitLine() {
        if (lines.size() < MAX_LINE_ALLOWED) {
            lines.add(charBuffer.toString());
        } else {
            extraLines++;
        }
        charBuffer = null;
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
        boolean ok = !this.isBroken();
        Summary summary = new Summary(
                this.getLineType(),
                this.getLineCount(this.getLineType()),
                this.getHandled(), (char) this.getMax(),
                ok, ok ? null : (char) this.getBrokenValue(),
                this.getContentHash(),
                this.getLines());
        return new DetectSummary(charset, summary);
    }
}
