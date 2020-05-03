package being.gaoyuan.encodingdetect;
//https://www.garykessler.net/library/file_sigs.html

import being.gaoyuan.encodingdetect.utils.MagicNumbers;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class BinaryType extends MagicNumbers {
    private final int magicNumberOffset;
    private final String extensionsText;
    private final Collection<String> extensions;
    public final String description;

    public static final BinaryType UNKNOWN_BINARY = new BinaryType();

    private BinaryType() {
        this.magicNumberOffset = -1;
        this.extensionsText = null;
        this.extensions = null;
        this.description = null;
    }

    public BinaryType(String extensionsText, String hexMarks, String description) {
        this(extensionsText, 0, hexMarks, description);
    }

    public BinaryType(String extensionsText, int magicNumberOffset, String hexMarks, String description) {
        super(hexMarks.replaceAll("\n", "")
                .replaceAll("\r", "")
                .replaceAll(" ", ""));
        if (magicNumberOffset < 0) {
            throw new IllegalArgumentException();
        }
        this.magicNumberOffset = magicNumberOffset;
        this.extensionsText = extensionsText;
        this.extensions = Collections.unmodifiableList(
                Arrays.asList(this.extensionsText.split("\\,")));
        if (this.extensions.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.description = description;
    }

    public Collection<String> getExtensions() {
        return extensions;
    }

    @Override
    protected boolean match(File file) {
        if (magicNumberOffset >= 0) {
            return super.match(file, magicNumberOffset);
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryType that = (BinaryType) o;
        return magicNumberOffset == that.magicNumberOffset &&
                Objects.equals(extensionsText, that.extensionsText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(magicNumberOffset, extensionsText);
    }

    @Override
    public String toString() {
        if (StringUtils.isNotEmpty(extensionsText)) {
            if (StringUtils.isEmpty(description)) {
                return "*." + extensionsText;
            } else {
                return "*." + extensionsText + ": " + description;
            }
        } else {
            if (StringUtils.isEmpty(description)) {
                return "UNKNOWN";
            } else {
                return "()" + extensionsText + ": " + description;
            }
        }
    }
}
