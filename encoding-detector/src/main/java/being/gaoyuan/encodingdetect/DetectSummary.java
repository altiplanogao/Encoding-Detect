package being.gaoyuan.encodingdetect;

import being.gaoyuan.encodingdetect.utils.LineType;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DetectSummary {
    public Charset charset;
    public LineType lineType = LineType.NA;
    public int lines = 0;
    public int chars = 0;
    public char maxChar = 0;
    public Character brokenChar = null;
    public boolean ok = false;
    public String contentHash;
    public List<String> content = new ArrayList<>();

    public DetectSummary() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(charset).append(']')
                .append(ok ? "" : "FAILED")
                .append(" chars:" + chars)
                .append(" max:" + (char) maxChar)
                .append(" " + lineType + ":" + lines);
        if (!content.isEmpty()) {
            sb.append(" [CONTENT]:");
        }
        int i = 0;
        for (String line : content) {
            sb.append(line.trim());
            i++;
            if (i > 5) {
                sb.append("...");
                break;
            }
        }

        return sb.toString();
    }
}
