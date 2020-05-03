package being.gaoyuan.encodingdetect.detectors;

import being.gaoyuan.encodingdetect.DetectSummary;
import being.gaoyuan.encodingdetect.EncodingGuesser;
import being.gaoyuan.encodingdetect.FileType;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class PreferredByExtensionEncodingDetector
        extends GeneralEncodingDetector {

    private final Map<String, Set<String>> encodingCache = new WeakHashMap<>();

    @Override
    public Optional<FileType> detect(File file) {
        final String ext = FilenameUtils.getExtension(file.getName()).toLowerCase();
        Set<String> cachedEncodings = encodingCache.computeIfAbsent(ext, x -> new HashSet<>());
        List<Charset> cachedCharsets = (cachedEncodings == null ? new ArrayList<String>() : cachedEncodings)
                .stream().map(encoding -> Charset.forName(encoding)).collect(Collectors.toList());
        if (!cachedCharsets.isEmpty()) {
            List<DetectSummary> summaryList = new ArrayList<>();
            for (Charset charset : cachedCharsets) {
                summaryList.add(tryFit(file, charset));
            }
            Optional<FileType> firstTry = EncodingGuesser.guess(summaryList);
            if (firstTry.isPresent()) {
                FileType fileType = firstTry.get();
                if (fileType.isText()) {
                    return firstTry;
                }
            }
        }

        {
            Set<Charset> candidates = new HashSet<>(CHARSETS.values());
            candidates.removeAll(cachedCharsets);

            List<DetectSummary> summaryList = new ArrayList<>();
            for (Charset charset : candidates) {
                summaryList.add(tryFit(file, charset));
            }
            Optional<FileType> secondTry = EncodingGuesser.guess(summaryList);
            if (secondTry.isPresent()) {
                final FileType fileType = secondTry.get();
                if (fileType.isBinary()) {
                    return secondTry;
                } else if (fileType.isText()) {
                    cachedEncodings.addAll(fileType.getPotentialEncodings());
                    return secondTry;
                } else {
                    throw new IllegalStateException();
                }
            }
        }
        return Optional.empty();
    }
}
