# Encoding-Detect

Detect whether a file is binary or text.
Try to detect and guess the most possible encoding of a text-file.

## Main principle

1. Try to match the file with the magic-codes which we have.
2. Try to decode the file content using common charsets. [For example: UTF-8, ISO-8859-1, etc.]
3. Check whether the decoded content contains forbidden-code-points [For example: 0x00, '\b', 0xFFFD, 0xFFFF, etc.]

## Usage

    File file = ...
    EncodingDetectorAgent detector = EncodingDetectorAgent.createDefault();
    detector.detect(file).ifPresent(fileType -> {
        // fileType.isBinary();
        // fileType.isText();
        // fileType.getBinary();
        // fileType.getEncoding();
        // fileType.getPotentialEncodings();
    });
    
## Note

Reuse the EncodingDetectorAgent instance, it may run faster when history detection result exists. 
