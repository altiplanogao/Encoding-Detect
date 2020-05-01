package being.gaoyuan;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DetectSummary {
    public Charset charset;
    public LineType lineType = LineType.NA;
    public int lines = 0;
    public int chars = 0;
    public char maxChar = 0;
    private Character brokenChar = null;
    public boolean ok = false;
    public String contentHash;
    public List<String> content = new ArrayList<>();

    public DetectSummary(DetectContext context) {
        charset = context.charset;
        lineType = context.getLineType();
        lines = context.getLineCount(lineType);
        chars = context.getChars();
        maxChar = (char)context.getMaxChar();
        ok = !context.isBroken();
        brokenChar = ok ? null : context.getBrokenChar();
        contentHash = context.getContentHash();
        content.addAll(context.getLines());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(charset).append(']')
                .append(ok ? "" : "FAILED")
                .append(" chars:" + chars)
                .append(" max:" + (char)maxChar)
                .append(" " + lineType + ":" + lines);
        if(!content.isEmpty()){
            sb.append( " [CONTENT]:");
        }
        int i = 0;
        for (String line : content){
            sb.append(line.trim());
            i++;
            if(i > 5){
                sb.append("...");
                break;
            }
        }

        return sb.toString();
    }
}
