package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.FileType;
import being.gaoyuan.encodingdetect.utils.ForbidsSet;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.UnicodeSet;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UnicodeReferencedEncodingDetector extends AbstractEncodingDetector {
    private static class UnicodeForbids implements Forbids{
        private final UnicodeSet unicodeSet;

        public UnicodeForbids(Set<Integer> values) {
            unicodeSet = new UnicodeSet();
            for (int i : values) {
                unicodeSet.add(i);
            }
            unicodeSet.freeze();
        }

        @Override
        public boolean contains(int value) {
            return unicodeSet.contains(value);
        }
    }

    public static Forbids calcUnicodeForbids() {
        final Set<Integer> textFileForbids = new HashSet<>();
        for (int cp = UCharacter.MIN_VALUE; cp <= UCharacter.MAX_VALUE; cp++) {
            boolean forbid = false;
            if (UCharacter.isISOControl(cp)) {
                forbid = true;
            }
            if (!UCharacter.isLegal(cp)) {
                forbid = true;
            }

//            if(UCharacter.isSupplementary(cp)){
//                System.out.println(String.format("0x%02X", cp) + ":\"" + (char) cp + "\"");
//            }

            if (forbid) {
                textFileForbids.add(cp);
            }
        }
        textFileForbids.addAll(DetectorSettings.calcSimpleForbidsSet());
        textFileForbids.removeAll(DetectorSettings.CONTROL_EXCEPTS);
        return new ForbidsSet(textFileForbids);
    }

    static {
        DetectorSettings.setForbids(calcUnicodeForbids());
    }

    public UnicodeReferencedEncodingDetector() {
    }

    @Override
    public Optional<FileType> detect(File file, Collection<Charset> attempt) {
        //do nothing, except ensure that "appendForbiddenChars(calcUnicodeForbiddenChars());" called
        return Optional.empty();
    }
}
