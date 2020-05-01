package being.gaoyuan;
//https://www.garykessler.net/library/file_sigs.html

public class FileType extends MagicNumbers {
    private final int magicNumberOffset;
    public final String extension;
    public final String description;

    public FileType(String hexMarks, String extension, String description) {
        this(0, hexMarks, extension, description);
    }

    public FileType(int magicNumberOffset, String hexMarks, String extension, String description) {
        super(hexMarks.replaceAll("\n", "")
                .replaceAll("\r", "")
                .replaceAll(" ", ""));
        this.magicNumberOffset = magicNumberOffset;
        this.extension = extension;
        this.description = description;
    }

//    public static final FileType
}
