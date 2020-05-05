package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.utils.LineType;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.*;

class Summary implements Comparable<Summary> {
    public final LineType lineType;
    public final int lines;
    public final int chars;
    public final char maxChar;
    public final Character brokenChar;
    public final boolean ok;
    public final String contentHash;
    public final List<String> content;
    private final String contentContacted;

    public Summary(LineType lineType,
                   int lines,
                   int chars, char maxChar,
                   boolean ok, Character brokenChar,
                   String contentHash,
                   Collection<String> content) {
        this.lineType = lineType;
        this.lines = lines;
        this.chars = chars;
        this.maxChar = maxChar;
        this.brokenChar = brokenChar;
        this.ok = ok;
        this.contentHash = contentHash;
        this.content = Collections.unmodifiableList(new ArrayList<>(content));
        StringBuilder sb = new StringBuilder();
        content.forEach(c -> sb.append(c));
        this.contentContacted = sb.toString();
    }

    public Summary(Summary other) {
        this.lineType = other.lineType;
        this.lines = other.lines;
        this.chars = other.chars;
        this.maxChar = other.maxChar;
        this.brokenChar = other.brokenChar;
        this.ok = other.ok;
        this.contentHash = other.contentHash;
        this.content = other.content;
        this.contentContacted = other.contentContacted;
    }

    @Override
    public int compareTo(Summary o) {
        int diff = StringUtils.compare(contentContacted, o.contentContacted);
        if (diff != 0) {
            return diff;
        }
        diff = Boolean.compare(ok, o.ok);
        if (diff != 0) {
            return diff;
        }
        diff = Character.compare(maxChar, o.maxChar);
        if (diff != 0) {
            return diff;
        }
        diff = Integer.compare(brokenChar == null ? -1 : brokenChar, o.brokenChar == null ? -1 : o.brokenChar);
        if (diff != 0) {
            return diff;
        }
        diff = Integer.compare(chars, o.chars);
        if (diff != 0) {
            return diff;
        }
        diff = Integer.compare(lines, o.lines);
        if (diff != 0) {
            return diff;
        }
        diff = Integer.compare(lineType.ordinal(), o.lineType.ordinal());
        return diff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Summary that = (Summary) o;
        return lines == that.lines &&
                chars == that.chars &&
                maxChar == that.maxChar &&
                ok == that.ok &&
                lineType == that.lineType &&
                Objects.equals(brokenChar, that.brokenChar) &&
                Objects.equals(contentHash, that.contentHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineType, lines, chars, maxChar, brokenChar, ok, contentHash);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ok ? "" : "FAILED")
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
        sb.append(" [HASH]:").append(contentHash);
        return sb.toString();
    }
}

class DetectSummary extends Summary {
    public final Charset charset;

    public DetectSummary(Charset charset, Summary other) {
        super(other);
        this.charset = charset;
    }

    @Override
    public String toString() {
        return "[" + charset +
                "]" + super.toString();
    }
}
